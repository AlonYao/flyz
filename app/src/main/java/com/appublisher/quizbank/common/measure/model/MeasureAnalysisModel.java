package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：解析
 */

public class MeasureAnalysisModel extends MeasureModel{

    public MeasureAnalysisBean mAnalysisBean;
    public boolean mIsErrorOnly;
    public int mSize;

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

    public boolean isShowLastPageAlert(int curPosition, int positionOffsetPixels) {
        return mSize != 0 && (curPosition == mSize - 1 && positionOffsetPixels == 0)
                && (AUTO.equals(mPaperType) || NOTE.equals(mPaperType));
    }

    private void showErrorOnly() {
        if (mAnalysisBean == null || !(mContext instanceof MeasureAnalysisActivity)) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureAnswerBean> answers = new ArrayList<>();

        if (mAnalysisBean.getCategorys() == null || mAnalysisBean.getCategorys().size() == 0) {
            // 非整卷
            List<Integer> wrongIndex = getWrongIndex(mAnalysisBean.getAnswers());
            if (wrongIndex == null || wrongIndex.size() == 0) return;

            // 生成题号
            List<MeasureQuestionBean> originList = mAnalysisBean.getQuestions();
            if (originList == null) return;
            int order = 0;
            int size = originList.size();
            for (int i = 0; i < size; i++) {
                MeasureQuestionBean questionBean = originList.get(i);
                if (questionBean == null) continue;
                order++;
                questionBean.setQuestion_order(order);
                questionBean.setQuestion_amount(size);
                originList.set(i, questionBean);
            }

            // 构造Questions
            for (Integer index : wrongIndex) {
                if (index < 0 || index >= originList.size()) continue;
                questions.add(originList.get(index));
            }

            // 添加索引
            questions = setQuestionIndex(questions);

            // 构造Answers
            answers = getWrongOnlyAnswers(mAnalysisBean.getAnswers());

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
                    question.setDesc_position(i);
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

            // 添加索引
            questions = setQuestionIndex(questions, size);

            // 显示Tab
            ((MeasureAnalysisActivity) mContext).showTabLayout(mTabs);
        }

        ((MeasureAnalysisActivity) mContext).showViewPager(questions, answers);
    }

    private void showAll() {
        if (mAnalysisBean == null || !(mContext instanceof MeasureAnalysisActivity)) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureAnswerBean> answers = new ArrayList<>();

        if (mAnalysisBean.getCategorys() == null || mAnalysisBean.getCategorys().size() == 0) {
            // 非整卷

            // 生成题号&索引
            questions = mAnalysisBean.getQuestions();
            if (questions == null) return;
            int order = 0;
            int size = questions.size();
            for (int i = 0; i < size; i++) {
                MeasureQuestionBean questionBean = questions.get(i);
                if (questionBean == null) continue;
                order++;
                questionBean.setQuestion_order(order);
                questionBean.setQuestion_amount(size);
                questionBean.setQuestion_index(i);
                questions.set(i, questionBean);
            }

            // 构造Answers
            answers = mAnalysisBean.getAnswers();

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

                // 添加说明页
                MeasureQuestionBean question = new MeasureQuestionBean();
                question.setIs_desc(true);
                question.setCategory_name(category.getName());
                question.setDesc_position(i);
                questions.add(question);

                MeasureAnswerBean answerBean = new MeasureAnswerBean();
                answers.add(answerBean);
                answers.addAll(categoryAnswers);

                // 添加题号
                int questionSize = categoryQuestions.size();
                for (int j = 0; j < questionSize; j++) {
                    MeasureQuestionBean questionBean = categoryQuestions.get(j);
                    if (questionBean == null) continue;
                    order++;
                    questionBean.setQuestion_order(order);
                    categoryQuestions.set(j, questionBean);
                }
                questions.addAll(categoryQuestions);
            }

            // 添加索引
            questions = setQuestionIndex(questions, size);

            // 显示Tab
            ((MeasureAnalysisActivity) mContext).showTabLayout(mTabs);
        }

        ((MeasureAnalysisActivity) mContext).showViewPager(questions, answers);
    }

    /**
     * 设置索引&题量
     * @param list MeasureQuestionBean list
     * @return MeasureQuestionBean list
     */
    private List<MeasureQuestionBean> setQuestionIndex(List<MeasureQuestionBean> list) {
        return setQuestionIndex(list, 0);
    }

    /**
     * 设置索引&题量
     * @param list MeasureQuestionBean list
     * @param descSize 说明页数量
     * @return MeasureQuestionBean list
     */
    private List<MeasureQuestionBean> setQuestionIndex(List<MeasureQuestionBean> list,
                                                       int descSize) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
        int amount = size - descSize;
        for (int i = 0; i < size; i++) {
            MeasureQuestionBean questionBean = list.get(i);
            if (questionBean == null) continue;
            questionBean.setQuestion_index(i);
            questionBean.setQuestion_amount(amount);
            list.set(i, questionBean);
        }
        return list;
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

}
