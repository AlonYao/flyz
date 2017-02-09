package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/*
*   名师点评
* */
public class InterviewTeacherRemarkNumResp {

    private int response_code;
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public static class Data {
        private String id;
        private String val;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }
    }
}
