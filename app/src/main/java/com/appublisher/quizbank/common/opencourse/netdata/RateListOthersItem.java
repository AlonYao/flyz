package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 公开课模块：评价列表接口 others 字段
 */
public class RateListOthersItem {

    int id;
    String nickname;
    int score;
    String comment;
    String avatar;
    String rate_time;

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
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
