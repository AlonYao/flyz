package com.appublisher.quizbank.common.vip.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/9/7.
 */
public class VipExercisesResp {

    private int response_code;

    private List<ExercisesBean> exercises;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<ExercisesBean> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExercisesBean> exercises) {
        this.exercises = exercises;
    }

    public static class ExercisesBean {
        private int exercise_id;
        private int exercise_type;
        private String teacher_name;
        private String name;
        private String course_name;
        private String class_name;
        private String start_time;
        private String end_time;
        private int status;
        private String status_text;

        public int getExercise_id() {
            return exercise_id;
        }

        public void setExercise_id(int exercise_id) {
            this.exercise_id = exercise_id;
        }

        public int getExercise_type() {
            return exercise_type;
        }

        public void setExercise_type(int exercise_type) {
            this.exercise_type = exercise_type;
        }

        public String getTeacher_name() {
            return teacher_name;
        }

        public void setTeacher_name(String teacher_name) {
            this.teacher_name = teacher_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCourse_name() {
            return course_name;
        }

        public void setCourse_name(String course_name) {
            this.course_name = course_name;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
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
    }
}
