package com.appublisher.quizbank.model.netdata.evaluation;

import java.util.ArrayList;

/**
 * 能力评估回调数据模型
 */
public class EvaluationResp {

    int response_code;
    int score;
    int rank;
    int learning_days;
    int total_time;
    int total_questions;
    int avarage_questions;
    float accuracy;
    float avarage_accuracy;
    ArrayList<Object> history_score;
    String summary_source;
    String calculation_basis;
    String summary_date;

    public int getResponse_code() {
        return response_code;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    public int getLearning_days() {
        return learning_days;
    }

    public int getTotal_time() {
        return total_time;
    }

    public int getTotal_questions() {
        return total_questions;
    }

    public int getAvarage_questions() {
        return avarage_questions;
    }

    public ArrayList<Object> getHistory_score() {
        return history_score;
    }

    public String getSummary_source() {
        return summary_source;
    }

    public String getCalculation_basis() {
        return calculation_basis;
    }

    public String getSummary_date() {
        return summary_date;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getAvarage_accuracy() {
        return avarage_accuracy;
    }
}
