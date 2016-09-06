package com.appublisher.quizbank.common.vip.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/9/1.
 */
public class VipExerciseFilterResp {

    private int response_code;

    private List<StatusFilterBean> status_filter;

    private List<CategoryFilterBean> category_filter;

    private List<CourseBean> course;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<StatusFilterBean> getStatus_filter() {
        return status_filter;
    }

    public void setStatus_filter(List<StatusFilterBean> status_filter) {
        this.status_filter = status_filter;
    }

    public List<CategoryFilterBean> getCategory_filter() {
        return category_filter;
    }

    public void setCategory_filter(List<CategoryFilterBean> category_filter) {
        this.category_filter = category_filter;
    }

    public List<CourseBean> getCourse() {
        return course;
    }

    public void setCourse(List<CourseBean> course) {
        this.course = course;
    }

    public static class StatusFilterBean {
        private int status_id;
        private String status_name;

        public int getStatus_id() {
            return status_id;
        }

        public void setStatus_id(int status_id) {
            this.status_id = status_id;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }

    public static class CategoryFilterBean {
        private int category_id;
        private String category_name;
        private List<?> exercise_types;

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public List<?> getExercise_types() {
            return exercise_types;
        }

        public void setExercise_types(List<?> exercise_types) {
            this.exercise_types = exercise_types;
        }
    }

    public static class CourseBean {
        private int course_id;
        private String course_name;

        public int getCourse_id() {
            return course_id;
        }

        public void setCourse_id(int course_id) {
            this.course_id = course_id;
        }

        public String getCourse_name() {
            return course_name;
        }

        public void setCourse_name(String course_name) {
            this.course_name = course_name;
        }
    }
}
