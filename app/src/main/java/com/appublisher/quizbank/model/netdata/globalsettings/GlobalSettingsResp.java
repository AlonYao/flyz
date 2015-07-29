package com.appublisher.quizbank.model.netdata.globalsettings;

import java.util.ArrayList;

/**
 * 全局配置接口回调数据模型
 */
public class GlobalSettingsResp {

    int response_code;
    String service_qq;
    String market_qq;
    int open_course_heartbeat;
    ArrayList<ExerciseIntroM> exercise_intro;
    MockM mock;
    String report_share_url;
    String evaluate_share_url;

    public String getReport_share_url() {
        return report_share_url;
    }

    public String getEvaluate_share_url() {
        return evaluate_share_url;
    }

    public MockM getMock() {
        return mock;
    }

    public String getMarket_qq() {
        return market_qq;
    }

    public int getOpen_course_heartbeat() {
        return open_course_heartbeat;
    }

    public int getResponse_code() {
        return response_code;
    }

    public ArrayList<ExerciseIntroM> getExercise_intro() {
        return exercise_intro;
    }

    public String getService_qq() {
        return service_qq;
    }
}
