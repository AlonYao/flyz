package com.appublisher.quizbank.model.netdata.history;

import java.util.ArrayList;

/**
 * 学习历史记录接口回调数据模型
 */
public class HistoryPapersResp {

    int response_code;
    ArrayList<HistoryPaperM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<HistoryPaperM> getList() {
        return list;
    }
}
