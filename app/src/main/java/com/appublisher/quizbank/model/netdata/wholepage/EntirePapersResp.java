package com.appublisher.quizbank.model.netdata.wholepage;

import java.util.ArrayList;

/**
 * 整卷练习回调数据模型
 */
public class EntirePapersResp {

    int response_code;
    ArrayList<EntirePaperM> list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<EntirePaperM> getList() {
        return list;
    }

    public void setList(ArrayList<EntirePaperM> list) {
        this.list = list;
    }
}
