package com.appublisher.quizbank.common.login.model;

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
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.common.login.activity.BindingSmsCodeActivity;
import com.appublisher.quizbank.common.login.activity.EmailResetPwdActivity;
import com.appublisher.quizbank.common.login.activity.ForceBindingMobileActivity;
import com.appublisher.quizbank.common.login.activity.LoginActivity;
import com.appublisher.quizbank.common.login.activity.MobileRegisterActivity;
import com.appublisher.quizbank.common.login.activity.MobileResetPwdActivity;
import com.appublisher.quizbank.common.login.activity.RegisterSmsCodeActivity;
import com.appublisher.quizbank.common.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.common.login.model.netdata.LoginResponseModel;
import com.appublisher.quizbank.common.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.common.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.DownloadAsyncTask;
import com.appublisher.quizbank.utils.FileManager;
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

    private static String mLoginId;
    private static String mNickName;
    private static String mAvatar;

    /**
     * 构造函数
     *
     * @param activity 登录Activity
     */
    public LoginModel(LoginActivity activity) {
        mLoginActivity = activity;
    }

    /**
     * 加密字符串
     *
     * @param str 待加密的字符串
     * @param key 密钥
     * @return 加密后的字符串
     */
    public static String encrypt(String str, String key) {
        MessageDigest mdEnc;
        String md5;

        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(key.getBytes(), 0, key.length());

            md5 = new BigInteger(1, mdEnc.digest()).toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }

            int x = 0, strLen = str.length(), keyLen = md5.length();
            StringBuilder ch = new StringBuilder();
            for (int i = 0; i < strLen; i++) {
                if (x == keyLen) {
                    x = 0;
                }
                ch.append(md5.charAt(x));
                x++;
            }

            String result = "";
            for (int i = 0; i < strLen; i++) {
                int asc1 = (int) (str.charAt(i)), asc2 = (int) (ch.charAt(i));
                result += Character.toString((char) ((asc1 + asc2) % 256));
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
                            ProgressDialogManager.showProgressDialog(mLoginActivity, false);
                            mLoginActivity.mController.getPlatformInfo(
                                    mLoginActivity, SHARE_MEDIA.WEIXIN,
                                    new SocializeListeners.UMDataListener() {
                                        @Override
                                        public void onStart() {
                                            // 获取数据开始
                                        }

                                        @Override
                                        public void onComplete(int status,
                                                               Map<String, Object> info) {
                                            if (status == 200 && info != null) {
                                                mLoginId = (String) info.get("unionid");
                                                mNickName = (String) info.get("nickname");
                                                mAvatar = (String) info.get("headimgurl");
                                                mLoginActivity.mSocialLoginType = "WX";
                                                mLoginActivity.mRequest.isUserExists(
                                                        mLoginId, "weixin");
                                            } else {
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
                            ProgressDialogManager.showProgressDialog(mLoginActivity, false);
                            mLoginActivity.mController.getPlatformInfo(
                                    mLoginActivity, SHARE_MEDIA.SINA,
                                    new SocializeListeners.UMDataListener() {
                                        @Override
                                        public void onStart() {
                                            // 获取平台数据开始
                                        }

                                        @Override
                                        public void onComplete(int status,
                                                               Map<String, Object> info) {
                                            if (status == 200 && info != null) {
                                                mLoginId = info.get("uid").toString();
                                                mNickName = info.get("screen_name").toString();
                                                mAvatar = info.get("profile_image_url").toString();
                                                mLoginActivity.mSocialLoginType = "WB";
                                                mLoginActivity.mRequest.isUserExists(
                                                        mLoginId, "weibo");
                                            } else {
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
     *
     * @param databaseName 数据库名
     */
    public static void setDatabase(String databaseName, Context context) {
        if (Globals.db_initialize) {
            ActiveAndroid.dispose();
        }

        Configuration.Builder builder = new Configuration.Builder(context);
        builder.setDatabaseName(databaseName);
        builder.setDatabaseVersion(5);
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
     *
     * @return 用户id
     */
    public static String getUserId() {
        return Globals.sharedPreferences.getString("user_id", "");
    }

    /**
     * 获取用户Token
     *
     * @return 用户Token
     */
    public static String getUserToken() {
        return Globals.sharedPreferences.getString("user_token", "");
    }

    /**
     * 获取用户手机号
     *
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
     * 获取用户学号
     *
     * @return 学号
     */
    public static String getSno() {
        User user = UserDAO.findById();
        if (user == null) return "";

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();

        UserInfoModel userInfo = Globals.gson.fromJson(user.user, UserInfoModel.class);
        if (userInfo == null) return "";

        return String.valueOf(userInfo.getSno());
    }

    /**
     * 判断是否有考试项目
     *
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
     *
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
     * 更新用户考试项目
     */
    public static void updateUserExam(String exam) {
        UserDAO.updateExamInfo(exam);
    }

    /**
     * 获取考试项目名称
     *
     * @return 考试项目名称
     */
    public static String getUserExamName() {
        User user = UserDAO.findById();
        if (user == null) return "";

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        UserExamInfoModel exam = Globals.gson.fromJson(user.exam, UserExamInfoModel.class);
        if (exam == null) return "";

        return exam.getName();
    }

    /**
     * 获取考试项目id
     *
     * @return 考试项目id
     */
    public static int getUserExamId() {
        User user = UserDAO.findById();
        if (user == null) return 0;

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        UserExamInfoModel exam = Globals.gson.fromJson(user.exam, UserExamInfoModel.class);
        if (exam == null) return 0;

        return exam.getExam_id();
    }

    /**
     * 获取本地存储的用户信息
     *
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
     *
     * @return 是或否
     */
    public static boolean isLogin() {
        return Globals.sharedPreferences.getBoolean("is_login", false);
    }

    /**
     * 执行登录
     *
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
     * 处理公开课手机号验证部分的回调
     *
     * @param activity BindingSmsCodeActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseResp(BindingSmsCodeActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        LoginResponseModel loginResp = gson.fromJson(response.toString(), LoginResponseModel.class);

        if (loginResp != null && loginResp.getResponse_code() == 1) {
            // 获取原有用户的user_id
            String userId = LoginModel.getUserId();

            UserInfoModel userInfoOnline = loginResp.getUser();
            if (userInfoOnline == null || userInfoOnline.getUser_id() == null) return;

            if (userInfoOnline.getUser_id().equals(userId)) {
                // 绑定手机号,更新本地数据
                UserDAO.updateMobileNum(userInfoOnline.getMobile_num());

                if ("book_opencourse".equals(activity.mFrom)) {
                    // 预约公开课
                    Intent intent = new Intent(activity, BindingMobileActivity.class);
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

                } else if ("opencourse_pre".equals(activity.mFrom)) {
                    // 公开课回放
                    Intent intent = new Intent(activity, BindingMobileActivity.class);
                    activity.setResult(ActivitySkipConstants.OPENCOURSE_PRE, intent);
                    activity.finish();
                } else if ("mock_openopencourse".equals(activity.mFrom)) {
                    Intent intent = new Intent(activity, BindingMobileActivity.class);
                    activity.setResult(ActivitySkipConstants.BOOK_MOCK_RESULT, intent);
                    activity.finish();
                }

                // Umeng
                UmengManager.sendCountEvent(
                        activity, "CodeVerified", "CodeVerified", "CodeVerified");

            } else {
                if ("mock_openopencourse".equals(activity.mFrom)) {
                    //给用户预约课
                    int mock_id = activity.getIntent().getIntExtra("mock_id", 0);
                    activity.mRequest.mobileBookMock(ParamBuilder.getBookMock(mock_id + ""), userInfoOnline.getUser_id(), userInfoOnline.getUser_token());
                } else {
                    // 手机号存在，提示用户切换账号
                    AlertManager.openCourseUserChangeAlert(activity);
                }
            }

        } else {
            ToastManager.showToast(activity, "验证失败");
        }
    }

    /**
     * 设置头像
     *
     * @param activity Activity
     * @param avatar   头像
     */
    public static void setAvatar(Activity activity, final RoundedImageView avatar) {
        String avatarFolder = activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/"
                + LoginModel.getUserId();
        FileManager.mkDir(avatarFolder);
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
     *
     * @param response 回调数据
     * @param apiName  接口类别
     */
    public static void dealResp(JSONObject response, String apiName, LoginActivity activity) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        switch (apiName) {
            case "is_user_exists":
                IsUserExistsResp isUserExistsResp =
                        GsonManager.getGson().fromJson(response.toString(), IsUserExistsResp.class);
                dealIsUserExistsResp(isUserExistsResp, activity);
                break;

            case "is_user_exists_oauth":
                // 检查第三方登录用户是否是新用户
                isUserExistsResp =
                        GsonManager.getGson().fromJson(response.toString(), IsUserExistsResp.class);
                dealCheckOAuthUserResp(isUserExistsResp);
                break;

            case "social_login":
            case "login":
                LoginResponseModel lrm =
                        GsonManager.getGson().fromJson(response.toString(), LoginResponseModel.class);
                dealLoginResp(lrm, activity, apiName);
                break;

            default:
                ProgressDialogManager.closeProgressDialog();
                break;
        }
    }

    /**
     * 处理登录接口回调
     *
     * @param lrm      回调数据模型
     * @param activity LoginActivity
     * @param apiName  接口类别
     */
    private static void dealLoginResp(LoginResponseModel lrm,
                                      LoginActivity activity,
                                      String apiName) {
        if (lrm == null || lrm.getResponse_code() != 1) {
            if (mPwdErrorCount == 0) {
                ToastManager.showToast(activity, "密码不正确");
                mPwdErrorCount++;
            } else if (mPwdErrorCount == 1) {
                LoginModel.showForgetPwdAlert(activity, activity.mUsername);
            }

        } else {
            // 执行成功后的操作
            setLoginSuccess(lrm, activity, apiName);
        }

        ProgressDialogManager.closeProgressDialog();
    }

    /**
     * 处理用户是否存在接口 请求回调
     *
     * @param isUserExistsResp 回调数据模型
     * @param activity         LoginActivity
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
     * 处理检查第三方登录用户是否是新用户
     * @param resp 用户检查回调
     */
    public static void dealCheckOAuthUserResp(IsUserExistsResp resp) {
        if (resp == null || resp.getResponse_code() != 1 || mLoginId == null) {
            ProgressDialogManager.closeProgressDialog();
            ToastManager.showToast(mLoginActivity, "登录失败");
            return;
        }

        if (resp.isUser_exists()) {
            // 老用户直接执行登录
            String type;
            if ("WB".equals(mLoginActivity.mSocialLoginType)) {
                // 微博
                type = "1";
            } else {
                // 微信
                type = "2";
            }

            mLoginActivity.mRequest.socialLogin(
                    ParamBuilder.socialLoginParams(
                            type,
                            mLoginId,
                            mNickName,
                            "",
                            mAvatar));
        } else {
            // 新用户跳转至强制绑定手机页面
            Intent intent = new Intent(mLoginActivity, ForceBindingMobileActivity.class);
            intent.putExtra("is_new", true);
            mLoginActivity.startActivity(intent);
            ProgressDialogManager.closeProgressDialog();

            // 本地记录第三方登录id，用于后续手机号的绑定
            SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
            if ("WB".equals(mLoginActivity.mSocialLoginType)) {
                editor.putString("user_wb_id", mLoginId);
            } else {
                editor.putString("user_wx_id", mLoginId);
            }
            editor.commit();
        }
    }

    /**
     * 显示用户不存在Alert
     *
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
                                    if (activity.mPwd.length() < 6 || activity.mPwd.length() > 16) {
                                        ToastManager.showToast(activity, "密码长度为6-16位");
                                        dialog.dismiss();
                                        return;
                                    }

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
     *
     * @param activity Activity
     */
    public static void showForgetPwdAlert(final Activity activity, final String userName) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.login_alert_forgetpwd_msg)
                .setTitle(R.string.alert_title)
                .setPositiveButton(R.string.login_alert_forgetpwd_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (userName == null || userName.length() == 0) return;

                                if (Utils.isEmail(userName)) {
                                    // 邮箱用户
                                    new Request(activity).resetPassword(userName);
                                    Intent intent = new Intent(activity, EmailResetPwdActivity.class);
                                    intent.putExtra("user_email", userName);
                                    activity.startActivity(intent);

                                } else {
                                    // 手机号用户
                                    new Request(activity).getSmsCode(
                                            ParamBuilder.phoneNumParams(userName, "resetPswd"));
                                    Intent intent = new Intent(activity, MobileResetPwdActivity.class);
                                    intent.putExtra("user_phone", userName);
                                    activity.startActivity(intent);
                                }

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
     *
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
     *
     * @param lrm      用户数据模型
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
        editor.putString("user_wb_id", uim.getWeibo());
        editor.putString("user_wx_id", uim.getWeixin());
        editor.putBoolean("is_login", true);
        editor.commit();

        return true;
    }

    /**
     * 获取之前的用户名
     *
     * @return 用户名
     */
    public static String getPreLoginName() {
        String mobile = Globals.sharedPreferences.getString("user_mobile", "");
        String email = Globals.sharedPreferences.getString("user_email", "");

        if (!"".equals(mobile)) {
            return mobile;
        } else if (!"".equals(email)) {
            return email;
        }

        return "";
    }

    /**
     * 检查上次第三方登录的类型
     *
     * @param activity LoginActivity
     */
    public static void checkPreOAuthLoginType(LoginActivity activity) {
        String preWeiboId = Globals.sharedPreferences.getString("user_wb_id", "");
        String preWeiXinId = Globals.sharedPreferences.getString("user_wx_id", "");
        String preUserEmail = Globals.sharedPreferences.getString("user_email", "");
        String preUserMobile = Globals.sharedPreferences.getString("user_mobile", "");

        if (!"".equals(preUserEmail) || !"".equals(preUserMobile)) return;

        if (!"".equals(preWeiXinId)) {
            // 微信用户
            activity.mIvWeixinPre.setVisibility(View.VISIBLE);

        } else if (!"".equals(preWeiboId)) {
            // 微博用户
            activity.mIvWeiboPre.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检查手机绑定
     * @param resp 用户是否存在接口的回调
     * @param context 上下文
     * @param phone_num 手机号
     * @param is_new 是否是新用户
     */
    public static void checkMobileBinding(IsUserExistsResp resp,
                                          Context context,
                                          String phone_num,
                                          boolean is_new) {
        if (resp == null || resp.getResponse_code() != 1) return;

        if (!resp.isUser_exists()) {
            // 未注册，可以直接绑定
            bindingMobile(context, phone_num);

        } else {
            // 已注册，需要进一步判断绑定的社交账号
            if (is_new) {
                // 如果是社交新用户，需要检测手机号的社交账号绑定状态
                if (Globals.sharedPreferences.getString("user_wb_id", "").length() != 0) {
                    // 微博新用户
                    if (resp.getWeibo() == null || resp.getWeibo().length() == 0) {
                        // 该手机号没有绑定微博，直接绑定
                        bindingMobile(context, phone_num);
                    } else {
                        // 其他情况联系客服处理
                    }
                } else if (Globals.sharedPreferences.getString("user_wb_id", "").length() != 0) {
                    // 微信新用户
                    if (resp.getWeixin() == null || resp.getWeixin().length() == 0) {
                        // 该手机号没有绑定微信，直接绑定
                        bindingMobile(context, phone_num);
                    } else {
                        // 其他情况联系客服处理
                    }
                }
            } else {
                // 如果是社交老用户
            }
        }
    }

    /**
     * 绑定手机号
     * @param context 上下文
     * @param phone_num 手机号
     */
    private static void bindingMobile(Context context, String phone_num) {
        new Request(context, (RequestCallback) context).getSmsCode(
                ParamBuilder.phoneNumParams(phone_num, ""));

        Intent intent = new Intent(context, BindingSmsCodeActivity.class);
        intent.putExtra("user_phone", phone_num);
        context.startActivity(intent);
    }

    /**
     * 展示切换账号Alert
     * @param is_new 是否是新用户
     */
    private static void showChangeAccountAlert(boolean is_new) {
        if (is_new) {

        } else {

        }
    }
}
