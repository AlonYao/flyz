package com.appublisher.quizbank.model.netdata.globalsettings;

import java.util.List;

/**
 * 全局配置接口回调数据模型
 */
public class GlobalSettingsResp {

    private int response_code;
    private String service_qq;
    private String market_qq;
    private int open_course_heartbeat;
    private MockBean mock;
    private String report_share_url;
    private String evaluate_share_url;
    private String question_share_url;
    private String app_ios_url;
    private String app_android_url;
    private NewVersionBean new_version;
    private boolean is_review;
    private int latest_notify;
    private String course_share_url;
    private List<ExerciseIntroBean> exercise_intro;
    private List<RateTagsBean> rate_tags;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public String getService_qq() {
        return service_qq;
    }

    public void setService_qq(String service_qq) {
        this.service_qq = service_qq;
    }

    public String getMarket_qq() {
        return market_qq;
    }

    public void setMarket_qq(String market_qq) {
        this.market_qq = market_qq;
    }

    public int getOpen_course_heartbeat() {
        return open_course_heartbeat;
    }

    public void setOpen_course_heartbeat(int open_course_heartbeat) {
        this.open_course_heartbeat = open_course_heartbeat;
    }

    public MockBean getMock() {
        return mock;
    }

    public void setMock(MockBean mock) {
        this.mock = mock;
    }

    public String getReport_share_url() {
        return report_share_url;
    }

    public void setReport_share_url(String report_share_url) {
        this.report_share_url = report_share_url;
    }

    public String getEvaluate_share_url() {
        return evaluate_share_url;
    }

    public void setEvaluate_share_url(String evaluate_share_url) {
        this.evaluate_share_url = evaluate_share_url;
    }

    public String getQuestion_share_url() {
        return question_share_url;
    }

    public void setQuestion_share_url(String question_share_url) {
        this.question_share_url = question_share_url;
    }

    public String getApp_ios_url() {
        return app_ios_url;
    }

    public void setApp_ios_url(String app_ios_url) {
        this.app_ios_url = app_ios_url;
    }

    public String getApp_android_url() {
        return app_android_url;
    }

    public void setApp_android_url(String app_android_url) {
        this.app_android_url = app_android_url;
    }

    public NewVersionBean getNew_version() {
        return new_version;
    }

    public void setNew_version(NewVersionBean new_version) {
        this.new_version = new_version;
    }

    public boolean isIs_review() {
        return is_review;
    }

    public void setIs_review(boolean is_review) {
        this.is_review = is_review;
    }

    public int getLatest_notify() {
        return latest_notify;
    }

    public void setLatest_notify(int latest_notify) {
        this.latest_notify = latest_notify;
    }

    public String getCourse_share_url() {
        return course_share_url;
    }

    public void setCourse_share_url(String course_share_url) {
        this.course_share_url = course_share_url;
    }

    public List<ExerciseIntroBean> getExercise_intro() {
        return exercise_intro;
    }

    public void setExercise_intro(List<ExerciseIntroBean> exercise_intro) {
        this.exercise_intro = exercise_intro;
    }

    public List<RateTagsBean> getRate_tags() {
        return rate_tags;
    }

    public void setRate_tags(List<RateTagsBean> rate_tags) {
        this.rate_tags = rate_tags;
    }

    public static class MockBean {
    }

    public static class NewVersionBean {
    }

    public static class ExerciseIntroBean {
        private String type;
        private String name;
        private String intro;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }
    }

    public static class RateTagsBean {
        private int star;
        private List<String> tags;

        public int getStar() {
            return star;
        }

        public void setStar(int star) {
            this.star = star;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

}
