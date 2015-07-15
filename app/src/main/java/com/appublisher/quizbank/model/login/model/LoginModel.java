package com.appublisher.quizbank.model.login.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.model.login.activity.LoginActivity;
import com.appublisher.quizbank.model.login.activity.MobileRegisterActivity;
import com.appublisher.quizbank.model.login.activity.RegisterSmsCodeActivity;
import com.appublisher.quizbank.model.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.DownloadAsyncTask;
import com.appublisher.quizbank.utils.FileMange;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 登录Activity Model
 */
public class LoginModel {

    private static LoginActivity mLoginActivity;
    public static int mPwdErrorCount;

    /**
     * 构造函数
     * @param activity  登录Activity
     */
    public LoginModel(LoginActivity activity) {
        mLoginActivity = activity;
    }

    /**
     * 加密字符串
     *
     * @param str  待加密的字符串
     * @param key  密钥
     * @return  加密后的字符串
     */
    public static String encrypt(String str, String key) {
        MessageDigest mdEnc;
        String md5;

        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(key.getBytes(), 0, key.length());

            md5 = new BigInteger(1, mdEnc.digest()).toString(16);
            while (md5.length() < 32) {
                md5 = "0"+md5;
            }

            int x = 0, strLen=str.length(), keyLen = md5.length();
            StringBuilder ch = new StringBuilder();
            for (int i = 0; i < strLen; i++) {
                if (x==keyLen) {
                    x = 0;
                }
                ch.append(md5.charAt(x));
                x++;
            }

            String result="";
            for (int i = 0; i < strLen; i++) {
                int asc1 = (int)(str.charAt(i)), asc2 = (int)(ch.charAt(i));
                result += Character.toString( (char)((asc1+asc2)%256) );
            }

            byte[] data;
            try {
                data = result.getBytes("ISO-8859-1");
                return Base64.encodeToString(data, Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
        }

        return "";
    }

    /**
     * 微信点击事件
     */
    public View.OnClickListener weixinOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginActivity.mController.doOauthVerify(mLoginActivity, SHARE_MEDIA.WEIXIN,
                new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        ProgressDialogManager.showProgressDialog(mLoginActivity, false);
                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mLoginActivity.mController.getPlatformInfo(
                                mLoginActivity, SHARE_MEDIA.WEIXIN,
                            new SocializeListeners.UMDataListener() {
                                @Override
                                public void onStart() {
                                    // 获取数据开始
                                }

                                @Override
                                public void onComplete(int status, Map<String, Object> info) {
                                    if(status == 200 && info != null){
                                        String login_id = (String) info.get("unionid");
                                        String nickname = (String) info.get("nickname");
                                        String avatar = (String) info.get("headimgurl");

                                        if (login_id == null) {
                                            ProgressDialogManager.closeProgressDialog();
                                            ToastManager.showToast(mLoginActivity, "登录失败");
                                            return;
                                        }

                                        mLoginActivity.mSocialLoginType = "WX";
                                        mLoginActivity.mRequest.socialLogin(
                                                ParamBuilder.socialLoginParams(
                                                    "2",
                                                    login_id,
                                                    nickname,
                                                    "",
                                                    avatar));
                                    }else{
                                        ProgressDialogManager.closeProgressDialog();
                                    }
                                }
                            });
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA share_media) {
                        ProgressDialogManager.closeProgressDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ProgressDialogManager.closeProgressDialog();
                    }
                });
        }
    };

    /**
     * 微博点击事件
     */
    public View.OnClickListener weiboOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginActivity.mController.doOauthVerify(mLoginActivity, SHARE_MEDIA.SINA,
                new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        ProgressDialogManager.showProgressDialog(mLoginActivity, false);
                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mLoginActivity.mController.getPlatformInfo(
                                mLoginActivity, SHARE_MEDIA.SINA,
                            new SocializeListeners.UMDataListener() {
                                @Override
                                public void onStart() {
                                    // 获取平台数据开始
                                }
                                @Override
                                public void onComplete(int status, Map<String, Object> info) {
                                    if(status == 200 && info != null){
                                        String login_id = info.get("uid").toString();
                                        String nickname = info.get("screen_name").toString();
                                        String avatar = info.get("profile_image_url").toString();

                                        mLoginActivity.mSocialLoginType = "WB";
                                        mLoginActivity.mRequest.socialLogin(
                                                ParamBuilder.socialLoginParams(
                                                    "1",
                                                    login_id,
                                                    nickname,
                                                    "",
                                                    avatar));
                                    }else{
                                        ProgressDialogManager.closeProgressDialog();
                                    }
                                }
                            });
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA share_media) {
                        ProgressDialogManager.closeProgressDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ProgressDialogManager.closeProgressDialog();
                    }
                });
        }
    };

    /**
     * 新建数据库（如果已存在则将该数据库设置为当前可操作的数据库）
     * @param databaseName 数据库名
     */
    public static void setDatabase(String databaseName, Context context) {
        if (Globals.db_initialize) {
            ActiveAndroid.dispose();
        }

        Configuration.Builder builder = new Configuration.Builder(context);
        builder.setDatabaseName(databaseName);
        builder.setDatabaseVersion(1);
        ActiveAndroid.initialize(builder.create());
        Globals.db_initialize = true;
    }

    /**
     * 检查是否是第三方登录用户
     */
    public static boolean checkIsSocialUser() {
        User user = UserDAO.findById();
        if (user != null) {
            Gson gson = new Gson();
            UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);
            if (userInfo != null) {
                String weixin = userInfo.getWeixin();
                String weibo = userInfo.getWeibo();
                if ((weibo != null && !weibo.equals("")) || (weixin != null && !weixin.equals(""))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取用户id
     * @return 用户id
     */
    public static String getUserId() {
        return Globals.sharedPreferences.getString("user_id", "");
    }

    /**
     * 获取用户Token
     * @return 用户Token
     */
    public static String getUserToken() {
        return Globals.sharedPreferences.getString("user_token", "");
    }

    /**
     * 获取用户手机号
     * @return 手机号
     */
    public static String getUserMobile() {
        User user = UserDAO.findById();
        if (user == null) return "";

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();

        UserInfoModel userInfo = Globals.gson.fromJson(user.user, UserInfoModel.class);
        if (userInfo == null) return "";

        return userInfo.getMobile_num() == null ? "" : userInfo.getMobile_num();
    }

    /**
     * 判断是否有考试项目
     * @return 是或否
     */
    public static boolean hasExamInfo() {
        User user = UserDAO.findById();
        if (user != null) {
            Gson gson = new Gson();
            UserExamInfoModel exam = gson.fromJson(user.exam, UserExamInfoModel.class);

            return exam != null && exam.getExam_id() != 0;
        }

        return false;
    }

    /**
     * 获取考试项目
     * @return 考试项目
     */
    public static UserExamInfoModel getExamInfo() {
        User user = UserDAO.findById();
        if (user != null) {
            Gson gson = new Gson();
            UserExamInfoModel exam = gson.fromJson(user.exam, UserExamInfoModel.class);

            if (exam != null && exam.getExam_id() != 0) {
                return exam;
            }
        }

        return null;
    }

    /**
     * 获取本地存储的用户信息
     * @return 用户信息模型
     */
    public static UserInfoModel getUserInfoM() {
        User user = UserDAO.findById();

        if (user == null) return null;

        Gson gson = GsonManager.initGson();
        return gson.fromJson(user.user, UserInfoModel.class);
    }

    /**
     * 判断是否登录
     * @return 是或否
     */
    public static boolean isLogin() {
        return Globals.sharedPreferences.getBoolean("is_login", false);
    }

    /**
     * 执行登录
     * @param activity Activity
     */
    public static void setLogout(Activity activity) {
        // 提交登出信息至服务器
        new Request(activity).userLogout();

        // 清空本地数据
        cleanLocalData();

        // 跳转至登录页面
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 清空本地数据
     */
    @SuppressLint("CommitPrefEdits")
    public static void cleanLocalData() {
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString("user_id", "");
        editor.putString("user_token", "");
        editor.putBoolean("is_login", false);
        editor.commit();
    }

    /**
     * 处理预约公开课手机号验证部分的回调
     * @param activity BindingMobileActivity
     * @param response 回调数据
     */
    public static void dealBookOpenCourse(BindingMobileActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        LoginResponseModel loginResp = gson.fromJson(response.toString(), LoginResponseModel.class);

        if (loginResp != null && loginResp.getResponse_code() == 1) {
            // 获取原有用户的user_id
            UserInfoModel userInfo = getUserInfoM();
            String userId = null;

            if (userInfo != null) {
                userId = userInfo.getUser_id();
            }

            UserInfoModel userInfoOnline = loginResp.getUser();

            if (userInfoOnline == null || userInfoOnline.getUser_id() == null) return;

            if (userInfoOnline.getUser_id().equals(userId)) {
                // 手机号不存在，视为绑定手机号
                // 更新本地数据
                UserDAO.updateMobileNum(userInfoOnline.getMobile_num());

                if ("book_opencourse".equals(activity.mFrom)) {
                    // 预约公开课
                    Intent intent = new Intent(activity, OpenCourseUnstartActivity.class);
                    activity.setResult(ActivitySkipConstants.BOOK_OPENCOURSE, intent);
                    activity.finish();
                } else if ("opencourse_started".equals(activity.mFrom)) {
                    // Umeng
                    activity.mUmengIsCheckSuccess = true;

                    // 进入直播课页面
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("from", activity.mFrom);
                    intent.putExtra("content", activity.mOpenCourseId);
                    intent.putExtra("umeng_entry", activity.mUmengEntry);
                    intent.putExtra("umeng_timestamp", activity.mUmengTimestamp);
                    activity.startActivity(intent);
                    activity.finish();
                }

                // Umeng
                UmengManager.sendCountEvent(activity, "CodeVerified", "", "");

            } else {
                // 手机号存在，提示用户切换账号
                AlertManager.openCourseUserChangeAlert(activity);
            }

        } else {
            ToastManager.showToast(activity, "验证失败");
        }
    }

    /**
     * 设置头像
     * @param activity Activity
     * @param avatar 头像
     */
    public static void setAvatar(Activity activity, final RoundedImageView avatar) {
        String avatarFolder = activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/"
                + LoginModel.getUserId();
        FileMange.mkDir(avatarFolder);
        final String filePath = avatarFolder + "/avatar.png";
        File yourAvatarFile = new File(filePath);
        if (yourAvatarFile.exists()) {
            Bitmap avatarImg = BitmapFactory.decodeFile(filePath);
            if (avatarImg != null) {
                avatar.setImageBitmap(avatarImg);
            }
        } else {
            // 下载
            UserInfoModel userInfoModel = LoginModel.getUserInfoM();

            if (userInfoModel == null) return;

            String fileUrl = userInfoModel.getAvatar();
            if (fileUrl != null && !fileUrl.equals("")) {
                DownloadAsyncTask mDownloadAsyncTask = new DownloadAsyncTask(fileUrl, filePath,
                        new DownloadAsyncTask.FinishListener() {

                            @Override
                            public void onFinished() {
                                File file = new File(filePath);
                                if (file.exists()) {
                                    Bitmap avatarImg = BitmapFactory.decodeFile(filePath);
                                    if (avatarImg != null) {
                                        avatar.setImageBitmap(avatarImg);
                                    }
                                }
                            }
                        }, null);
                mDownloadAsyncTask.execute();
            }
        }
    }

    /**
     * 处理接口回调
     * @param response 回调数据
     * @param apiName 接口类别
     */
    public static void dealResp(JSONObject response, String apiName, LoginActivity activity) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();

        if ("is_user_exists".equals(apiName)) {
            IsUserExistsResp isUserExistsResp =
                    Globals.gson.fromJson(response.toString(), IsUserExistsResp.class);
            dealIsUserExistsResp(isUserExistsResp, activity);

        } else if ("login".equals(apiName) || "social_login".equals(apiName)) {
            LoginResponseModel lrm =
                    Globals.gson.fromJson(response.toString(), LoginResponseModel.class);
            dealLoginResp(lrm, activity, apiName);

        } else {
            ProgressDialogManager.closeProgressDialog();
        }
    }

    /**
     * 处理登录接口回调
     * @param lrm 回调数据模型
     * @param activity LoginActivity
     * @param apiName 接口类别
     */
    private static void dealLoginResp(LoginResponseModel lrm,
                                      LoginActivity activity,
                                      String apiName) {
        if (lrm == null || lrm.getResponse_code() != 1) {
            if (mPwdErrorCount == 0) {
                ToastManager.showToast(activity, "密码不正确");
                mPwdErrorCount++;
            } else if (mPwdErrorCount == 1) {
                LoginModel.showForgetPwdAlert(activity);
            }

        } else {
            // 执行成功后的操作
            setLoginSuccess(lrm, activity, apiName);
        }

        ProgressDialogManager.closeProgressDialog();
    }

    /**
     * 处理用户是否存在接口 请求回调
     * @param isUserExistsResp 回调数据模型
     * @param activity LoginActivity
     */
    public static void dealIsUserExistsResp(IsUserExistsResp isUserExistsResp,
                                            LoginActivity activity) {
        if (isUserExistsResp == null || isUserExistsResp.getResponse_code() != 1) {
            ProgressDialogManager.closeProgressDialog();
            LoginModel.showUserNonentityAlert(activity);
            return;
        }

        if (isUserExistsResp.isUser_exists()) {
            activity.mRequest.login(
                    ParamBuilder.loginParams("0", activity.mUsername, "", activity.mPwdEncrypt));
        } else {
            // 用户不存在
            ProgressDialogManager.closeProgressDialog();
            LoginModel.showUserNonentityAlert(activity);
        }
    }

    /**
     * 显示用户不存在Alert
     * @param activity LoginActivity
     */
    private static void showUserNonentityAlert(final LoginActivity activity) {
        new AlertDialog.Builder(activity)
            .setMessage(R.string.login_alert_usernonentity_msg)
            .setTitle(R.string.alert_title)
            .setPositiveButton(R.string.login_alert_usernonentity_p,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        if (Utils.isEmail(activity.mUsername)) {
                            // 判断用户是否是邮箱用户
                            intent = new Intent(activity, MobileRegisterActivity.class);
                        } else {
                            // 非邮箱用户，暂时全部认定为手机号用户
                            activity.mRequest.getSmsCode(
                                    ParamBuilder.phoneNumParams(activity.mUsername, ""));

                            intent = new Intent(activity, RegisterSmsCodeActivity.class);
                            intent.putExtra("user_phone", activity.mUsername);
                            intent.putExtra("user_pwd", activity.mPwdEncrypt);
                        }

                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                })
            .setNegativeButton(R.string.login_alert_usernonentity_n,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
            .create().show();
    }

    /**
     * 显示忘记密码Alert
     * @param activity Activity
     */
    public static void showForgetPwdAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
            .setMessage(R.string.login_alert_forgetpwd_msg)
            .setTitle(R.string.alert_title)
            .setPositiveButton(R.string.login_alert_forgetpwd_p,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, MobileRegisterActivity.class);
                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                })
            .setNegativeButton(R.string.login_alert_forgetpwd_n,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
            .create().show();
    }

    /**
     * 设置登录成功后的操作
     * @param lrm 用户个人信息数据模型
     */
    @SuppressLint("CommitPrefEdits")
    private static void setLoginSuccess(LoginResponseModel lrm,
                                        LoginActivity activity,
                                        String apiName) {
        if (saveToLocal(lrm, activity)) {
            // 页面跳转
            activity.mHandler.sendEmptyMessage(LoginActivity.LOGIN_SUCCESS);

            // Umeng
            if ("login".equals(apiName)) {
                UmengManager.sendCountEvent(activity, "RegLog", "Action", "Login");
                UmengManager.sendCountEvent(activity, "Home", "Entry", "Launch");
            } else if ("social_login".equals(apiName)) {
                UmengManager.sendCountEvent(
                        activity, "RegLog", "Action", activity.mSocialLoginType);
                if (!lrm.isIs_new())
                    UmengManager.sendCountEvent(activity, "Home", "Entry", "Launch");
            }
        } else {
            ToastManager.showToast(activity, "登录失败");
        }
    }

    /**
     * 保存至本地
     * @param lrm 用户数据模型
     * @param activity Activity
     * @return 是否保存成功
     */
    @SuppressLint("CommitPrefEdits")
    public static boolean saveToLocal(LoginResponseModel lrm, Activity activity) {
        if (lrm == null || lrm.getResponse_code() != 1) return false;

        UserInfoModel uim = lrm.getUser();
        UserExamInfoModel ueim = lrm.getExam();

        if (uim.getUser_id() == null || uim.getUser_id().length() == 0) return false;

        // 新建或切换数据库
        LoginModel.setDatabase(uim.getUser_id(), activity);

        // 保存用户信息至数据库
        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        UserDAO.save(Globals.gson.toJson(uim), Globals.gson.toJson(ueim));

        // 本地缓存
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString("user_id", uim.getUser_id());
        editor.putString("user_token", uim.getUser_token());
        editor.putString("user_mobile", uim.getMobile_num());
        editor.putString("user_email", uim.getMail());
        editor.putBoolean("is_login", true);
        editor.commit();

        return true;
    }
}
