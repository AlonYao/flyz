package com.appublisher.quizbank.common.measure.netdata;

import java.util.List;

/**
 * Created by huaxiao on 2017/1/2.
 */

public class MeasureSearchResp {

    private int response_code;
    private int total;
    private List<String> keywords;
    private List<SearchItemBean> list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<SearchItemBean> getList() {
        return list;
    }

    public void setList(List<SearchItemBean> list) {
        this.list = list;
    }

    public static class SearchItemBean {
        private int id;
        private String material;
        private String question;
        private String option_a;
        private String option_b;
        private String option_c;
        private String option_d;
        private String answer;
        private String analysis;
        private int note_id;
        private String note_name;
        private int category_id;
        private String category_name;
        private String source;
        private double accuracy;
        private double summary_accuracy;
        private int summary_count;
        private String summary_fallible;
        private int material_id;
        private boolean is_collected;
        private String question_notes;
        private String type;
        private List<Integer> note_ids;
        private List<SearchItemBean> questions;

        public boolean is_collected() {
            return is_collected;
        }

        public List<SearchItemBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<SearchItemBean> questions) {
            this.questions = questions;
        }

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

        public String getOption_a() {
            return option_a;
        }

        public void setOption_a(String option_a) {
            this.option_a = option_a;
        }

        public String getOption_b() {
            return option_b;
        }

        public void setOption_b(String option_b) {
            this.option_b = option_b;
        }

        public String getOption_c() {
            return option_c;
        }

        public void setOption_c(String option_c) {
            this.option_c = option_c;
        }

        public String getOption_d() {
            return option_d;
        }

        public void setOption_d(String option_d) {
            this.option_d = option_d;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public int getNote_id() {
            return note_id;
        }

        public void setNote_id(int note_id) {
            this.note_id = note_id;
        }

        public String getNote_name() {
            return note_name;
        }

        public void setNote_name(String note_name) {
            this.note_name = note_name;
        }

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public double getSummary_accuracy() {
            return summary_accuracy;
        }

        public void setSummary_accuracy(double summary_accuracy) {
            this.summary_accuracy = summary_accuracy;
        }

        public int getSummary_count() {
            return summary_count;
        }

        public void setSummary_count(int summary_count) {
            this.summary_count = summary_count;
        }

        public String getSummary_fallible() {
            return summary_fallible;
        }

        public void setSummary_fallible(String summary_fallible) {
            this.summary_fallible = summary_fallible;
        }

        public int getMaterial_id() {
            return material_id;
        }

        public void setMaterial_id(int material_id) {
            this.material_id = material_id;
        }

        public boolean isIs_collected() {
            return is_collected;
        }

        public void setIs_collected(boolean is_collected) {
            this.is_collected = is_collected;
        }

        public String getQuestion_notes() {
            return question_notes;
        }

        public void setQuestion_notes(String question_notes) {
            this.question_notes = question_notes;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Integer> getNote_ids() {
            return note_ids;
        }

        public void setNote_ids(List<Integer> note_ids) {
            this.note_ids = note_ids;
        }
    }
}
