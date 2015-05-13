package com.appublisher.quizbank.model.login.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.LoginActivity;
import com.appublisher.quizbank.model.login.activity.RegisterActivity;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 登录Activity Model
 */
public class LoginModel {

    private LoginActivity mActivity;

    /**
     * 构造函数
     * @param activity  登录Activity
     */
    public LoginModel(LoginActivity activity) {
        this.mActivity = activity;
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
            mActivity.mController.doOauthVerify(mActivity, SHARE_MEDIA.WEIXIN,
                new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        ProgressDialogManager.showProgressDialog(mActivity, false);
                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mActivity.mController.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN,
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
                                                ToastManager.showToast(mActivity, "登录失败");
                                                return;
                                            }

                                            mActivity.mSocialLoginType = "WX";
                                            mActivity.mRequest.socialLogin(ParamBuilder.socialLoginParams("2",
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
            mActivity.mController.doOauthVerify(mActivity, SHARE_MEDIA.SINA,
                new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        ProgressDialogManager.showProgressDialog(mActivity, false);
                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mActivity.mController.getPlatformInfo(mActivity, SHARE_MEDIA.SINA,
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

                                        mActivity.mSocialLoginType = "WB";
                                        mActivity.mRequest.socialLogin(ParamBuilder.socialLoginParams("1",
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
        if (Utils.isGuest()) {
            return Globals.sharedPreferences.getString("guest_id", "");
        } else {
            return Globals.sharedPreferences.getString("user_id", "");
        }
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
        editor.clear();
        editor.commit();
    }

    /**
     * 处理预约公开课手机号验证部分的回调
     * @param activity RegisterActivity
     * @param response 回调数据
     */
    public static void dealBookOpenCourse(RegisterActivity activity, JSONObject response) {
        if (response == null) return;

        Logger.i(response.toString());

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
                }

            } else {
                // 手机号存在，提示用户切换账号
                AlertManager.openCourseUserChangeAlert(activity);
            }

        } else {
            ToastManager.showToast(activity, "校验失败");
        }
    }
}
