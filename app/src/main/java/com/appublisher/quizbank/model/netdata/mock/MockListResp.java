package com.appublisher.quizbank.model.netdata.mock;

import java.util.ArrayList;

/**
 * 模考&估分列表数据模型
 */
public class MockListResp {

    int response_code;
    String name;
    String type;
    ArrayList<MockPaperM> paper_list;

    public int getResponse_code() {
        return response_code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<MockPaperM> getPaper_list() {
        return paper_list;
    }
}
