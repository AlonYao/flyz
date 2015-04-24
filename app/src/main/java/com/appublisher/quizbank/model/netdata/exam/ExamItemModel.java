package com.appublisher.quizbank.model.netdata.exam;

/**
 * 单个考试项目模型
 */
public class ExamItemModel {

    String name;
    int exam_id;
    int is_official;
    String code;
    String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExam_id() {
        return exam_id;
    }

    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }

//    public boolean isIs_official() {
//        return is_official;
//    }
//
//    public void setIs_official(boolean is_official) {
//        this.is_official = is_official;
//    }
//
//    public boolean is_official() {
//        return is_official;
//    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
