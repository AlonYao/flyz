package com.appublisher.quizbank.common.measure;

/**
 * 做题模块：用户答案实体类
 */

public class UserAnswerEntity {

    private int id;
    private String answer;
    private boolean is_right;
    private int category;
    private String category_name;
    private int note_id;
    private int duration;
    private String right_answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer == null ? "" : answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean is_right() {
        return is_right;
    }

    public void setIs_right(boolean is_right) {
        this.is_right = is_right;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }
}
