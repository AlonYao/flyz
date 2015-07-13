package com.appublisher.quizbank.model.login.model.netdata;

/**
 * 检查用户是否存在的接口回调模型
 */
public class IsUserExistsResp {

    int response_code;
    boolean user_exists;

    public int getResponse_code() {
        return response_code;
    }

    public boolean isUser_exists() {
        return user_exists;
    }
}
