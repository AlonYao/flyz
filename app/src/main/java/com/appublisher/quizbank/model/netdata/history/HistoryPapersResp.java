package com.appublisher.quizbank.model.netdata.history;

import java.util.ArrayList;

/**
 * ѧϰ��ʷ��¼�ӿڻص�����ģ��
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
