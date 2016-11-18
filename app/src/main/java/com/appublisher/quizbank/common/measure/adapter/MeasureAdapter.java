package com.appublisher.quizbank.common.measure.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureItemFragment;
import com.appublisher.quizbank.common.measure.fragment.MeasureTabDescFragment;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureAdapter extends FragmentStatePagerAdapter{

    private List<MeasureQuestionBean> mQuestions;

    public MeasureAdapter(FragmentManager fm, List<MeasureQuestionBean> questions) {
        super(fm);
        mQuestions = questions;
    }

    @Override
    public Fragment getItem(int position) {
        MeasureQuestionBean measureQuestionBean = mQuestions.get(position);
        if (measureQuestionBean != null && measureQuestionBean.is_desc()) {
            // Tab说明页
            return MeasureTabDescFragment.newInstance(
                    measureQuestionBean.getCategory_name(), measureQuestionBean.getDesc_position());
        } else {
            // 题目页面
            String question = GsonManager.modelToString(mQuestions.get(position));
            return MeasureItemFragment.newInstance(question, position);
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

    public List<MeasureQuestionBean> getQuestions() {
        return mQuestions;
    }
}
