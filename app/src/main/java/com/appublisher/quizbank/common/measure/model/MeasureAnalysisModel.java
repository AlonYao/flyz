package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureExcludeBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureEntireResp;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：解析
 */

public class MeasureAnalysisModel extends MeasureModel{

    public MeasureAnalysisBean mAnalysisBean;
    public boolean mIsErrorOnly;

    public MeasureAnalysisModel(Context context) {
        super(context);
    }

    public void showContent() {
        if (!(mContext instanceof MeasureAnalysisActivity)) return;
        if (mIsErrorOnly) {
            showErrorOnly();
        } else {
            showAll();
        }
    }

    private void showErrorOnly() {
        if (mAnalysisBean == null) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureAnswerBean> answers = new ArrayList<>();

        if (mAnalysisBean.getCategorys() == null || mAnalysisBean.getCategorys().size() == 0) {
            // 非整卷
        } else {
            // 整卷
            int order = 0;
            int size = mAnalysisBean.getCategorys().size();
            for (int i = 0; i < size; i++) {
                MeasureCategoryBean category = mAnalysisBean.getCategorys().get(i);
                if (category == null) continue;

                List<MeasureQuestionBean> categoryQuestions = category.getQuestions();
                if (categoryQuestions == null) continue;
                List<MeasureAnswerBean> categoryAnswers = category.getAnswers();
                if (categoryAnswers == null) continue;

                // 添加Tab数据
                MeasureTabBean tabBean = new MeasureTabBean();
                tabBean.setName(category.getName());
                tabBean.setPosition(questions.size());
                mTabs.add(tabBean);

                // 添加题目数据，构造说明页，如果全部作对则构造全部作对提示页面
                MeasureQuestionBean question = new MeasureQuestionBean();
                List<Integer> wrongIndex = getWrongIndex(categoryAnswers);

                if (wrongIndex == null || wrongIndex.size() == 0) {
                    // 全对
                    question.setIs_allright(true);
                    questions.add(question);
                    // 递增题号
                    order = order + categoryQuestions.size();
                } else {
                    // 添加说明页
                    question.setIs_desc(true);
                    question.setCategory_name(category.getName());
                    question.setDesc_position(i);
                    questions.add(question);

                    // 添加题号
                    int questionSize = categoryQuestions.size();
                    for (int j = 0; j < questionSize; j++) {
                        MeasureQuestionBean questionBean = categoryQuestions.get(j);
                        if (questionBean == null) continue;
                        order++;
                        questionBean.setQuestion_order(order);
                        categoryQuestions.set(j, questionBean);
                    }

                    // 筛选错题
                    List<MeasureQuestionBean> tempList = new ArrayList<>();
                    for (Integer index : wrongIndex) {
                        if (index < 0 || index >= categoryQuestions.size()) continue;
                        tempList.add(categoryQuestions.get(index));
                    }
                    questions.addAll(tempList);
                }

                // 筛选只包含错题的用户答案
                answers.add(new MeasureAnswerBean()); // 保证与questions一一对应
                List<MeasureAnswerBean> tempAnswers = getWrongOnlyAnswers(categoryAnswers);
                answers.addAll(tempAnswers);
            }
        }
    }

    private void showAll() {

    }

    private boolean isAllRight(List<MeasureAnswerBean> answers) {
        if (answers == null || answers.size() == 0) return true;
        for (MeasureAnswerBean answer : answers) {
            if (answer == null) continue;
            if (!answer.is_right()) return false;
        }
        return true;
    }

    private List<Integer> getWrongIndex(List<MeasureAnswerBean> answers) {
        List<Integer> list = new ArrayList<>();
        if (answers == null || answers.size() == 0) return list;

        int size = answers.size();
        for (int i = 0; i < size; i++) {
            MeasureAnswerBean answer = answers.get(i);
            if (answer == null) continue;
            if (!answer.is_right()) list.add(i);
        }

        return list;
    }

    private List<MeasureAnswerBean> getWrongOnlyAnswers(List<MeasureAnswerBean> answers) {
        List<MeasureAnswerBean> list = new ArrayList<>();
        if (answers == null) return list;
        for (MeasureAnswerBean answer : answers) {
            if (answer == null) continue;
            if (!answer.is_right()) list.add(answer);
        }
        return list;
    }

    /**
     * 设置题号&索引(同时初始化用户记录)
     * @param descSize 说明页面的数量
     * @param list List<MeasureQuestionBean>
     * @return List<MeasureQuestionBean>
     */
    private List<MeasureQuestionBean> setQuestionOrder(List<MeasureQuestionBean> list,
                                                       int descSize) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
        int amount = size - descSize;
        int order = 0;

        for (int i = 0; i < size; i++) {
            // 设置索引
            MeasureQuestionBean measureQuestionBean = list.get(i);
            if (measureQuestionBean == null) continue;
            measureQuestionBean.setQuestion_index(i);

            // 设置题号
            if (measureQuestionBean.is_desc()) continue;
            order++;
            measureQuestionBean.setQuestion_order(order);
            measureQuestionBean.setQuestion_amount(amount);
            list.set(i, measureQuestionBean);
        }

        return list;
    }

}
