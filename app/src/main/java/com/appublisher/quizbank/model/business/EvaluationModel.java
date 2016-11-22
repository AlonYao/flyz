package com.appublisher.quizbank.model.business;

import android.content.res.Resources;
import android.graphics.Paint;
import android.view.View;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.evaluation.EvaluationResp;
import com.appublisher.quizbank.model.netdata.evaluation.HistoryScoreM;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.utils.PopupWindowManager;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

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
        EvaluationResp evaluationResp = GsonManager.getModel(response.toString(), EvaluationResp.class);

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
            if (!activity.isFinishing())
                PopupWindowManager.showUpdateEvaluation(activity.parentView, activity);
        }
    }

    /**
     * 设置友盟分享
     *
     * @param activity EvaluationActivity
     */
    public static void setUmengShare(EvaluationActivity activity) {
        String content;
        if (activity.mRank <= 1 && activity.mRank >= 0.75) {
            // 100%-75%
            content = "学习Day" + String.valueOf(activity.mLearningDays)
                    + "，我的" + LoginModel.getUserExamName() + "考试已经刷到了"
                    + String.valueOf(activity.mScore) + "分，排名前"
                    + Utils.rateToPercent(activity.mRank) + "%，再也不用担心我的拖延症啦~";
        } else if (activity.mRank < 0.75 && activity.mRank >= 0.5) {
            // 75%-50%
            content = "学习Day" + String.valueOf(activity.mLearningDays)
                    + "，我的" + LoginModel.getUserExamName() + "考试已经刷到了"
                    + String.valueOf(activity.mScore) + "分，排名前"
                    + Utils.rateToPercent(activity.mRank) + "%，上岸指日可待~";
        } else if (activity.mRank < 0.5 && activity.mRank >= 0.25) {
            // 50%-25%
            content = "学习Day" + String.valueOf(activity.mLearningDays)
                    + "，我的" + LoginModel.getUserExamName() + "考试已经刷到了"
                    + String.valueOf(activity.mScore) + "分，排名前"
                    + Utils.rateToPercent(activity.mRank) + "%，成公就在眼前啦~";
        } else {
            // 25%-1%
            content = "学习Day" + String.valueOf(activity.mLearningDays)
                    + "，我的" + LoginModel.getUserExamName() + "考试已经刷到了"
                    + String.valueOf(activity.mScore) + "分，排名前"
                    + Utils.rateToPercent(activity.mRank) + "%，排名靠前也是很孤独的，谁来打败我啊？";
        }

        GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
        String baseUrl = "http://m.zhiboke.net/#/live/assessment?";
        if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
            baseUrl = globalSettingsResp.getEvaluate_share_url();
        }
        baseUrl = baseUrl + "user_id=" + LoginModel.getUserId()
                + "&user_token=" + LoginModel.getUserToken();

        Resources resources = activity.getResources();
        //noinspection ConstantConditions
        UmengManager.UMShareEntity shareEntity = new UmengManager.UMShareEntity()
                .setTitle(resources.getString(R.string.share_title))
                .setText(content)
                .setTargetUrl(baseUrl)
                .setSinaWithoutTargetUrl(true)
                .setUmImage(new UMImage(activity, Utils.getBitmapByView(activity.mSvMain)));

        UmengManager.shareAction(activity, shareEntity, UmengManager.APP_TYPE_QUIZBANK, new UmengManager.PlatformInter() {
            @Override
            public void platform(SHARE_MEDIA platformType) {

            }
        });
    }

}
