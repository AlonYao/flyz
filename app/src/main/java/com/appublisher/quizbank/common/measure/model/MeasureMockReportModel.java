package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.activity.MeasureMockReportActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureMockReportResp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 模考报告
 */

public class MeasureMockReportModel extends MeasureReportModel {

    public MeasureMockReportModel(Context context) {
        super(context);
    }

    @Override
    public void getData() {
        super.getData();
        startRefresh();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (HISTORY_EXERCISE_DETAIL.equals(apiName)) {
            dealHistoryExerciseDetail(response);
        }
        stopRefresh();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        super.requestCompleted(response, apiName);
        stopRefresh();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        super.requestEndedWithError(error, apiName);
        stopRefresh();
    }

    private void startRefresh() {
        if (mContext instanceof MeasureMockReportActivity)
            ((MeasureMockReportActivity) mContext).startRefresh();
    }

    private void stopRefresh() {
        if (mContext instanceof MeasureMockReportActivity)
            ((MeasureMockReportActivity) mContext).stopRefresh();
    }

    private void dealHistoryExerciseDetail(JSONObject response) {
        MeasureMockReportResp resp = GsonManager.getModel(response, MeasureMockReportResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        mAnalysisBean = new MeasureAnalysisBean();
        mAnalysisBean.setCategorys(resp.getCategory());

        showMockName(resp.getExercise_name());
        showScore(resp.getScore());
        showAvgDur(resp.getAvg_duration());
        showCategory(resp.getCategory());
        showBarChart(resp.getMock_rank());
    }

    private void showBarChart(MeasureMockReportResp.MockRankBean mock_rank) {
        if (!(mContext instanceof MeasureMockReportActivity)) return;

        if (mock_rank == null) return;
        List<Integer> list = mock_rank.getDistribute();
        if (list == null) return;

        float[] lineValues = new float[10];
        int amount = 0;
        for (Integer integer : list) {
            amount = amount + integer;
        }

        if (amount == 0) return;

        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i > 9) continue;
            int cur = list.get(i);
            float f = (float) cur / amount * 100;
            BigDecimal b = new BigDecimal(f);
            f = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            lineValues[i] = f;
        }

        ((MeasureMockReportActivity) mContext).showBarChart(lineValues);
    }

    private void showCategory(List<MeasureCategoryBean> categorys) {
        if (!(mContext instanceof MeasureMockReportActivity)) return;

        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureAnswerBean> answers = new ArrayList<>();
        if (categorys != null) {
            for (MeasureCategoryBean category : categorys) {
                if (category == null) continue;
                questions.addAll(category.getQuestions());
                answers.addAll(category.getAnswers());
            }
            List<MeasureReportCategoryBean> categoryList = getCategorys(questions, answers);
            ((MeasureMockReportActivity) mContext).showCategory(categoryList);
        }
    }

    private void showAvgDur(double avg_duration) {
        if (mContext instanceof MeasureMockReportActivity)
            ((MeasureMockReportActivity) mContext).showAvgDur(String.valueOf(avg_duration));
    }

    private void showScore(double score) {
        if (mContext instanceof MeasureMockReportActivity)
            ((MeasureMockReportActivity) mContext).showScore(String.valueOf(score));
    }

    private void showMockName(String name) {
        if (mContext instanceof MeasureMockReportActivity)
            ((MeasureMockReportActivity) mContext).showMockName(name);
    }
}
