package com.appublisher.quizbank.model.netdata.exam;

import java.util.List;

/**
 * 考试项目详情模型
 */
public class ExamDetailModel {

    int response_code;
    List<ExamItemModel> exams;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<ExamItemModel> getExams() {
        return exams;
    }

    public void setExams(List<ExamItemModel> exams) {
        this.exams = exams;
    }
}
