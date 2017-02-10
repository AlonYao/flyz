package com.appublisher.quizbank.common.interview.netdata;

import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;

import java.util.ArrayList;


public class InterviewRecordListItemResp {

    int response_code;
    ArrayList<HistoryPaperM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<HistoryPaperM> getList() {
        return list;
    }
}
