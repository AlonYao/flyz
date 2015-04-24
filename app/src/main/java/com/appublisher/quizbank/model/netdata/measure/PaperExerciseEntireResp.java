package com.appublisher.quizbank.model.netdata.measure;

import java.util.ArrayList;

/**
 * 整卷练习数据回调模型
 */
public class PaperExerciseEntireResp {

    int response_code;
    int paper_id;
    int duration;
    ArrayList<CategoryM> category;
    ArrayList<QuestionM> questions;

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

    public ArrayList<CategoryM> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<CategoryM> category) {
        this.category = category;
    }

    public ArrayList<QuestionM> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionM> questions) {
        this.questions = questions;
    }
}
