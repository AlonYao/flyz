package com.appublisher.quizbank.model.login.model;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.LoginActivity;
import com.appublisher.quizbank.model.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

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
                                            String login_id = (String) info.get("openid");
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
     * 将游客数据库更改为用户数据库
     * @param activity 当前Activity
     * @param user_id 用户id
     */
    public static void migrateGuestToUser(Activity activity, String user_id) {
        String guestDBPath = activity.getApplicationContext().getDatabasePath("guest").getAbsolutePath();

        File file = new File(guestDBPath);
        if (file.exists()) {
            // 如果存在游客数据库
            String userDBPath = file.getParentFile() + "/" + user_id;
            File newFile = new File(userDBPath);
            if (file.renameTo(newFile)) {
                LoginModel.setDatabase(user_id, activity);
            }
        } else {
            LoginModel.setDatabase(user_id, activity);
        }
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

            return exam != null && exam.getExam_id() != null;
        }

        return false;
    }

    /**
     * 判断是否登录
     * @return 是或否
     */
    public static boolean isLogin() {
        return Globals.sharedPreferences.getBoolean("is_login", false);
    }
}
