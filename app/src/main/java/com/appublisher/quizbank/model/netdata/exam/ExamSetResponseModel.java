package com.appublisher.quizbank.model.netdata.exam;

/**
 * 设置考试项目后的回调
 */
public class ExamSetResponseModel {
    int response_code;
    long sno;
    int position;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public long getSno() {
        return sno;
    }

    public void setSno(long sno) {
        this.sno = sno;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
