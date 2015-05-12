package com.appublisher.quizbank.model.netdata.history;

/**
 * 学习历史数据模型
 */
public class HistoryPaperM {

    int paper_id;
    String paper_type;
    String name;
    float accuracy;
    String status;
    String action_time;

    public int getPaper_id() {
        return paper_id;
    }

    public String getPaper_type() {
        return paper_type;
    }

    public String getName() {
        return name;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getStatus() {
        return status;
    }

    public String getAction_time() {
        return action_time;
    }
}
