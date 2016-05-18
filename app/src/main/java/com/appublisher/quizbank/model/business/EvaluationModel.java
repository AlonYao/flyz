package com.appublisher.quizbank.model.business;

import android.graphics.Paint;
import android.view.View;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.model.entity.umeng.UMShareContentEntity;
import com.appublisher.quizbank.model.entity.umeng.UMShareUrlEntity;
import com.appublisher.quizbank.model.entity.umeng.UmengShareEntity;
import com.appublisher.quizbank.model.netdata.evaluation.EvaluationResp;
import com.appublisher.quizbank.model.netdata.evaluation.HistoryScoreM;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.PopupWindowManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
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
     *
     * @param activity EvaluationActivity
     * @param response 回调数据
     */
    public static void dealEvaluationResp(EvaluationActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        EvaluationResp evaluationResp = gson.fromJson(response.toString(), EvaluationResp.class);

        if (evaluationResp == null || evaluationResp.getResponse_code() != 1) return;

        // 列表数据
        activity.mScore = evaluationResp.getScore();
        activity.mRank = evaluationResp.getRank();
        activity.mLearningDays = evaluationResp.getLearning_days();
        int totalTime = evaluationResp.getTotal_time();
        int totalQuestions = evaluationResp.getTotal_questions();
        int avarageQuestions = evaluationResp.getAvarage_questions();
        float accuracy = evaluationResp.getAccuracy();
        float avarageAccuracy = evaluationResp.getAvarage_accuracy();
        String summarySource = evaluationResp.getSummary_source();
        String calculationBasis = evaluationResp.getCalculation_basis();
        String summaryDate = evaluationResp.getSummary_date();

        int accuracyInt = (int) (Math.round(accuracy * 100) / 1.0);
        int avarageAccuracyInt = (int) (Math.round(avarageAccuracy * 100) / 1.0);

        // 预测分&排名
        activity.mTvScore.setText(String.valueOf(activity.mScore));
        activity.mTvRank.setText(Utils.rateToPercent(activity.mRank));

        // 学习天数
        activity.mTvLearningDays.setText(String.valueOf(activity.mLearningDays));

        // 模考时长
        activity.mTvTotalTime.setText(String.valueOf(totalTime / 60));

        // 答题量&全站平均
        activity.mTvTotalQuestions.setText(String.valueOf(totalQuestions));
        activity.mTvAvarageQuestions.setText(String.valueOf(avarageQuestions));

        // 正确率&全站平均
        activity.mTvAccuracy.setText(String.valueOf(accuracyInt));
        activity.mTvAvarageAccuracy.setText(String.valueOf(avarageAccuracyInt));

        // 统计
        activity.mTvSummarySource.setText("统计来源：" + summarySource);
        activity.mTvCalculationBasis.setText("计算根据：" + calculationBasis);
        activity.mTvSummaryDate.setText("报告时间：" + summaryDate);

        // 绘制折线图
        ArrayList<HistoryScoreM> historyScores = evaluationResp.getHistory_score();
        String[] lineLabels = new String[]{"", "", "", "", "", "", ""};  // X轴上显示的文字
        float[] lineValues = new float[]{0, 0, 0, 0, 0, 0, 0};  // 各个点的分值
        int size;

        if (historyScores != null && historyScores.size() != 0) {
            size = historyScores.size();

            for (int i = 0; i < size; i++) {
                HistoryScoreM historyScore = historyScores.get(i);

                if (historyScore == null) continue;

                String itemDate = historyScore.getDate();
                int itemScore = historyScore.getScore();

                lineLabels[i] = Utils.switchDate(itemDate, "hh-dd");
                lineValues[i] = itemScore;
            }

        } else {
            size = 1;
        }

        ArrayList<HierarchyM> hierarchys = evaluationResp.getNote_hierarchy();
        if (hierarchys != null && hierarchys.size() != 0) {
            new KnowledgeTreeModel(
                    activity,
                    activity.mContainer,
                    KnowledgeTreeModel.TYPE_EVALUATION).dealHierarchyResp(hierarchys);
        }

        // 根据值绘图
        Paint lineGridPaint = new Paint();
        lineGridPaint.setColor(activity.getResources().getColor(R.color.common_line));
        lineGridPaint.setStyle(Paint.Style.STROKE);
        lineGridPaint.setAntiAlias(true);
        lineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        activity.mLineChart.reset();

        LineSet dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues);
        dataSet.setSmooth(true);
        dataSet.setDashed(false);
        dataSet.setDots(true)
                .setDotsColor(activity.getResources().getColor(R.color.evaluation_diagram_line))
                .setDotsRadius(Tools.fromDpToPx(4))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(
                        activity.getResources().getColor(R.color.evaluation_diagram_line))
                .setLineColor(activity.getResources().getColor(R.color.evaluation_diagram_line))
                .setLineThickness(Tools.fromDpToPx(3))
                .beginAt(0).endAt(size);
        activity.mLineChart.addData(dataSet);

        activity.mLineChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setGrid(LineChartView.GridType.FULL, lineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(0, 100, 20)
                .show();

        activity.mLlHistory.setVisibility(View.VISIBLE);

        //1.5版本提示
        boolean isFirstStart = Globals.sharedPreferences.getBoolean("firstNotice", true);
        boolean detailCategory = Globals.sharedPreferences.getBoolean("detailCategory", true);
        if (!isFirstStart && detailCategory) {
            PopupWindowManager.showUpdateEvaluation(activity.parentView, activity);
        }
    }

    /**
     * 设置友盟分享
     *
     * @param activity EvaluationActivity
     */
    public static void setUmengShare(EvaluationActivity activity) {
        UmengShareEntity umengShareEntity = new UmengShareEntity();
        umengShareEntity.setActivity(activity);
        umengShareEntity.setBitmap(Utils.getBitmapByView(activity.mSvMain));
        umengShareEntity.setFrom("evaluation");

        // 友盟分享文字处理
        UMShareContentEntity umShareContentEntity = new UMShareContentEntity();
        umShareContentEntity.setType("evaluation");
        umShareContentEntity.setLearningDays(activity.mLearningDays);
        umShareContentEntity.setExamName(LoginModel.getUserExamName());
        umShareContentEntity.setScore(activity.mScore);
        umShareContentEntity.setRank(activity.mRank);
        umengShareEntity.setContent(UmengManager.getShareContent(umShareContentEntity));

        // 友盟分享跳转链接处理
        UMShareUrlEntity urlEntity = new UMShareUrlEntity();
        urlEntity.setType("evaluation");
        urlEntity.setUser_id(LoginModel.getUserId());
        urlEntity.setUser_token(LoginModel.getUserToken());
        umengShareEntity.setUrl(UmengManager.getUrl(urlEntity));

        UmengManager.openShare(umengShareEntity);
    }

}
