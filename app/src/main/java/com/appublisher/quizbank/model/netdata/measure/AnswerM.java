package com.appublisher.quizbank.model.netdata.measure;

import java.io.Serializable;

/**
 * 用户做题答案数据模型
 */
public class AnswerM implements Serializable{

    int id;
    boolean is_right;
    String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIs_right() {
        return is_right;
    }

    public void setIs_right(boolean is_right) {
        this.is_right = is_right;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
