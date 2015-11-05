package com.appublisher.quizbank.common.login.model.netdata;

/**
 * 通用返回值
 */
public class CommonResponseModel {
    private int response_code;
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
}
