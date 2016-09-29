package com.appublisher.quizbank.common.vip.model;

/**
 * 小班提交作业实体类
 */
public class VipSubmitEntity {

    public int exercise_id;
    public int question_id;
    public String image_url;
    public int record_id;
    public String postil;
    public int level;
    public String answer_content;
    public int duration;
    public String summary;
    public int done;

    public VipSubmitEntity setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
        return this;
    }

    public VipSubmitEntity setQuestion_id(int question_id) {
        this.question_id = question_id;
        return this;
    }

    public VipSubmitEntity setImage_url(String image_url) {
        this.image_url = image_url;
        return this;
    }

    public VipSubmitEntity setRecord_id(int record_id) {
        this.record_id = record_id;
        return this;
    }

    public VipSubmitEntity setPostil(String postil) {
        this.postil = postil;
        return this;
    }

    public VipSubmitEntity setLevel(int level) {
        this.level = level;
        return this;
    }

    public VipSubmitEntity setAnswer_content(String answer_content) {
        this.answer_content = answer_content;
        return this;
    }

    public VipSubmitEntity setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public VipSubmitEntity setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public VipSubmitEntity setDone(int done) {
        this.done = done;
        return this;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public String getImage_url() {
        if (image_url == null) return "";
        return image_url;
    }

    public int getRecord_id() {
        return record_id;
    }

    public String getPostil() {
        if (postil == null) return "";
        return postil;
    }

    public int getLevel() {
        return level;
    }

    public String getAnswer_content() {
        if (answer_content == null) return "";
        return answer_content;
    }

    public int getDuration() {
        return duration;
    }

    public String getSummary() {
        if (summary == null) return "";
        return summary;
    }

    public int getDone() {
        return done;
    }
}
