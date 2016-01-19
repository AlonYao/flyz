package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 公开课列表接口中courses字段item
 */
public class OpenCourseListItem {

    int id;
    String name;
    String date;
    String lector;
    boolean is_onair;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLector() {
        return lector;
    }

    public boolean is_onair() {
        return is_onair;
    }
}
