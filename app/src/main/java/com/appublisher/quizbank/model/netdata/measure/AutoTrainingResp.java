package com.appublisher.quizbank.model.netdata.measure;

import java.util.ArrayList;

/**
 * 快速智能练习数据模型
 */
public class AutoTrainingResp {
    int response_code;
    int duration;
    ArrayList<QuestionM> questions;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<QuestionM> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionM> questions) {
        this.questions = questions;
    }
}
