package com.appublisher.quizbank.model.netdata.measure;

/**
 * 提交试卷回调中的知识点变化数据模型
 */
public class NoteM {

    int id;
    String name;
    int from;
    int to;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
