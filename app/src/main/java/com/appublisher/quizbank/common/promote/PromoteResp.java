package com.appublisher.quizbank.common.promote;

/**
 * 国考公告解读宣传
 */
public class PromoteResp {

    private int response_code;
    private ImageBean image;
    private AlertBean alert;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public AlertBean getAlert() {
        return alert;
    }

    public void setAlert(AlertBean alert) {
        this.alert = alert;
    }

    public static class ImageBean {
        private boolean enable;
        private String iOS_high;
        private String iOS_low;
        private String android;
        private String target;
        private String target_type;
        private String deadline;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getIOS_high() {
            return iOS_high;
        }

        public void setIOS_high(String iOS_high) {
            this.iOS_high = iOS_high;
        }

        public String getIOS_low() {
            return iOS_low;
        }

        public void setIOS_low(String iOS_low) {
            this.iOS_low = iOS_low;
        }

        public String getAndroid() {
            return android;
        }

        public void setAndroid(String android) {
            this.android = android;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTarget_type() {
            return target_type;
        }

        public void setTarget_type(String target_type) {
            this.target_type = target_type;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }

    public static class AlertBean {
        private boolean enable;
        private String alert_image;
        private String alert_text;
        private String target;
        private String target_type;
        private String deadline;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getAlert_image() {
            return alert_image;
        }

        public void setAlert_image(String alert_image) {
            this.alert_image = alert_image;
        }

        public String getAlert_text() {
            return alert_text;
        }

        public void setAlert_text(String alert_text) {
            this.alert_text = alert_text;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTarget_type() {
            return target_type;
        }

        public void setTarget_type(String target_type) {
            this.target_type = target_type;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }
}
