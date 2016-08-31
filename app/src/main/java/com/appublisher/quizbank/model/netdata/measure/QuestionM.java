package com.appublisher.quizbank.model.netdata.measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目数据模型
 */
public class QuestionM implements Serializable{
    int id;
    String material;
    String question;
    String option_a;
    String option_b;
    String option_c;
    String option_d;
    String answer;
    String analysis;
    int note_id;
    ArrayList<Integer> note_ids;
    String note_name;
    int category_id;
    String category_name;
    String source;
    float accuracy;
    float summary_accuracy;
    int summary_count;
    String summary_fallible;

    public ArrayList<Integer> getNote_ids() {
        return note_ids;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSummary_accuracy() {
        return summary_accuracy;
    }

    public int getSummary_count() {
        return summary_count;
    }

    public String getSummary_fallible() {
        return summary_fallible == null ? "" : summary_fallible;
    }
}
