package com.appublisher.quizbank.model.netdata;

/**
 * 系统时间回调
 */
public class ServerCurrentTimeResp {

    int response_code;
    String current_time;

    public int getResponse_code() {
        return response_code;
    }

    public String getCurrent_time() {
        return current_time;
    }
}
