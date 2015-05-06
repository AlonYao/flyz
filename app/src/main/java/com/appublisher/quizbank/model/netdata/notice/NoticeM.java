package com.appublisher.quizbank.model.netdata.notice;

/**
 * 系统通知item 数据模型
 */
public class NoticeM {

    int id;
    String type;
    String title;
    String content;
    String status;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }
}
