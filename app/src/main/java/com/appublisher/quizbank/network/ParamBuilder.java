package com.appublisher.quizbank.network;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.utils.OpenUDID_manager;
import com.appublisher.quizbank.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder implements ApiConstants {

    /**
     * 拼接通用参数
     * @param url  拼接前的url
     * @return  拼接后的url
     */
    public static String finalUrl(String url) {
        StringBuilder finalUrl = new StringBuilder();

        // 参数说明
        // terminal_type  终端类别,目前包括iOS_phone/iOS_pad/android_phone/android_pad/pc,必需
        // app_type  应用的类别,目前landing_plan,必需
        // app_version  应用的版本号,必需
        // uuid	 唯一标识符,可以没有或null
        // user_id  用户id,可以没有或－1
        // user_token  用户授权token,和user_id关联校验用户授权,可为空
        // timestamp  客户端时间戳

        finalUrl.append(url)
                .append("?terminal_type=android_phone")
                .append("&app_type=quizbank")
                .append("&app_version=")
                .append(Globals.appVersion)
                .append("&uuid=")
                .append(OpenUDID_manager.getID() == null ? "" : OpenUDID_manager.getID())
                .append("&user_id=")
                .append(Globals.sharedPreferences.getString("user_id", ""))
                .append("&user_token=")
                .append(Globals.sharedPreferences.getString("user_token", ""))
                .append("&timestamp=")
                .append(System.currentTimeMillis());

        return finalUrl.toString();
    }

    /**
     * 用户登录参数
     * @param login_type  登录类型  0:手机/邮箱，1:新浪微博，2:微信，3:人人
     * @param login_id  登录的id  手机号/邮箱，或者是第三方的open_id
     * @param nickname  用户的昵称  第三方注册时对服务端有效，没有时传空
     * @param password  用户密码
     * @return params  用户登录参数
     */
    public static Map<String, String> loginParams(String login_type, String login_id,
                                                  String nickname, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("login_type", login_type);
        params.put("login_id", login_id);
        params.put("nickname", nickname);
        params.put("password", password);

        return params;
    }

    /**
     * 第三方用户登录参数
     * @param login_type  登录类型  0:手机/邮箱，1:新浪微博，2:微信，3:人人
     * @param login_id  登录的id  手机号/邮箱，或者是第三方的open_id
     * @param nickname  用户的昵称  第三方注册时对服务端有效，没有时传空
     * @param password  用户密码
     * @param avatar 用户头像
     * @return params  用户登录参数
     */
    public static Map<String, String> socialLoginParams(String login_type, String login_id,
                                                  String nickname, String password, String avatar) {
        Map<String, String> params = new HashMap<>();
        params.put("login_type", login_type);
        params.put("login_id", login_id);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);

        return params;
    }

    /**
     * 手机号信息
     * @param mobile_num  手机号
     * @return  手机号信息
     */
    public static Map<String, String> phoneNumParams(String mobile_num, String action) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_num", mobile_num);
        params.put("action", action);
        return params;
    }

    /**
     * 验证码校验参数
     * @param mobile_num  手机号
     * @param mobile_token  验证码
     * @return  参数
     */
    public static Map<String, String> checkSmsCodeParams(String mobile_num, String mobile_token) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_num", mobile_num);
        params.put("mobile_token", mobile_token);
        return params;
    }

    /**
     * 用户注册参数
     * @param mobile_num  手机号
     * @param password  密码
     * @return  用户注册参数
     */
    public static Map<String, String> register(String mobile_num, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_num", mobile_num);
        params.put("password", password);
        return params;
    }

    /**
     * 忘记密码参数
     * @param mobile_num  手机号
     * @param password  密码
     * @return  用户注册参数
     */
    public static Map<String, String> forgetPwd(String mobile_num, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_num", mobile_num);
        params.put("password", password);
        return params;
    }

    /**
     * 修改个人信息参数
     * @param info_type  类型：avatar,nickname
     * @param info_value  新的信息
     * @return  个人信息参数
     */
    public static Map<String, String> changeUserInfo(String info_type, String info_value) {
        Map<String, String> params = new HashMap<>();
        params.put("info_type", info_type);
        params.put("info_value", info_value);
        return params;
    }

    /**
     * 登录信息授权参数
     * @param auth_type  授权类型：0:手机，1:新浪微博，2:微信，3:人人
     * @param handle_type  授权行为的类别：delete:删除，add:新增，update:更新
     * @param auth_id  授权的id：手机号，或者是第三方的open_id
     * @return  参数
     */
    public static Map<String, String> authHandle(String auth_type, String handle_type,
                                                 String auth_id, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("auth_type", auth_type);
        params.put("handle_type", handle_type);
        params.put("auth_id", auth_id);
        params.put("password", password);

        return params;
    }

    /**
     * 修改密码参数
     * @param old_pswd  旧密码
     * @param new_pswd  新密码
     * @return  参数
     */
    public static Map<String, String> changePwd(String old_pswd, String new_pswd) {
        Map<String, String> params = new HashMap<>();
        params.put("old_pswd", old_pswd);
        params.put("new_pswd", new_pswd);
        return params;
    }

    /**
     * 又拍云上传头像地址生成
     * @return 地址
     */
    public static String upyunInterviewVideoPath() {
        String uid;
        if (Utils.isGuest()) {
            uid = Globals.sharedPreferences.getString("guest_id", "");
        } else {
            uid = Globals.sharedPreferences.getString("user_id", "");
        }

        return "/dp/avatar/" + uid + ".jpg";
    }

    /**
     * 提交试卷参数
     * @param paper_id 试卷id
     * @param paper_type 试卷类型
     * @param redo 是否为重新做题
     * @param duration 答题总时长（秒）
     * @param questions 序列化json，同上岸计划，额外增加每道题的时间
     * @param status 完成状态
     * @return  参数
     */
    public static Map<String, String> submitPaper(String paper_id, String paper_type,
                                                  String redo, String duration,
                                                  String questions, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("paper_id", paper_id);
        params.put("paper_type", paper_type);
        params.put("redo", redo);
        params.put("duration", duration);
        params.put("questions", questions);
        params.put("status", status);

        return params;
    }
}
