package com.appublisher.quizbank.model.netdata.historyexercise;

import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
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
    ArrayList<CategoryM> category;
    ArrayList<NoteM> notes;
    float defeat;
    float score;
    ArrayList<ScoreM> scores;

    public float getScore() {
        return score;
    }

    public ArrayList<ScoreM> getScores() {
        return scores;
    }

    public float getDefeat() {
        return defeat;
    }

    public ArrayList<NoteM> getNotes() {
        return notes;
    }

    public ArrayList<CategoryM> getCategory() {
        return category;
    }

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
