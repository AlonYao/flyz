package com.appublisher.quizbank.common.measure.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureMockReportResp;
import com.appublisher.quizbank.common.measure.view.IMeasureMockReportView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 模考报告
 */

public class MeasureMockReportModel extends MeasureReportModel {

    private IMeasureMockReportView mView;

    public MeasureMockReportModel(Context context, IMeasureMockReportView view) {
        super(context);
        mView = view;
    }

    @Override
    public void getData() {
        super.getData();
        mView.startRefresh();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (HISTORY_EXERCISE_DETAIL.equals(apiName)) {
            dealHistoryExerciseDetail(response);
        }
        mView.stopRefresh();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        super.requestCompleted(response, apiName);
        mView.stopRefresh();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        super.requestEndedWithError(error, apiName);
        mView.stopRefresh();
    }

    private void dealHistoryExerciseDetail(JSONObject response) {
        MeasureMockReportResp resp = GsonManager.getModel(response, MeasureMockReportResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        mAnalysisBean = new MeasureAnalysisBean();
        mAnalysisBean.setCategorys(resp.getCategory());

        mView.showMockName(resp.getExercise_name());
        mView.showScore(String.valueOf(resp.getScore()));
        mView.showAvgDur(String.valueOf(resp.getAvg_duration()));
        mView.showNotes(resp.getNotes());

        showNotice(resp.getMock_rank());
        showCategory(resp.getCategory());
        showBarChart(resp.getMock_rank());
        showLineChart(resp.getHistory_mock());
        showStatistics(resp.getMock_rank());
        showUp(resp.getMock_rank());
    }

    private void showUp(MeasureMockReportResp.MockRankBean mock_rank) {
        if (mock_rank == null || !mock_rank.isAvailable()) return;
        mView.showUp(mock_rank.isDefeat_up(), mock_rank.isScore_up());

        if (!isShowUpBefore()) {
            mView.showUpAlert(mock_rank.isDefeat_up(), mock_rank.isScore_up());
            updateShowUpIds();
        }
    }

    private boolean isShowUpBefore() {
        SharedPreferences spf = MeasureModel.getMeasureCache(mContext);
        if (spf == null) return false;

        try {
            String cache = spf.getString(CACHE_MOCK_UP_PAPER_IDS, "");
            if (cache.length() == 0) return false;

            JSONArray ids = new JSONArray(cache);
            int length = ids.length();
            for (int i = 0; i < length; i++) {
                if (mPaperId == (int) ids.get(i)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressLint("CommitPrefEdits")
    private void updateShowUpIds() {
        if (isShowUpBefore()) return;

        SharedPreferences spf = MeasureModel.getMeasureCache(mContext);
        if (spf == null) return;

        try {
            String cache = spf.getString(CACHE_MOCK_UP_PAPER_IDS, "");

            JSONArray ids;
            if (cache.length() > 0) {
                ids = new JSONArray(spf.getString(CACHE_MOCK_UP_PAPER_IDS, ""));
            } else {
                ids = new JSONArray();
            }
            ids.put(mPaperId);
            String s = ids.toString();
            SharedPreferences.Editor editor = spf.edit();
            editor.putString(CACHE_MOCK_UP_PAPER_IDS, s);
            editor.commit();
        } catch (Exception e) {
            // Empty
        }
    }

    private void showNotice(MeasureMockReportResp.MockRankBean mock_rank) {
        if (mock_rank == null || mock_rank.isAvailable()) {
            mView.hideNotice();
            return;
        }

        String time = mock_rank.getAvailable_time();
        try {
            time = time.substring(11, 16);
            time = time.replace("-", ":");
        } catch (Exception e) {
            // Empty
        }
        mView.showNotice(time);
    }

    private void showStatistics(MeasureMockReportResp.MockRankBean mockRankBean) {
        if (mockRankBean == null || !mockRankBean.isAvailable()) return;

        mView.showStatistics(
                String.valueOf(mockRankBean.getDefeat()*100),
                String.valueOf(mockRankBean.getAvg()),
                String.valueOf(mockRankBean.getTop()));
    }

    private void showLineChart(List<MeasureMockReportResp.HistoryMockBean> history_mock) {
        if (history_mock == null) return;

        int size = history_mock.size();
        if (size == 0) return;

        if (size > 30) size = 30;

        String[] lineLabels = new String[size + 1];
        float[] lineScores = new float[size + 1];
        float[] lineAvgs = new float[size + 1];

        lineLabels[size] = "";
        lineScores[size] = 0;
        lineAvgs[size] = 0;

        int j = size - 1;
        for (int i = 0; i < size; i++) {
            MeasureMockReportResp.HistoryMockBean bean = history_mock.get(i);
            if (bean == null) continue;

            // 日期处理
            String date = bean.getDate();
            try {
                date = date.substring(5, 10);
                date = date.replace("-", "/");
            } catch (Exception e) {
                // Empty
            }

            lineLabels[j] = date;
            lineScores[j] = (float) bean.getUser_score();
            lineAvgs[j] = (float) bean.getAvg();

            j--;
        }

        mView.showLineChart(lineLabels, lineScores, lineAvgs);
    }

    private void showBarChart(MeasureMockReportResp.MockRankBean mock_rank) {
        if (mock_rank == null) return;
        List<Integer> list = mock_rank.getDistribute();
        if (list == null || list.size() == 0) return;

        float[] lineValues = new float[10];
        int amount = 0;
        for (Integer integer : list) {
            amount = amount + integer;
        }

        if (amount == 0) return;

        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i > 9) continue; // 最多显示10个数据
            int cur = list.get(i);
            float f = (float) cur / amount * 100;
            BigDecimal b = new BigDecimal(f);
            f = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            lineValues[i] = f;
        }

        mView.showBarChart(lineValues);
    }

    private void showCategory(List<MeasureCategoryBean> categorys) {
        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureAnswerBean> answers = new ArrayList<>();
        if (categorys != null) {
            for (MeasureCategoryBean category : categorys) {
                if (category == null) continue;
                questions.addAll(category.getQuestions());
                answers.addAll(category.getAnswers());
            }
            List<MeasureReportCategoryBean> categoryList = getCategorys(questions, answers);
            mView.showCategory(categoryList);
        }
    }

}
