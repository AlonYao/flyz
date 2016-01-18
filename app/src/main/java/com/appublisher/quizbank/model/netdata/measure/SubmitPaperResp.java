package com.appublisher.quizbank.model.netdata.measure;

import com.appublisher.quizbank.model.netdata.historyexercise.ScoreM;

import java.util.ArrayList;

/**
 * 提交试卷回调 数据模型
 */
public class SubmitPaperResp {

    int response_code;
    ArrayList<NoteM> notes;
    float defeat;
    float score;
    ArrayList<ScoreM> scores;
    int exercise_id;
    float avg_score;

    public float getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(float avg_score) {
        this.avg_score = avg_score;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public float getScore() {
        return score;
    }

    public ArrayList<ScoreM> getScores() {
        return scores;
    }

    public float getDefeat() {
        return defeat;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<NoteM> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteM> notes) {
        this.notes = notes;
    }
}
