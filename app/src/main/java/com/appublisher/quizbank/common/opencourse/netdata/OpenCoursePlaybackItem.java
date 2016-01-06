package com.appublisher.quizbank.common.opencourse.netdata;

/**
 * 公开课列表接口中playbacks字段item
 */
public class OpenCoursePlaybackItem {

    int id;
    String name;
    String lector;
    float score;
    int persons_num;
    int rate_num;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLector() {
        return lector;
    }

    public float getScore() {
        return score;
    }

    public int getPersons_num() {
        return persons_num;
    }

    public int getRate_num() {
        return rate_num;
    }
}
