package com.appublisher.quizbank.model.netdata.historyexercise;

import java.io.Serializable;

/**
 * 往年分数线数据模型
 */
public class ScoreM implements Serializable{

    String name;
    int score;

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
