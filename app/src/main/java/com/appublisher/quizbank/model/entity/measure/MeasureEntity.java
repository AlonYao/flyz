package com.appublisher.quizbank.model.entity.measure;

import com.appublisher.quizbank.model.netdata.historyexercise.ScoreM;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 做题模块实体类
 */
public class MeasureEntity implements Serializable{

    float defeat;
    int score;
    ArrayList<ScoreM> scores;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
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
