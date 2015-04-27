package com.appublisher.quizbank.model.netdata.measure;

import java.util.ArrayList;

/**
 * 整卷分类数据模型
 */
public class CategoryM {

    int id;
    String name;
    ArrayList<QuestionM> questions;
    ArrayList<AnswerM> answers;

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

    public ArrayList<QuestionM> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionM> questions) {
        this.questions = questions;
    }

    public ArrayList<AnswerM> getAnswers() {
        return answers;
    }
}
