package com.appublisher.quizbank.model.netdata.homepage;

/**
 * 首页直播课数据模型
 */
public class LiveCourseM {
    int id;
    boolean started;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
