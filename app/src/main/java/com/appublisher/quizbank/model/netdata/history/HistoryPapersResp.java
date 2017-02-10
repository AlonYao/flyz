package com.appublisher.quizbank.model.netdata.history;

import com.appublisher.quizbank.common.interview.netdata.InterviewRecordListItemBean;

import java.util.ArrayList;

/**
 * 历史模考回调
 */
public class HistoryPapersResp {

    int response_code;
    ArrayList<InterviewRecordListItemBean> list;


    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<InterviewRecordListItemBean> getList() {
        return list;
    }

}
