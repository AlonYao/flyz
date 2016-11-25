package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：练习报告
 */

public class MeasureReportModel extends MeasureModel {

    public MeasureAnalysisBean mAnalysisBean;

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
    public String getPaperType(String type) {
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
}
