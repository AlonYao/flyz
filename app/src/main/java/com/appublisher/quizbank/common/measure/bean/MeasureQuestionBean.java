package com.appublisher.quizbank.common.measure.bean;

import java.util.List;

/**
 * 做题模块：题目模型
 */

public class MeasureQuestionBean {

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
    private int accuracy;
    private double summary_accuracy;
    private int summary_count;
    private String summary_fallible;
    private int material_id;
    private List<Integer> note_ids;
    private int question_id;
    private boolean is_desc;
    private int desc_position;
    private int question_order;
    private int question_amount;
    private int question_index;
    private boolean is_allright;

    public boolean is_allright() {
        return is_allright;
    }

    public void setIs_allright(boolean is_allright) {
        this.is_allright = is_allright;
    }

    public int getQuestion_index() {
        return question_index;
    }

    public void setQuestion_index(int question_index) {
        this.question_index = question_index;
    }

    public int getQuestion_order() {
        return question_order;
    }

    public void setQuestion_order(int question_order) {
        this.question_order = question_order;
    }

    public int getQuestion_amount() {
        return question_amount;
    }

    public void setQuestion_amount(int question_amount) {
        this.question_amount = question_amount;
    }

    public int getDesc_position() {
        return desc_position;
    }

    public void setDesc_position(int desc_position) {
        this.desc_position = desc_position;
    }

    public boolean is_desc() {
        return is_desc;
    }

    public void setIs_desc(boolean is_desc) {
        this.is_desc = is_desc;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterial() {
        return material == null ? "" : material;
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

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
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

    public List<Integer> getNote_ids() {
        return note_ids;
    }

    public void setNote_ids(List<Integer> note_ids) {
        this.note_ids = note_ids;
    }
}
