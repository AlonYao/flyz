package com.appublisher.quizbank.model.entity.umeng;

/**
 * 友盟分享中的跳转链接 实体类
 */
public class UMShareUrlEntity {

    String type; // 种类：能力评估(evaluation)、练习报告(practice_report)、单题解析(measure_analysis)
    String user_id; // 用户id
    String user_token; // 用户token
    int exercise_id; // 试卷id
    String paper_type; // 练习类型：mini模考、快速智能练习等等
    String name; // 试卷名称
    int question_id; // 题目id

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getPaper_type() {
        return paper_type;
    }

    public void setPaper_type(String paper_type) {
        this.paper_type = paper_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
