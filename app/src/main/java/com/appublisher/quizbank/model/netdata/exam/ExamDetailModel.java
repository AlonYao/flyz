package com.appublisher.quizbank.model.netdata.exam;

import java.util.ArrayList;

/**
 * 考试项目详情模型
 */
public class ExamDetailModel {

    int response_code;
    ArrayList<ExamItemModel> exams;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<ExamItemModel> getExams() {
        return exams;
    }

    public void setExams(ArrayList<ExamItemModel> exams) {
        this.exams = exams;
    }
}
