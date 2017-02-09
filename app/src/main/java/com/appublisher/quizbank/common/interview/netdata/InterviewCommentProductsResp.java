package com.appublisher.quizbank.common.interview.netdata;

import java.util.List;

/**
 * Created by jinbao on 2017/1/23.
 */

public class InterviewCommentProductsResp {

    private int response_code;
    private boolean first_buy;
    private List<CommentProductM> list;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public boolean isFirst_buy() {
        return first_buy;
    }

    public void setFirst_buy(boolean first_buy) {
        this.first_buy = first_buy;
    }

    public List<CommentProductM> getList() {
        return list;
    }

    public void setList(List<CommentProductM> list) {
        this.list = list;
    }

}
