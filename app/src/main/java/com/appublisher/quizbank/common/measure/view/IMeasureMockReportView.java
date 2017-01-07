package com.appublisher.quizbank.common.measure.view;

import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;

import java.util.List;

/**
 * 做题模块：模考报告页面view层
 */

public interface IMeasureMockReportView extends IMeasureReportBaseView{

    void startRefresh();

    void stopRefresh();

    void showMockName(String name);

    void showScore(String score);

    void showAvgDur(String dur);

    void showCategory(List<MeasureReportCategoryBean> list);

    void showBarChart(float[] lineValues);

    void showLineChart(String[] lineLabels, float[] lineScore, float[] lineAvg);

    void showStatistics(String defeat, String avg, String best);

    void showNotice(String time);

    void hideNotice();

    void showUp(boolean isRankUp, boolean isScoreUp);

    void showUpAlert(boolean isRankUp, boolean isScoreUp);
}
