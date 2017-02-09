package com.appublisher.quizbank.common.interview.netdata;

/**
 * Created by jinbao on 2017/2/9.
 */

public class InterviewCommentM {
    private int record_id;
    private String question;
    private String note_name;
    private String comment_status;

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getNote_name() {
        return note_name;
    }

    public void setNote_name(String note_name) {
        this.note_name = note_name;
    }

    public String getComment_status() {
        return comment_status;
    }

    public void setComment_status(String comment_status) {
        this.comment_status = comment_status;
    }
}
