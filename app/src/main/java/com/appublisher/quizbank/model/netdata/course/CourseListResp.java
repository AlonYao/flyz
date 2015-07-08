package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;

/**
 * 课程列表数据回调
 */
public class CourseListResp {

    int response_code;
    ArrayList<CourseM> courses;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<CourseM> getCourses() {
        return courses;
    }
}
