package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;

/**
 * Filter：课程地区回调数据
 */
public class FilterAreaResp {

    int response_code;
    ArrayList<FilterAreaM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<FilterAreaM> getList() {
        return list;
    }
}
