package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;
import com.appublisher.quizbank.common.measure.network.MeasureParamBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：解析
 */

public class MeasureAnalysisModel extends MeasureModel{

    public MeasureAnalysisBean mAnalysisBean;
    public boolean mIsErrorOnly;
    public boolean mIsFromFolder;
    public boolean mIsFromSearch;
    public int mSize;

    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureAnswerBean> mAnswers;

    public MeasureAnalysisModel(Context context) {
        super(context);
    }

    public int getSize() {
       return mSize;
    }

    public void getData() {
        if (mIsFromFolder && (COLLECT.equals(mPaperType) || ERROR.equals(mPaperType))) {
            if (mContext instanceof BaseActivity) ((BaseActivity) mContext).showLoading();
            mRequest.getCollectErrorQuestions(mHierarchyId, mPaperType);
        } else {
            showContent();
        }
    }

    private void showContent() {
        if (!(mContext instanceof MeasureAnalysisActivity)) return;
        if (mIsErrorOnly) {
            showErrorOnly();
        } else {
            showAll();
        }
    }

    private void showErrorOnly() {
        if (mAnalysisBean == null || !(mContext instanceof MeasureAnalysisActivity)) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();
        mAnswers = new ArrayList<>();

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
            int index = 0;
            for (MeasureQuestionBean question : questions) {
                if (question == null) continue;
                question.setQuestion_index(index);
                index++;
            }

            // 构造Answers
            mAnswers = getWrongOnlyAnswers(mAnalysisBean.getAnswers());

        } else {
            // 整卷
            int order = 0;
            int amount = 0;
            int size = mAnalysisBean.getCategorys().size();
            for (int i = 0; i < size; i++) {
                MeasureCategoryBean category = mAnalysisBean.getCategorys().get(i);
                if (category == null) continue;

                List<MeasureQuestionBean> categoryQuestions = category.getQuestions();
                if (categoryQuestions == null) continue;
                List<MeasureAnswerBean> categoryAnswers = category.getAnswers();
                if (categoryAnswers == null) continue;

                // 统计原有题目总数
                amount = amount + category.getQuestions().size();

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
                mAnswers.add(new MeasureAnswerBean()); // 保证与questions一一对应
                List<MeasureAnswerBean> tempAnswers = getWrongOnlyAnswers(categoryAnswers);
                mAnswers.addAll(tempAnswers);
            }

            // 添加索引
            questions = setQuestionIndex(questions, amount);

            // 显示Tab
            ((MeasureAnalysisActivity) mContext).showTabLayout(mTabs);
        }

        mSize = questions.size();
        mQuestions = questions;

        ((MeasureAnalysisActivity) mContext).showViewPager(questions, mAnswers);
    }

    private void showAll() {
        if (mAnalysisBean == null || !(mContext instanceof MeasureAnalysisActivity)) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();
        mAnswers = new ArrayList<>();

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
            mAnswers = mAnalysisBean.getAnswers();

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
                mAnswers.add(answerBean);
                mAnswers.addAll(categoryAnswers);

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
            int amount = questions.size() - size;
            questions = setQuestionIndex(questions, amount);

            // 显示Tab
            ((MeasureAnalysisActivity) mContext).showTabLayout(mTabs);
        }

        mSize = questions.size();
        mQuestions = questions;

        ((MeasureAnalysisActivity) mContext).showViewPager(questions, mAnswers);
    }

    /**
     * 设置索引&题量
     * @param list MeasureQuestionBean list
     * @param amount 题量
     * @return MeasureQuestionBean list
     */
    private List<MeasureQuestionBean> setQuestionIndex(List<MeasureQuestionBean> list,
                                                       int amount) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
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

    public boolean isCollected(int position) {
        if (mAnswers == null || position >= mAnswers.size()) return false;
        MeasureAnswerBean answerBean = mAnswers.get(position);
        return answerBean != null && answerBean.is_collected();
    }

    public void setCollected(int position, boolean isCollected) {
        if (position < 0) return;

        // answers处理
        if (mAnswers == null) {
            // 重新构造
            if (mQuestions == null) return;
            mAnswers = new ArrayList<>();
            int size = mQuestions.size();
            for (int i = 0; i < size; i++) {
                MeasureAnswerBean answerBean = new MeasureAnswerBean();
                mAnswers.add(answerBean);
            }
        }

        if (position >= mAnswers.size()) return;
        MeasureAnswerBean answerBean = mAnswers.get(position);
        if (answerBean == null) return;

        // id处理
        int id = answerBean.getId();
        if (id == 0) {
            // 从Questions中获取
            if (mQuestions == null || position >= mQuestions.size()) return;
            MeasureQuestionBean questionBean = mQuestions.get(position);
            if (questionBean == null) return;
            id = questionBean.getId();
            if (id == 0) return;
            // 更新Answers
            answerBean.setId(id);
            mAnswers.set(position, answerBean);
        }

        // 提交数据
        mRequest.collectQuestion(
                MeasureParamBuilder.collectQuestion(id, isCollected));
        answerBean.setIs_collected(isCollected);
        mAnswers.set(position, answerBean);

        // 刷新
        if (mContext instanceof MeasureAnalysisActivity) {
            ((MeasureAnalysisActivity) mContext).invalidateOptionsMenu();
        }
    }

    private void dealCollectErrorQuestions(JSONObject response) {
        MeasureHistoryResp resp = GsonManager.getModel(response, MeasureHistoryResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        mAnalysisBean = new MeasureAnalysisBean();
        mAnalysisBean.setQuestions(resp.getQuestions());
        mAnalysisBean.setAnswers(resp.getAnswers());
        showContent();
    }

    public int getCurQuestionId(int position) {
        if (mQuestions == null || position < 0 || position >= mQuestions.size()) return 0;
        MeasureQuestionBean questionBean = mQuestions.get(position);
        if (questionBean == null) return 0;
        return questionBean.getId();
    }

    public boolean isDescPage(int position) {
        if (mQuestions == null || position < 0 || position >= mQuestions.size()) return false;
        MeasureQuestionBean questionBean = mQuestions.get(position);
        return questionBean != null && questionBean.is_desc();
    }

    public boolean isShowAnother() {
        return AUTO.equals(mPaperType) || NOTE.equals(mPaperType);
    }

    /**
     * 获取加载在Adapter里面的Questions数据
     * @return List<MeasureQuestionBean>
     */
    public List<MeasureQuestionBean> getAdapterQuestions() {
        if (!(mContext instanceof MeasureAnalysisActivity)) return null;
        if (((MeasureAnalysisActivity) mContext).mAdapter == null) return null;
        return ((MeasureAnalysisActivity) mContext).mAdapter.getQuestions();
    }

    /**
     * 获取加载在Adapter里面的Answers数据
     * @return List<MeasureAnswerBean>
     */
    public List<MeasureAnswerBean> getAdapterAnswers() {
        if (!(mContext instanceof MeasureAnalysisActivity)) return null;
        if (((MeasureAnalysisActivity) mContext).mAdapter == null) return null;
        return ((MeasureAnalysisActivity) mContext).mAdapter.getAnswers();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (COLLECT_ERROR_QUESTIONS.equals(apiName)) {
            dealCollectErrorQuestions(response);
        }
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

}
