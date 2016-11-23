package com.appublisher.quizbank.common.measure.bean;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureAnalysisBean {

    private List<MeasureQuestionBean> questions;
    private List<MeasureAnswerBean> answers;
    private List<MeasureCategoryBean> categorys;

    public List<MeasureCategoryBean> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<MeasureCategoryBean> categorys) {
        this.categorys = categorys;
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
