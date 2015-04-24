package com.appublisher.quizbank.model.netdata.historymokao;

import java.util.ArrayList;

/**
 * 历史模考回调数据模型
 */
public class HistoryMokaoResp {

    int response_code;
    ArrayList<HistoryMokaoM> paper_list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<HistoryMokaoM> getPaper_list() {
        return paper_list;
    }

    public void setPaper_list(ArrayList<HistoryMokaoM> paper_list) {
        this.paper_list = paper_list;
    }
}
