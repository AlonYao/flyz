package com.appublisher.quizbank.common.vip.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/9/27.
 */

public class VipIndexEntryDataResp {


    private int response_code;
    private boolean is_vip_member;

    private AnnouncesBean announces;

    private ExercisesBean exercises;

    private NotificationsBean notifications;

    private ClassroomBean classroom;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public boolean isIs_vip_member() {
        return is_vip_member;
    }

    public void setIs_vip_member(boolean is_vip_member) {
        this.is_vip_member = is_vip_member;
    }

    public AnnouncesBean getAnnounces() {
        return announces;
    }

    public void setAnnounces(AnnouncesBean announces) {
        this.announces = announces;
    }

    public ExercisesBean getExercises() {
        return exercises;
    }

    public void setExercises(ExercisesBean exercises) {
        this.exercises = exercises;
    }

    public NotificationsBean getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationsBean notifications) {
        this.notifications = notifications;
    }

    public ClassroomBean getClassroom() {
        return classroom;
    }

    public void setClassroom(ClassroomBean classroom) {
        this.classroom = classroom;
    }

    public static class AnnouncesBean {
        private int total;

        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private int id;
            private String title;
            private String content;
            private String publish_date;
            private boolean is_read;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPublish_date() {
                return publish_date;
            }

            public void setPublish_date(String publish_date) {
                this.publish_date = publish_date;
            }

            public boolean isIs_read() {
                return is_read;
            }

            public void setIs_read(boolean is_read) {
                this.is_read = is_read;
            }
        }
    }

    public static class ExercisesBean {
        private int total;
        private String end_time;

        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
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

    public static class NotificationsBean {
        private int total;
        private int unread_total;
        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getUnread_total() {
            return unread_total;
        }

        public void setUnread_total(int unread_total) {
            this.unread_total = unread_total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private int id;
            private int exercise_id;
            private int exercise_type;
            private int type;
            private String title;
            private String content;
            private String redirect_url;
            private String mobile_redirect_url;
            private String send_time;
            private boolean is_read;

            private SenderBean sender;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

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

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getRedirect_url() {
                return redirect_url;
            }

            public void setRedirect_url(String redirect_url) {
                this.redirect_url = redirect_url;
            }

            public String getMobile_redirect_url() {
                return mobile_redirect_url;
            }

            public void setMobile_redirect_url(String mobile_redirect_url) {
                this.mobile_redirect_url = mobile_redirect_url;
            }

            public String getSend_time() {
                return send_time;
            }

            public void setSend_time(String send_time) {
                this.send_time = send_time;
            }

            public boolean isIs_read() {
                return is_read;
            }

            public void setIs_read(boolean is_read) {
                this.is_read = is_read;
            }

            public SenderBean getSender() {
                return sender;
            }

            public void setSender(SenderBean sender) {
                this.sender = sender;
            }

            public static class SenderBean {
                private int id;
                private String name;
                private String avatar;

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

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }
            }
        }
    }

    public static class ClassroomBean {
        private int total;
        private String start_time;
        private List<?> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public List<?> getList() {
            return list;
        }

        public void setList(List<?> list) {
            this.list = list;
        }
    }
}
