package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 获取公开课咨询轮询请求 回调数据模型
 */
public class OpenCourseConsultResp {

    int response_code;
    boolean alert_status;

    public int getResponse_code() {
        return response_code;
    }

    public boolean isAlert_status() {
        return alert_status;
    }
}
