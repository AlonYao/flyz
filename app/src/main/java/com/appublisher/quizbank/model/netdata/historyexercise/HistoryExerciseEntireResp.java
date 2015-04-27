package com.appublisher.quizbank.model.netdata.historyexercise;

import com.appublisher.quizbank.model.netdata.measure.CategoryM;

import java.util.ArrayList;

/**
 * 练习历史回调(整卷)
 */
public class HistoryExerciseEntireResp {

    int response_code;
    String status;
    int duration;
    int start_from;
    ArrayList<CategoryM> category;

    public int getResponse_code() {
        return response_code;
    }

    public String getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public int getStart_from() {
        return start_from;
    }

    public ArrayList<CategoryM> getCategory() {
        return category;
    }
}
