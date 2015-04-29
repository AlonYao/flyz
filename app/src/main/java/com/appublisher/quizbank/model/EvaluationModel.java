package com.appublisher.quizbank.model;

import android.graphics.Paint;
import android.view.View;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.model.netdata.evaluation.EvaluationResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 能力评估 Activity Model
 */
public class EvaluationModel {

    /**
     * 处理能力评估回调
     * @param activity EvaluationActivity
     * @param response 回调数据
     */
    public static void dealEvaluationResp(EvaluationActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        EvaluationResp evaluationResp = gson.fromJson(response.toString(), EvaluationResp.class);

        if (evaluationResp == null || evaluationResp.getResponse_code() != 1) return;

        // 列表数据
        int score = evaluationResp.getScore();
        int rank = evaluationResp.getRank();
        int learningDays = evaluationResp.getLearning_days();
        int totalTime = evaluationResp.getTotal_time();
        int totalQuestions = evaluationResp.getTotal_questions();
        int avarageQuestions = evaluationResp.getAvarage_questions();
        float accuracy = evaluationResp.getAccuracy();
        float avarageAccuracy = evaluationResp.getAvarage_accuracy();
        String summarySource = evaluationResp.getSummary_source();
        String calculationBasis = evaluationResp.getCalculation_basis();
        String summaryDate = evaluationResp.getSummary_date();

        accuracy = (float) (Math.round(accuracy*10000)/100.0);
        avarageAccuracy = (float) (Math.round(avarageAccuracy*10000)/100.0);

        activity.mTvScore.setText(String.valueOf(score));
        activity.mTvRank.setText(String.valueOf(rank));
        activity.mTvLearningDays.setText(String.valueOf(learningDays));
        activity.mTvTotalTime.setText(String.valueOf(totalTime / 60));
        activity.mTvTotalQuestions.setText(String.valueOf(totalQuestions));
        activity.mTvAvarageQuestions.setText(String.valueOf(avarageQuestions));
        activity.mTvAccuracy.setText(String.valueOf(accuracy));
        activity.mTvAvarageAccuracy.setText(String.valueOf(avarageAccuracy));

        activity.mTvSummarySource.setText("统计来源：" + summarySource);
        activity.mTvCalculationBasis.setText("计算根据：" + calculationBasis);
        activity.mTvSummaryDate.setText("报告时间：" + summaryDate);

        // 绘制折线图
        ArrayList<Object> historyScores = evaluationResp.getHistory_score();

        if (historyScores != null && historyScores.size() != 0) {
            String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
            float[][] lineValues = { {10, 80, 20, 100, 0, 10, 50} };

            Paint lineGridPaint = new Paint();
            lineGridPaint.setColor(activity.getResources().getColor(R.color.setting_line));
            lineGridPaint.setStyle(Paint.Style.STROKE);
            lineGridPaint.setAntiAlias(true);
            lineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

            activity.mLineChart.reset();

            LineSet dataSet = new LineSet();
            dataSet.addPoints(lineLabels, lineValues[0]);
            dataSet.setSmooth(true);
            dataSet.setDashed(false);
            dataSet.setDots(true)
                    .setDotsColor(activity.getResources().getColor(R.color.evaluation_diagram_line))
                    .setDotsRadius(Tools.fromDpToPx(5))
                    .setDotsStrokeThickness(Tools.fromDpToPx(2))
                    .setDotsStrokeColor(
                            activity.getResources().getColor(R.color.evaluation_diagram_line))
                    .setLineColor(activity.getResources().getColor(R.color.evaluation_diagram_line))
                    .setLineThickness(Tools.fromDpToPx(3))
                    .beginAt(1).endAt(lineLabels.length - 1);
            activity.mLineChart.addData(dataSet);

            activity.mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                    .setGrid(LineChartView.GridType.FULL, lineGridPaint)
                    .setXAxis(false)
                    .setXLabels(XController.LabelPosition.OUTSIDE)
                    .setYAxis(false)
                    .setYLabels(YController.LabelPosition.OUTSIDE)
                    .setAxisBorderValues(0, 100, 20)
                    .show();

            activity.mLlHistory.setVisibility(View.VISIBLE);
        } else {
            activity.mLlHistory.setVisibility(View.GONE);
        }
    }
}
