package com.appublisher.quizbank.model.login.model.netdata;

/**
 * 用户考试项目信息模型
 */
public class UserExamInfoModel {

    String exam_id;
    String code;
    String date;
    String is_official;
    String name;

    public String getExam_id() {
        return exam_id;
    }

    public void setExam_id(String exam_id) {
        this.exam_id = exam_id;
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

    public String getIs_official() {
        return is_official;
    }

    public void setIs_official(String is_official) {
        this.is_official = is_official;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
