package com.appublisher.quizbank.common.login.model.netdata;

/**
 * 用户考试项目信息模型
 */
public class UserExamInfoModel {

    int exam_id;
    String code;
    String date;
    boolean is_official;
    boolean is_interview;
    String name;

    public boolean is_interview() {
        return is_interview;
    }

    public void setIs_interview(boolean is_interview) {
        this.is_interview = is_interview;
    }

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

    public boolean is_official() {
        return is_official;
    }

    public void setIs_official(boolean is_official) {
        this.is_official = is_official;
    }
}
