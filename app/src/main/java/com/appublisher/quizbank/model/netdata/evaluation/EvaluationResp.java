package com.appublisher.quizbank.model.netdata.evaluation;

import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;

import java.util.ArrayList;

/**
 * 能力评估回调数据模型
 */
public class EvaluationResp {

    int response_code;
    int score;
    float rank;
    int learning_days;
    int total_time;
    int total_questions;
    int avarage_questions;
    float accuracy;
    float avarage_accuracy;
    ArrayList<HistoryScoreM> history_score;
    String summary_source;
    String calculation_basis;
    String summary_date;
    ArrayList<HierarchyM> note_hierarchy;
    public int getResponse_code() {
        return response_code;
    }

    public int getScore() {
        return score;
    }

    public float getRank() {
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

    public ArrayList<HistoryScoreM> getHistory_score() {
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

    public ArrayList<HierarchyM> getNote_hierarchy() {
        return note_hierarchy;
    }

    public void setNote_hierarchy(ArrayList<HierarchyM> note_hierarchy) {
        this.note_hierarchy = note_hierarchy;
    }
}
