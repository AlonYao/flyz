package com.appublisher.quizbank.common.vip.netdata;

import java.util.ArrayList;

/**
 * 小班：互评提升
 */

public class VipHPTSResp {

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
    private Object summary;

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

    public Object getSummary() {
        return summary;
    }

    public void setSummary(Object summary) {
        this.summary = summary;
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
        private String notice;
        private String material;
        private String question;
        private String answer;

        public int getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(int question_id) {
            this.question_id = question_id;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    public static class UserAnswerBean {
        private UserRecordBean user_record;
        private MyPostilBean my_postil;

        public UserRecordBean getUser_record() {
            return user_record;
        }

        public void setUser_record(UserRecordBean user_record) {
            this.user_record = user_record;
        }

        public MyPostilBean getMy_postil() {
            return my_postil;
        }

        public void setMy_postil(MyPostilBean my_postil) {
            this.my_postil = my_postil;
        }

        public static class UserRecordBean {
            private int record_id;
            private UserInfoBean user_info;
            private String submit_time;
            private ArrayList<String> images;

            public int getRecord_id() {
                return record_id;
            }

            public void setRecord_id(int record_id) {
                this.record_id = record_id;
            }

            public UserInfoBean getUser_info() {
                return user_info;
            }

            public void setUser_info(UserInfoBean user_info) {
                this.user_info = user_info;
            }

            public String getSubmit_time() {
                return submit_time;
            }

            public void setSubmit_time(String submit_time) {
                this.submit_time = submit_time;
            }

            public ArrayList<String> getImages() {
                return images;
            }

            public void setImages(ArrayList<String> images) {
                this.images = images;
            }

            public static class UserInfoBean {
                private int user_id;
                private String nickname;
                private String avatar;

                public int getUser_id() {
                    return user_id;
                }

                public void setUser_id(int user_id) {
                    this.user_id = user_id;
                }

                public String getNickname() {
                    return nickname;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }
            }
        }

        public static class MyPostilBean {
            private String review_level;
            private String review_time;
            private String review_postil;

            public String getReview_level() {
                return review_level;
            }

            public void setReview_level(String review_level) {
                this.review_level = review_level;
            }

            public String getReview_time() {
                return review_time;
            }

            public void setReview_time(String review_time) {
                this.review_time = review_time;
            }

            public String getReview_postil() {
                return review_postil;
            }

            public void setReview_postil(String review_postil) {
                this.review_postil = review_postil;
            }
        }
    }
}
