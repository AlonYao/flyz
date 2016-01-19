package com.appublisher.quizbank.common.opencourse.netdata;

import java.io.Serializable;

/**
 * 未评价课堂
 */
public class OpenCourseUnrateClassItem implements Serializable{

    int class_id;
    int course_id;
    String course_name;
    String class_name;
    String lector;
    String start_time;
    String end_time;

    public int getClass_id() {
        return class_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public String getLector() {
        return lector;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getClass_name() {
        return class_name;
    }
}
