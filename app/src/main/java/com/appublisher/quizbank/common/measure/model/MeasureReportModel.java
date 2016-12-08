package com.appublisher.quizbank.common.measure.model;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：练习报告
 */

public class MeasureReportModel extends MeasureModel {

    public MeasureAnalysisBean mAnalysisBean;

    private double mScore;
    private double mDefeat;
    private int mRigthNum;
    private int mTotalNum;
    private ScrollView mScrollView;

    public MeasureReportModel(Context context) {
        super(context);
    }

    public void getData() {
        mRequest.getHistoryExerciseDetail(mPaperId, mPaperType);
    }

    public boolean isAllRight() {
        if (mAnalysisBean == null) return true;

        List<MeasureCategoryBean> categorys = mAnalysisBean.getCategorys();
        if (categorys == null || categorys.size() == 0) {
            // 非整卷
            List<MeasureAnswerBean> answers = mAnalysisBean.getAnswers();
            if (answers == null) return true;
            for (MeasureAnswerBean answer : answers) {
                if (answer == null) continue;
                if (!answer.is_right()) return false;
            }

        } else {
            // 整卷
            for (MeasureCategoryBean category : categorys) {
                if (category == null) continue;
                List<MeasureAnswerBean> answers = category.getAnswers();
                if (answers == null) return true;
                for (MeasureAnswerBean answer : answers) {
                    if (answer == null) continue;
                    if (!answer.is_right()) return false;
                }
            }
        }

        return true;
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (HISTORY_EXERCISE_DETAIL.equals(apiName)) {
            dealHistoryExerciseDetailResp(response);
        }
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    private void dealHistoryExerciseDetailResp(JSONObject response) {
        MeasureHistoryResp resp = GsonManager.getModel(response, MeasureHistoryResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureReportActivity)) return;

        // init param
        mAnalysisBean = new MeasureAnalysisBean();
        mAnalysisBean.setCategorys(resp.getCategory());
        mAnalysisBean.setQuestions(resp.getQuestions());
        mAnalysisBean.setAnswers(resp.getAnswers());

        // Umeng分享
        mPaperName = resp.getExercise_name();
        mScore = resp.getScore();
        mDefeat = resp.getDefeat();

        if (AUTO.equals(mPaperType)) {
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 做对/全部
            showRightAll(resp.getAnswers());

            // 科目
            List<MeasureReportCategoryBean> categorys = getCategorys(resp.getQuestions(), resp.getAnswers());
            ((MeasureReportActivity) mContext).showCategory(categorys);

            // 知识点
            ((MeasureReportActivity) mContext).showNotes(resp.getNotes());
        } else if (ENTIRE.equals(mPaperType)){
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 你的分数
            ((MeasureReportActivity) mContext).showYourScore(String.valueOf(resp.getScore()));

            // 全站排名
            ((MeasureReportActivity) mContext).showStatistics(
                    Utils.rateToPercent(resp.getDefeat()) + "%",
                    String.valueOf(resp.getAvg_score()));

            // 科目
            List<MeasureQuestionBean> questions = new ArrayList<>();
            List<MeasureAnswerBean> answers = new ArrayList<>();
            List<MeasureCategoryBean> categorys = resp.getCategory();
            if (categorys != null) {
                for (MeasureCategoryBean category : categorys) {
                    if (category == null) continue;
                    questions.addAll(category.getQuestions());
                    answers.addAll(category.getAnswers());
                }
                List<MeasureReportCategoryBean> categoryList = getCategorys(questions, answers);
                ((MeasureReportActivity) mContext).showCategory(categoryList);
            }

            // 分数线
            ((MeasureReportActivity) mContext).showBorderline(resp.getScores());
        } else if (MOKAO.equals(mPaperType)) {
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 战绩
            String defeat = "击败"
                    + Utils.rateToPercent(resp.getDefeat())
                    + "%的考生";
            Spannable word = new SpannableString(defeat);
            word.setSpan(
                    new AbsoluteSizeSpan(Utils.sp2px(mContext, 22)),
                    2,
                    defeat.indexOf("的考生"),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //noinspection deprecation
            word.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.themecolor)),
                    2,
                    defeat.indexOf("的考生"),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ((MeasureReportActivity) mContext).showStandings(word);

            // 做对/全部
            showRightAll(resp.getAnswers());

            // 科目
            List<MeasureReportCategoryBean> categorys = getCategorys(
                    resp.getQuestions(), resp.getAnswers());
            ((MeasureReportActivity) mContext).showCategory(categorys);

            // 知识点
            ((MeasureReportActivity) mContext).showNotes(resp.getNotes());
        } else if (NOTE.equals(mPaperType)) {
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 做对/全部
            showRightAll(resp.getAnswers());

            // 科目
            List<MeasureReportCategoryBean> categorys = getCategorys(
                    resp.getQuestions(), resp.getAnswers());
            ((MeasureReportActivity) mContext).showCategory(categorys);

            // 知识点
            ((MeasureReportActivity) mContext).showNotes(resp.getNotes());
        } else if (MOCK.equals(mPaperType)) {
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 你的分数
            ((MeasureReportActivity) mContext).showYourScore(String.valueOf(resp.getScore()));

            // 科目
            List<MeasureQuestionBean> questions = new ArrayList<>();
            List<MeasureAnswerBean> answers = new ArrayList<>();
            List<MeasureCategoryBean> categorys = resp.getCategory();
            if (categorys != null) {
                for (MeasureCategoryBean category : categorys) {
                    if (category == null) continue;
                    questions.addAll(category.getQuestions());
                    answers.addAll(category.getAnswers());
                }
                List<MeasureReportCategoryBean> categoryList = getCategorys(questions, answers);
                ((MeasureReportActivity) mContext).showCategory(categoryList);
            }

            // 知识点
            ((MeasureReportActivity) mContext).showNotes(resp.getNotes());
        } else if (EVALUATE.equals(mPaperType)) {
            // 试卷信息
            ((MeasureReportActivity) mContext).showPaperInfo(
                    getPaperType(mPaperType), resp.getExercise_name());

            // 你的分数
            ((MeasureReportActivity) mContext).showYourScore(String.valueOf(resp.getScore()));

            // 科目
            List<MeasureQuestionBean> questions = new ArrayList<>();
            List<MeasureAnswerBean> answers = new ArrayList<>();
            List<MeasureCategoryBean> categorys = resp.getCategory();
            if (categorys != null) {
                for (MeasureCategoryBean category : categorys) {
                    if (category == null) continue;
                    questions.addAll(category.getQuestions());
                    answers.addAll(category.getAnswers());
                }
                List<MeasureReportCategoryBean> categoryList = getCategorys(questions, answers);
                ((MeasureReportActivity) mContext).showCategory(categoryList);
            }

            // 分数线
            ((MeasureReportActivity) mContext).showBorderline(resp.getScores());
        }
    }

    private void showRightAll(List<MeasureAnswerBean> answers) {
        if (answers == null || !(mContext instanceof MeasureReportActivity)) return;
        int rightNum = 0;
        int totalNum = answers.size();
        for (MeasureAnswerBean answer : answers) {
            if (answer == null) continue;
            if (answer.is_right()) rightNum++;
        }
        ((MeasureReportActivity) mContext).showRightAll(rightNum, totalNum);

        // Umeng分享
        mRigthNum = rightNum;
        mTotalNum = totalNum;
    }

    private List<MeasureReportCategoryBean> getCategorys(List<MeasureQuestionBean> questions,
                                                         List<MeasureAnswerBean> answers) {
        List<MeasureReportCategoryBean> categorys = new ArrayList<>();

        if (questions == null || answers == null) return categorys;

        int curCategoryId = 0;
        for (MeasureQuestionBean questionBean : questions) {
            if (questionBean == null) continue;
            if (questionBean.getCategory_id() == curCategoryId) continue;
            curCategoryId = questionBean.getCategory_id();
            MeasureReportCategoryBean categoryBean = new MeasureReportCategoryBean();
            categoryBean.setCategory_id(curCategoryId);
            categoryBean.setCategory_name(questionBean.getCategory_name());
            categorys.add(categoryBean);
        }

        for (MeasureAnswerBean answerBean : answers) {
            if (answerBean == null) continue;
            int answerCategory = answerBean.getCategory();
            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                MeasureReportCategoryBean categoryBean = categorys.get(i);
                if (categoryBean == null) continue;
                if (answerCategory != categoryBean.getCategory_id()) continue;

                // 统计总数
                int totalNum = categoryBean.getTotalNum();
                totalNum++;
                categoryBean.setTotalNum(totalNum);

                // 统计总时长
                int duration = categoryBean.getDuration();
                duration = duration + answerBean.getDuration();
                categoryBean.setDuration(duration);

                // 统计做对题目的数量
                if (answerBean.is_right()) {
                    int rightNum = categoryBean.getRightNum();
                    rightNum++;
                    categoryBean.setRightNum(rightNum);
                }

                categorys.set(i, categoryBean);
            }
        }

        return categorys;
    }

    /**
     * 显示试卷类型
     */
    private String getPaperType(String type) {
        if (AUTO.equals(type)) {
            return "快速智能练习";
        } else if (NOTE.equals(type)) {
            return "专项练习";
        } else if (MOKAO.equals(type)) {
            return "mini模考";
        } else if (COLLECT.equals(type)) {
            return "收藏夹练习";
        } else if (ERROR.equals(type)) {
            return "错题本练习";
        } else if (ENTIRE.equals(type)) {
            return "真题演练";
        } else if (EVALUATE.equals(type)) {
            return "估分";
        } else if (MOCK.equals(type)) {
            return "模考";
        }

        return "";
    }

    /**
     * 设置友盟分享
     */
    public void setUmengShare() {
        GlobalSettingsResp globalSettingsResp = CommonModel.getGlobalSetting(mContext);

        String baseUrl = "http://m.yaoguo.cn/appShare/index.html#/appShare/pr?";
        if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
            baseUrl = globalSettingsResp.getReport_share_url();
        }

        baseUrl = baseUrl + "user_id=" + LoginModel.getUserId()
                + "&user_token=" + LoginModel.getUserToken()
                + "&exercise_id=" + mPaperId
                + "&paper_type=" + mPaperType
                + "&name=" + mPaperName;

        // 练习报告
        String content;
        if ("mokao".equals(mPaperType)) {
            content = "刚刚打败了全国"
                    + Utils.rateToPercent(mDefeat)
                    + "%的小伙伴，学霸非我莫属！";
        } else if ("evaluate".equals(mPaperType)) {
            content = mPaperName
                    + "我估计能"
                    + mScore
                    + "分，快来看看~";
        } else if ("mock".equals(mPaperType)) {
            content = "我在"
                    + mPaperName
                    + "中拿了"
                    + mScore
                    + "分，棒棒哒！";
        } else {
            content = "刷了一套题，正确率竟然达到了"
                    + Utils.getPercent1(mRigthNum, mTotalNum)
                    + "呢~";
        }
        //noinspection ConstantConditions
        UmengManager.UMShareEntity umShareEntity = new UmengManager.UMShareEntity()
                .setTitle("腰果公考")
                .setText(content)
                .setTargetUrl(baseUrl)
                .setSinaWithoutTargetUrl(true)
                .setUmImage(UmengManager.getUMImage(mContext, mScrollView));
        UmengManager.shareAction(
                (Activity) mContext,
                umShareEntity,
                UmengManager.APP_TYPE_QUIZBANK,
                new UmengManager.PlatformInter() {
                    @Override
                    public void platform(SHARE_MEDIA platformType) {

                    }
        });
    }

    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }

}
