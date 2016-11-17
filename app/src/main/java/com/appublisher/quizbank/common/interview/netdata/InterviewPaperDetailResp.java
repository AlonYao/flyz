package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/11/17.
 */

public class InterviewPaperDetailResp {

    private int response_code;
    private List<QuestionsBean> questions;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class QuestionsBean {
        private int id;
        private String material;
        private String question;
        private String notes;
        private String analysis;
        private String keywords;
        private String from;
        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
