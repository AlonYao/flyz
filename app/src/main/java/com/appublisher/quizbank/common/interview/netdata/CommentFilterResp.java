package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by jinbao on 2017/2/9.
 */

public class CommentFilterResp {

    private int response_code;
    private List<NotesBean> notes;
    private List<StatusBean> status;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<NotesBean> getNotes() {
        return notes;
    }

    public void setNotes(List<NotesBean> notes) {
        this.notes = notes;
    }

    public List<StatusBean> getStatus() {
        return status;
    }

    public void setStatus(List<StatusBean> status) {
        this.status = status;
    }

    public static class NotesBean {
        private int note_id;
        private String note_name;

        public int getNote_id() {
            return note_id;
        }

        public void setNote_id(int note_id) {
            this.note_id = note_id;
        }

        public String getNote_name() {
            return note_name;
        }

        public void setNote_name(String note_name) {
            this.note_name = note_name;
        }
    }

    public static class StatusBean {

        private int status_id;
        private String status_name;

        public int getStatus_id() {
            return status_id;
        }

        public void setStatus_id(int status_id) {
            this.status_id = status_id;
        }

        public String getComment_status() {
            return status_name;
        }

        public void setComment_status(String status_name) {
            this.status_name = status_name;
        }
    }
}
