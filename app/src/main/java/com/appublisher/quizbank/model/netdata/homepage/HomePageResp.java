package com.appublisher.quizbank.model.netdata.homepage;

/**
 * 首页数据模型
 */
public class HomePageResp {

    int response_code;
    AssessmentM assessmentM;
    PaperM paper;
    LiveCourseM live_course;

    public int getResponse_code() {
        return response_code;
    }

    public PaperM getPaper() {
        return paper;
    }

    public void setPaper(PaperM paper) {
        this.paper = paper;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public AssessmentM getAssessmentM() {
        return assessmentM;
    }

    public void setAssessmentM(AssessmentM assessmentM) {
        this.assessmentM = assessmentM;
    }

    public LiveCourseM getLive_course() {
        return live_course;
    }

    public void setLive_course(LiveCourseM live_course) {
        this.live_course = live_course;
    }
}
