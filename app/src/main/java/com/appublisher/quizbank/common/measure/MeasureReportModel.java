package com.appublisher.quizbank.common.measure;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;

import org.json.JSONObject;

/**
 * 做题模块：练习报告
 */

public class MeasureReportModel extends MeasureModel{

    public MeasureReportModel(Context context) {
        super(context);
    }

    public void getData() {
        mRequest.getHistoryExerciseDetail(mPaperId, mPaperType);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (HISTORY_EXERCISE_DETAIL.equals(apiName)) {
            dealHistoryExerciseDetailResp(response);
        }
    }

    private void dealHistoryExerciseDetailResp(JSONObject response) {
        MeasureHistoryResp resp = GsonManager.getModel(response, MeasureHistoryResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

    }
}
