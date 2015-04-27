package com.appublisher.quizbank.model.netdata.historyexercise;

import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.util.ArrayList;

/**
 * 历史练习回调
 */
public class HistoryExerciseResp {

    int response_code;
    String status;
    int duration;
    int start_from;
    ArrayList<QuestionM> questions;
    ArrayList<AnswerM> answers;

    public int getResponse_code() {
        return response_code;
    }

    public String getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public int getStart_from() {
        return start_from;
    }

    public ArrayList<QuestionM> getQuestions() {
        return questions;
    }

    public ArrayList<AnswerM> getAnswers() {
        return answers;
    }
}