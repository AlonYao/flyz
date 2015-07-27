package com.appublisher.quizbank.model.netdata.course;

/**
 * 评价模块开通课程 数据回调模型
 */
public class GradeCourseResp {

    int response_code;
    String jump_url;

    public int getResponse_code() {
        return response_code;
    }

    public String getJump_url() {
        return jump_url;
    }
}
