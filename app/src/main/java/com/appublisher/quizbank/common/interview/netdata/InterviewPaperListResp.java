package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by jinbao on 2016/11/16.
 */

public class InterviewPaperListResp {

    private int response_code;
    private List<PapersBean> papers;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<PapersBean> getPapers() {
        return papers;
    }

    public void setPapers(List<PapersBean> papers) {
        this.papers = papers;
    }

    public static class PapersBean {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
