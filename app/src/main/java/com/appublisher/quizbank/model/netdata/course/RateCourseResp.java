package com.appublisher.quizbank.model.netdata.course;

/**
 * 评论获取待赠送课程、开课数据回调模型
 */
public class RateCourseResp {

    int response_code;
    int course_id;
    String course_name;
    float course_price;

    public int getResponse_code() {
        return response_code;
    }

    public int getCourse_id() {
        return course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public float getCourse_price() {
        return course_price;
    }
}
