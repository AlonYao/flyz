package com.appublisher.quizbank.model.netdata.measure;

import java.io.Serializable;

/**
 * 用户做题答案数据模型
 */
public class AnswerM implements Serializable{

    int id;
    boolean is_right;
    String answer;
    int duration;
    boolean is_collected;

    /** 小班 START **/
    String submit_time;
    String image_url;
    String review_postil;
    int score;
    /** 小班 END **/

    public boolean is_right() {
        return is_right;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getReview_postil() {
        return review_postil;
    }

    public void setReview_postil(String review_postil) {
        this.review_postil = review_postil;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean is_collected() {
        return is_collected;
    }

    public void setIs_collected(boolean is_collected) {
        this.is_collected = is_collected;
    }

    public int getDuration() {
        return duration;
    }

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
