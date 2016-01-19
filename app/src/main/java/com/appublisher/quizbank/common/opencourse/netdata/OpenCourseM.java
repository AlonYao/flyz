package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 公开课详情 数据模型
 */
public class OpenCourseM {

    String name;
    String cover_pic;
    String lector;
    String start_time;
    String end_time;

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getName() {
        return name;
    }

    public String getCover_pic() {
        return cover_pic;
    }

    public String getLector() {
        return lector;
    }
}
