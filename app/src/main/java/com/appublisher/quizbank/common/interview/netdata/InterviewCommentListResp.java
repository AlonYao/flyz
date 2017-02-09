package com.appublisher.quizbank.common.interview.netdata;

import com.appublisher.quizbank.common.interview.netdata.InterviewCommentM;

import java.util.List;

/**
 * Created by jinbao on 2017/2/9.
 */

public class InterviewCommentListResp {

    private int response_code;
    private List<InterviewCommentM> list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<InterviewCommentM> getList() {
        return list;
    }

    public void setList(List<InterviewCommentM> list) {
        this.list = list;
    }

}
