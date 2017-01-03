package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by Admin on 2017/1/3.
 */

public class InterviewCollectResp {
    private int response_code;
    private List<InterviewCollectResp.InterviewM> list;

    public int getResponse_code() {
        return response_code;
    }

    public List<InterviewM> getQuestions() {
        return list;
    }

    public void setQuestions(List<InterviewM> list) {
        this.list = list;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public static class InterviewM{
        private int note_id;
        private String note;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getNote_id() {
            return note_id;
        }

        public void setNote_id(int note_id) {
            this.note_id = note_id;
        }
    }
}
