package com.appublisher.quizbank.model.netdata.measure;

import java.util.ArrayList;

/**
 * 解析回调数据模型
 */
public class MeasureAnalysisResp {

    int response_code;
    ArrayList<QuestionM> questions;
    ArrayList<AnswerM> answers;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
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

    public void setAnswers(ArrayList<AnswerM> answers) {
        this.answers = answers;
    }
}
