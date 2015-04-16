package com.appublisher.quizbank.model.netdata.hierarchy;

import java.util.ArrayList;

/**
 * 知识点层级结构回调 数据模型
 */
public class HierarchyResp {

    int response_code;
    ArrayList<HierarchyM> hierarchy;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<HierarchyM> getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(ArrayList<HierarchyM> hierarchy) {
        this.hierarchy = hierarchy;
    }
}
