package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 公开课模块：评价列表 self字段
 */
public class RateListSelfItem {

    int id;
    int score;
    String comment;
    String avatar;
    String rate_time;

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRate_time() {
        return rate_time;
    }
}
