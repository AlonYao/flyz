package com.appublisher.quizbank.common.measure.bean;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureCategoryBean {

    private int id;
    private String name;
    private List<MeasureQuestionBean> questions;
    private List<MeasureAnswerBean> answers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MeasureQuestionBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MeasureQuestionBean> questions) {
        this.questions = questions;
    }

    public List<MeasureAnswerBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<MeasureAnswerBean> answers) {
        this.answers = answers;
    }

}
