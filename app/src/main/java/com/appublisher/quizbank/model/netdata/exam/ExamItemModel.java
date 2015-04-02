package com.appublisher.quizbank.model.netdata.exam;

/**
 * 单个考试项目模型
 */
public class ExamItemModel {

    String name;
    int exam_id;
    boolean is_official;
    String shortening;
    String exam_date;

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

    public boolean isIs_official() {
        return is_official;
    }

    public void setIs_official(boolean is_official) {
        this.is_official = is_official;
    }

    public String getShortening() {
        return shortening;
    }

    public void setShortening(String shortening) {
        this.shortening = shortening;
    }

    public String getExam_date() {
        return exam_date;
    }

    public void setExam_date(String exam_date) {
        this.exam_date = exam_date;
    }
}
