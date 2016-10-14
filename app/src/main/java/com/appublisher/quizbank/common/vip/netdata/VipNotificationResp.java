package com.appublisher.quizbank.common.vip.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/8/30.
 */
public class VipNotificationResp {

    private int response_code;

    private List<NotificationsBean> notifications;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<NotificationsBean> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationsBean> notifications) {
        this.notifications = notifications;
    }

    public static class NotificationsBean {
        private int id;
        private int exercise_id;
        private int type;
        private String title;
        private String content;
        private String redirect_url;
        private String send_time;
        private boolean is_read;
        private SenderBean sender;
        private int exercise_type;
        private String mobile_redirect_url;

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

        public int getExercise_type() {
            return exercise_type;
        }

        public void setExercise_type(int exercise_type) {
            this.exercise_type = exercise_type;
        }

        public String getMobile_redirect_url() {
            return mobile_redirect_url;
        }

        public void setMobile_redirect_url(String mobile_redirect_url) {
            this.mobile_redirect_url = mobile_redirect_url;
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
