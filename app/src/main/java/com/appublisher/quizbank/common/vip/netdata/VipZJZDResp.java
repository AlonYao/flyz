package com.appublisher.quizbank.common.vip.netdata;

/**
 * 小班：字迹诊断 接口回调
 */
public class VipZJZDResp {


    private int response_code;
    private int exercise_id;
    private RemarkBean remark;
    private String exercise_name;
    private int exercise_type;
    private String end_time;
    private int duration;
    private int status;
    private String status_text;
    private boolean can_submit;
    private QuestionBean question;
    private UserAnswerBean user_answer;

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

    public RemarkBean getRemark() {
        return remark;
    }

    public void setRemark(RemarkBean remark) {
        this.remark = remark;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public QuestionBean getQuestion() {
        return question;
    }

    public void setQuestion(QuestionBean question) {
        this.question = question;
    }

    public UserAnswerBean getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(UserAnswerBean user_answer) {
        this.user_answer = user_answer;
    }

    public static class RemarkBean {
        private String cover_remark;
        private String content_remark;

        public String getCover_remark() {
            return cover_remark;
        }

        public void setCover_remark(String cover_remark) {
            this.cover_remark = cover_remark;
        }

        public String getContent_remark() {
            return content_remark;
        }

        public void setContent_remark(String content_remark) {
            this.content_remark = content_remark;
        }
    }

    public static class QuestionBean {
        private int question_id;
        private String content;
        private String image_url;

        public int getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(int question_id) {
            this.question_id = question_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }

    public static class UserAnswerBean {
        private String submit_time;
        private String image_url;
        private String review_postil;
        private int score;

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
    }
}
