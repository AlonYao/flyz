package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;

/**
 * 课程标签回调
 */
public class FilterTagResp {

    int response_code;
    ArrayList<FilterTagM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<FilterTagM> getList() {
        return list;
    }
}
