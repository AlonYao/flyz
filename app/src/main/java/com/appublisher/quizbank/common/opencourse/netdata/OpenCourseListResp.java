package com.appublisher.quizbank.common.opencourse.netdata;

import java.util.ArrayList;

/**
 * 公开课列表
 */
public class OpenCourseListResp {

    int response_code;
    ArrayList<OpenCourseListItem> courses;
    ArrayList<OpenCoursePlaybackItem> playbacks;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<OpenCourseListItem> getCourses() {
        return courses;
    }

    public ArrayList<OpenCoursePlaybackItem> getPlaybacks() {
        return playbacks;
    }
}
