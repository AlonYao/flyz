package com.appublisher.quizbank.common.measure.netdata;

/**
 * 系统时间回调
 */
public class ServerCurrentTimeResp {

    private int response_code;
    private String current_time;

    public int getResponse_code() {
        return response_code;
    }

    public String getCurrent_time() {
        return current_time;
    }
}
