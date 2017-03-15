package com.appublisher.quizbank.model.netdata.mock;

import java.util.List;

/**
 * Created by bihaitian on 15/9/18.
 */
public class MockPreResp {

    private int response_code;
    private List<String> description;
    private List<String> pride_info;
    private List<MockListBean> mock_list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<String> getPride_info() {
        return pride_info;
    }

    public void setPride_info(List<String> pride_info) {
        this.pride_info = pride_info;
    }

    public List<MockListBean> getMock_list() {
        return mock_list;
    }

    public void setMock_list(List<MockListBean> mock_list) {
        this.mock_list = mock_list;
    }

    public static class MockListBean {

        private int mock_id;
        private String name;
        private int course_id;
        private String description;
        private String start_time;
        private int duration;
        private boolean is_purchased;
        private int exercise_id;
        private boolean is_booked;
        private String status;
        private List<PapersBean> papers;
        private String course_url;

        public String getCourseUrl() {
            return course_url;
        }

        public void setCourse_url(String course_url) {
            this.course_url = course_url;
        }

        public int getMock_id() {
            return mock_id;
        }

        public void setMock_id(int mock_id) {
            this.mock_id = mock_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCourse_id() {
            return course_id;
        }

        public void setCourse_id(int course_id) {
            this.course_id = course_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public boolean isPurchased() {
            return is_purchased;
        }

        public void setIs_purchased(boolean is_purchased) {
            this.is_purchased = is_purchased;
        }

        public int getExercise_id() {
            return exercise_id;
        }

        public void setExercise_id(int exercise_id) {
            this.exercise_id = exercise_id;
        }

        public boolean isBooked() {
            return is_booked;
        }

        public void setIs_booked(boolean is_booked) {
            this.is_booked = is_booked;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<PapersBean> getPapers() {
            return papers;
        }

        public void setPapers(List<PapersBean> papers) {
            this.papers = papers;
        }

        public static class PapersBean {

            private int paper_id;
            private String paper_name;

            public int getPaper_id() {
                return paper_id;
            }

            public void setPaper_id(int paper_id) {
                this.paper_id = paper_id;
            }

            public String getPaper_name() {
                return paper_name;
            }

            public void setPaper_name(String paper_name) {
                this.paper_name = paper_name;
            }
        }
    }
}
