package com.appublisher.quizbank.model.netdata.opencourse;

/**
 * 公开课状态回调数据模型
 */
public class OpenCourseStatusResp {

    int response_code;
    int type;
    String content;
    String course_name;

    public int getResponse_code() {
        return response_code;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getCourse_name() {
        return course_name;
    }
}