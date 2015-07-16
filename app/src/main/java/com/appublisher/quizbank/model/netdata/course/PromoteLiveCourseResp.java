package com.appublisher.quizbank.model.netdata.course;

/**
 * 公告栏课程推广数据回调
 */
public class PromoteLiveCourseResp {

    int response_code;
    String display_type;
    String display_content;
    String target_type;
    String target_content;

    public int getResponse_code() {
        return response_code;
    }

    public String getDisplay_type() {
        return display_type;
    }

    public String getDisplay_content() {
        return display_content;
    }

    public String getTarget_type() {
        return target_type;
    }

    public String getTarget_content() {
        return target_content;
    }
}
