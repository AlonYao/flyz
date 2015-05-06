package com.appublisher.quizbank.model.netdata.notice;

import java.util.ArrayList;

/**
 * 系统通知接口回调 数据模型
 */
public class NoticeResp {

    int response_code;
    ArrayList<NoticeM> list;

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<NoticeM> getList() {
        return list;
    }
}
