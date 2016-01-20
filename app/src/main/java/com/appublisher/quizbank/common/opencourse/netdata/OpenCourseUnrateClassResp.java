package com.appublisher.quizbank.common.opencourse.netdata;

import java.util.ArrayList;

/**
 * 未评价课程列表接口
 */
public class OpenCourseUnrateClassResp {

    int response_code;
    ArrayList<OpenCourseUnrateClassItem> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<OpenCourseUnrateClassItem> getList() {
        return list;
    }
}
