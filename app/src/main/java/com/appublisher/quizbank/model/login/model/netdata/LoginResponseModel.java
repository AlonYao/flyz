package com.appublisher.quizbank.model.login.model.netdata;

/**
 * 登录&注册 成功后的返回值
 */
public class LoginResponseModel {
    private int response_code;
    private boolean is_new;
    private UserInfoModel user;
    private UserExamInfoModel exam;
    private String response_msg;

    public String getResponse_msg() {
        return response_msg;
    }

    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public UserExamInfoModel getExam() {
        return exam;
    }

    public void setExam(UserExamInfoModel exam) {
        this.exam = exam;
    }

    public boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public UserInfoModel getUser() {
        return user;
    }

    public void setUser(UserInfoModel user) {
        this.user = user;
    }
}
