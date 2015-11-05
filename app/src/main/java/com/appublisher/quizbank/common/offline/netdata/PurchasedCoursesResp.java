package com.appublisher.quizbank.common.offline.netdata;

import java.util.ArrayList;

/**
 * 已购课程列表
 */
public class PurchasedCoursesResp {

    int response_code;
    ArrayList<PurchasedCourseM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<PurchasedCourseM> getList() {
        return list;
    }
}
