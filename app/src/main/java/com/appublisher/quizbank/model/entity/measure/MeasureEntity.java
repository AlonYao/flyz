package com.appublisher.quizbank.model.entity.measure;

import com.appublisher.quizbank.model.netdata.historyexercise.ScoreM;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 做题模块实体类
 */
public class MeasureEntity implements Serializable{

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

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public ArrayList<ScoreM> getScores() {
        return scores;
    }

    public void setScores(ArrayList<ScoreM> scores) {
        this.scores = scores;
    }

    public float getDefeat() {
        return defeat;
    }

    public void setDefeat(float defeat) {
        this.defeat = defeat;
    }
}
