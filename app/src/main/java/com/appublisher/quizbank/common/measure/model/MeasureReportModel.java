package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：练习报告
 */

public class MeasureReportModel extends MeasureModel {

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
        if (!(mContext instanceof MeasureReportActivity)) return;

        // 试卷信息
        ((MeasureReportActivity) mContext).showPaperInfo(
                getPaperType(mPaperType), resp.getExercise_name());

        // 做对/全部
        showRightAll(resp.getAnswers());

        // 科目
        List<MeasureCategoryBean> categorys = getCategorys(resp.getQuestions(), resp.getAnswers());
        ((MeasureReportActivity) mContext).showCategory(categorys);

        // 知识点
        ((MeasureReportActivity) mContext).showNotes(resp.getNotes());
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

    private List<MeasureCategoryBean> getCategorys(List<MeasureQuestionBean> questions,
                                                   List<MeasureAnswerBean> answers) {
        List<MeasureCategoryBean> categorys = new ArrayList<>();

        if (questions == null || answers == null) return categorys;

        int curCategoryId = 0;
        for (MeasureQuestionBean questionBean : questions) {
            if (questionBean == null) continue;
            if (questionBean.getCategory_id() == curCategoryId) continue;
            curCategoryId = questionBean.getCategory_id();
            MeasureCategoryBean categoryBean = new MeasureCategoryBean();
            categoryBean.setCategory_id(curCategoryId);
            categoryBean.setCategory_name(questionBean.getCategory_name());
            categorys.add(categoryBean);
        }

        for (MeasureAnswerBean answerBean : answers) {
            if (answerBean == null) continue;
            int answerCategory = answerBean.getCategory();
            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                MeasureCategoryBean categoryBean = categorys.get(i);
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
