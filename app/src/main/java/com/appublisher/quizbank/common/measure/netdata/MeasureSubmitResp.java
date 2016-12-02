package com.appublisher.quizbank.common.measure.netdata;

/**
 * 做题模块
 */

public class MeasureSubmitResp {

    private int response_code;
    private int exercise_id;

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }
}
