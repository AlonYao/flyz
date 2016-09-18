package com.appublisher.quizbank.common.vip.netdata;

import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.util.ArrayList;

/**
 * Created by jinbao on 2016/8/31.
 */
public class VipExerciseDetailCommonResp {

    private int response_code;
    private int exercise_id;
    private String exercise_name;
    private int exercise_type;
    private String end_time;
    private int duration;
    private int status;
    private String status_text;
    private boolean can_submit;
    private ArrayList<QuestionM> question;
    private String course_name;
    private String class_name;
    private ArrayList<AnswerM> user_answer;

    public ArrayList<AnswerM> getUser_answer() {
        return user_answer;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<QuestionM> getQuestion() {
        return question;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public int getExercise_type() {
        return exercise_type;
    }

    public void setExercise_type(int exercise_type) {
        this.exercise_type = exercise_type;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public boolean isCan_submit() {
        return can_submit;
    }

    public void setCan_submit(boolean can_submit) {
        this.can_submit = can_submit;
    }
}
