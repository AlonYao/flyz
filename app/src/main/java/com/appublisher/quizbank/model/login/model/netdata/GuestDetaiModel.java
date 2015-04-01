package com.appublisher.quizbank.model.login.model.netdata;

/**
 * 游客注册返回信息模型
 */
public class GuestDetaiModel {
    int response_code;
    UserExamInfoModel exam;
    GuestInfoModel guest_info;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public UserExamInfoModel getExam() {
        return exam;
    }

    public void setExam(UserExamInfoModel exam) {
        this.exam = exam;
    }

    public GuestInfoModel getGuest_info() {
        return guest_info;
    }

    public void setGuest_info(GuestInfoModel guest_info) {
        this.guest_info = guest_info;
    }
}
