package com.appublisher.quizbank.common.measure.netdata;

import com.appublisher.quizbank.common.measure.MeasureQuestion;

import java.util.List;

/**
 * 做题模块：快速智能练习
 */

public class MeasureAutoResp {

    private int response_code;
    private int paper_id;
    private int duration;
    private List<MeasureQuestion> questions;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public int getPaper_id() {
        return paper_id;
    }

    public void setPaper_id(int paper_id) {
        this.paper_id = paper_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<MeasureQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MeasureQuestion> questions) {
        this.questions = questions;
    }
}
