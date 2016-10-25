package com.appublisher.quizbank.common.vip.netdata;

import java.util.ArrayList;
import java.util.List;

/**
 * 小班：行测
 */

public class VipXCResp {

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
    private SummaryBean summary;
    private String course_name;
    private String class_name;
    private ArrayList<QuestionBean> question;

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

    public SummaryBean getSummary() {
        return summary;
    }

    public void setSummary(SummaryBean summary) {
        this.summary = summary;
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

    public ArrayList<QuestionBean> getQuestion() {
        return question;
    }

    public void setQuestion(ArrayList<QuestionBean> question) {
        this.question = question;
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

    public static class SummaryBean {
        private int position;
        private int duration;
        private float accuracy;
        private List<NoteInfoBean> note_info;
        private ArrayList<CategoryInfoBean> category_info;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public float getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(float accuracy) {
            this.accuracy = accuracy;
        }

        public List<NoteInfoBean> getNote_info() {
            return note_info;
        }

        public void setNote_info(List<NoteInfoBean> note_info) {
            this.note_info = note_info;
        }

        public ArrayList<CategoryInfoBean> getCategory_info() {
            return category_info;
        }

        public void setCategory_info(ArrayList<CategoryInfoBean> category_info) {
            this.category_info = category_info;
        }

        public static class NoteInfoBean {
            private int id;
            private int total;
            private int right;
            private int duration;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getRight() {
                return right;
            }

            public void setRight(int right) {
                this.right = right;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class CategoryInfoBean {
            private int id;
            private String name;
            private int total;
            private int right;
            private int duration;
            private List<NotesBean> notes;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getRight() {
                return right;
            }

            public void setRight(int right) {
                this.right = right;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public List<NotesBean> getNotes() {
                return notes;
            }

            public void setNotes(List<NotesBean> notes) {
                this.notes = notes;
            }

            public static class NotesBean {
                private int id;
                private int total;
                private int right;
                private int duration;
                private String name;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getTotal() {
                    return total;
                }

                public void setTotal(int total) {
                    this.total = total;
                }

                public int getRight() {
                    return right;
                }

                public void setRight(int right) {
                    this.right = right;
                }

                public int getDuration() {
                    return duration;
                }

                public void setDuration(int duration) {
                    this.duration = duration;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }

    public static class QuestionBean {
        private String material;
        private String question;
        private String option_a;
        private String option_b;
        private String option_c;
        private String option_d;
        private String answer;
        private String analysis;
        private int note_id;
        private String note_name;
        private int category_id;
        private String category_name;
        private String source;
        private int accuracy;
        private float summary_accuracy;
        private int summary_count;
        private String summary_fallible;
        private int material_id;
        private int question_id;
        private UserAnswerBean user_answer;
        private List<Integer> note_ids;

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

        public String getOption_a() {
            return option_a;
        }

        public void setOption_a(String option_a) {
            this.option_a = option_a;
        }

        public String getOption_b() {
            return option_b;
        }

        public void setOption_b(String option_b) {
            this.option_b = option_b;
        }

        public String getOption_c() {
            return option_c;
        }

        public void setOption_c(String option_c) {
            this.option_c = option_c;
        }

        public String getOption_d() {
            return option_d;
        }

        public void setOption_d(String option_d) {
            this.option_d = option_d;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public int getNote_id() {
            return note_id;
        }

        public void setNote_id(int note_id) {
            this.note_id = note_id;
        }

        public String getNote_name() {
            return note_name;
        }

        public void setNote_name(String note_name) {
            this.note_name = note_name;
        }

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

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
        }

        public float getSummary_accuracy() {
            return summary_accuracy;
        }

        public void setSummary_accuracy(float summary_accuracy) {
            this.summary_accuracy = summary_accuracy;
        }

        public int getSummary_count() {
            return summary_count;
        }

        public void setSummary_count(int summary_count) {
            this.summary_count = summary_count;
        }

        public String getSummary_fallible() {
            return summary_fallible;
        }

        public void setSummary_fallible(String summary_fallible) {
            this.summary_fallible = summary_fallible;
        }

        public int getMaterial_id() {
            return material_id;
        }

        public void setMaterial_id(int material_id) {
            this.material_id = material_id;
        }

        public int getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(int question_id) {
            this.question_id = question_id;
        }

        public UserAnswerBean getUser_answer() {
            return user_answer;
        }

        public void setUser_answer(UserAnswerBean user_answer) {
            this.user_answer = user_answer;
        }

        public List<Integer> getNote_ids() {
            return note_ids;
        }

        public void setNote_ids(List<Integer> note_ids) {
            this.note_ids = note_ids;
        }

        public static class UserAnswerBean {
            private int question_id;
            private boolean is_right;
            private boolean is_collected;
            private String action_time;
            private String answer;

            public int getQuestion_id() {
                return question_id;
            }

            public void setQuestion_id(int question_id) {
                this.question_id = question_id;
            }

            public boolean isIs_right() {
                return is_right;
            }

            public void setIs_right(boolean is_right) {
                this.is_right = is_right;
            }

            public boolean isIs_collected() {
                return is_collected;
            }

            public void setIs_collected(boolean is_collected) {
                this.is_collected = is_collected;
            }

            public String getAction_time() {
                return action_time;
            }

            public void setAction_time(String action_time) {
                this.action_time = action_time;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }
        }
    }
}
