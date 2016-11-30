package com.appublisher.quizbank.common.measure.netdata;

import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;

import java.util.List;

/**
 * 做题模块：整卷
 */

public class MeasureEntireResp {

    private int response_code;
    private int paper_id;
    private String paper_name;
    private int duration;
    private List<CategoryBean> category;
    private List<MeasureQuestionBean> questions;

    public List<MeasureQuestionBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MeasureQuestionBean> questions) {
        this.questions = questions;
    }

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

    public String getPaper_name() {
        return paper_name;
    }

    public void setPaper_name(String paper_name) {
        this.paper_name = paper_name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<CategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryBean> category) {
        this.category = category;
    }

    public static class CategoryBean {
        private int id;
        private String name;
        private List<MeasureQuestionBean> questions;
        private List<?> answers;

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

        public List<?> getAnswers() {
            return answers;
        }

        public void setAnswers(List<?> answers) {
            this.answers = answers;
        }
    }
}
