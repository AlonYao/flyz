package com.appublisher.quizbank.model.netdata.globalsettings;

import java.util.ArrayList;

/**
 * 全局配置接口回调数据模型
 */
public class GlobalSettingsResp {

    int response_code;
    String service_qq;
    ArrayList<ExerciseIntroM> exercise_intro;

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
