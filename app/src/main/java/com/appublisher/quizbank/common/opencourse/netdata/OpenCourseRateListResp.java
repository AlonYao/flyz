package com.appublisher.quizbank.common.opencourse.netdata;

import java.util.ArrayList;

/**
 * 公开课模块：评价列表接口 数据模型
 */
public class OpenCourseRateListResp {

    int response_code;
    int status;
    RateListSelfItem self;
    ArrayList<RateListOthersItem> others;

    public int getResponse_code() {
        return response_code;
    }

    public int getStatus() {
        return status;
    }

    public RateListSelfItem getSelf() {
        return self;
    }

    public ArrayList<RateListOthersItem> getOthers() {
        return others;
    }
}
