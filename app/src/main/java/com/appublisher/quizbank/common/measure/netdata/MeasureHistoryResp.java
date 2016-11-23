package com.appublisher.quizbank.common.measure.netdata;

import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureNotesBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureScoresBean;

import java.util.List;

/**
 * 做题模块：历史练习
 */

public class MeasureHistoryResp {

    private int response_code;
    private String exercise_name;
    private String status;
    private int duration;
    private int start_from;
    private double score;
    private double defeat;
    private double avg_score;
    private List<MeasureCategoryBean> category;
    private List<MeasureNotesBean> notes;
    private List<MeasureScoresBean> scores;
    private List<MeasureQuestionBean> questions;
    private List<MeasureAnswerBean> answers;

    public List<MeasureQuestionBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MeasureQuestionBean> questions) {
        this.questions = questions;
    }

    public List<MeasureAnswerBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<MeasureAnswerBean> answers) {
        this.answers = answers;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStart_from() {
        return start_from;
    }

    public void setStart_from(int start_from) {
        this.start_from = start_from;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getDefeat() {
        return defeat;
    }

    public void setDefeat(double defeat) {
        this.defeat = defeat;
    }

    public double getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(double avg_score) {
        this.avg_score = avg_score;
    }

    public List<MeasureCategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<MeasureCategoryBean> category) {
        this.category = category;
    }

    public List<MeasureNotesBean> getNotes() {
        return notes;
    }

    public void setNotes(List<MeasureNotesBean> notes) {
        this.notes = notes;
    }

    public List<MeasureScoresBean> getScores() {
        return scores;
    }

    public void setScores(List<MeasureScoresBean> scores) {
        this.scores = scores;
    }

}
