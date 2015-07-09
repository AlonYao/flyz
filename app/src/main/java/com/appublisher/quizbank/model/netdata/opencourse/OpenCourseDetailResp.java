package com.appublisher.quizbank.model.netdata.opencourse;

/**
 * 获取公开课详情回调 数据模型
 */
public class OpenCourseDetailResp {

    int response_code;
    OpenCourseM course;
    boolean booked;
    String staticCourseUrl;

    public int getResponse_code() {
        return response_code;
    }

    public OpenCourseM getCourse() {
        return course;
    }

    public boolean isBooked() {
        return booked;
    }

    public String getStaticCourseUrl() {
        return staticCourseUrl;
    }
}
