package com.appublisher.quizbank.common.login.model.netdata;

/**
 * 检测账号是否被合并
 */
public class IsUserMergedResp {

    int response_code;
    boolean is_merged;

    public int getResponse_code() {
        return response_code;
    }

    public boolean is_merged() {
        return is_merged;
    }
}
