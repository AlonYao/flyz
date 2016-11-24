package com.appublisher.quizbank.common.measure.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureAnalysisItemFragment;
import com.appublisher.quizbank.common.measure.fragment.MeasureTabAllRightFragment;
import com.appublisher.quizbank.common.measure.fragment.MeasureTabDescFragment;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureAnalysisAdapter extends FragmentStatePagerAdapter{

    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureAnswerBean> mAnswers;

    public MeasureAnalysisAdapter(FragmentManager fm,
                                  List<MeasureQuestionBean> questions,
                                  List<MeasureAnswerBean> answers) {
        super(fm);
        mQuestions = questions;
        mAnswers = answers;
    }

    @Override
    public Fragment getItem(int position) {
        MeasureQuestionBean measureQuestionBean = mQuestions.get(position);
        if (measureQuestionBean != null && measureQuestionBean.is_desc()) {
            // Tab说明页
            return MeasureTabDescFragment.newInstance(
                    measureQuestionBean.getCategory_name(), measureQuestionBean.getDesc_position());
        } else if (measureQuestionBean != null && measureQuestionBean.is_allright()) {
            // Tab说明页（科目全对提示页面）
            return MeasureTabAllRightFragment.newInstance();
        } else {
            // 题目页面
            String question = GsonManager.modelToString(mQuestions.get(position));
            String answer = GsonManager.modelToString(mAnswers.get(position));
            return MeasureAnalysisItemFragment.newInstance(question, answer, position);
        }
    }

    @Override
    public int getCount() {
        return mQuestions == null ? 0 : mQuestions.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
