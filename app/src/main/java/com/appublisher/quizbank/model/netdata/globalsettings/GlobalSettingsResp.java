package com.appublisher.quizbank.model.netdata.globalsettings;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseRateTagItem;
import com.appublisher.quizbank.common.update.NewVersion;

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
    String question_share_url;
    String app_ios_url;
    String app_android_url;
    NewVersion new_version;
    ArrayList<OpenCourseRateTagItem> rate_tags;

    public String getApp_ios_url() {
        return app_ios_url;
    }

    public String getApp_android_url() {
        return app_android_url;
    }

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

    public NewVersion getNew_version() {
        return new_version;
    }

    public ArrayList<OpenCourseRateTagItem> getRate_tags() {
        return rate_tags;
    }

    public String getQuestion_share_url() {
        return question_share_url;
    }
}
