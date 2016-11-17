package com.appublisher.quizbank.common.measure.bean;

import java.util.List;

/**
 * 做题模块：提交
 */

public class MeasureSubmitBean {

    private int id;
    private String answer;
    private int is_right; // 0 错 1 对
    private int category;
    private List<Integer> note_ids;
    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIs_right() {
        return is_right;
    }

    public void setIs_right(int is_right) {
        this.is_right = is_right;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public List<Integer> getNote_ids() {
        return note_ids;
    }

    public void setNote_ids(List<Integer> note_ids) {
        this.note_ids = note_ids;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
