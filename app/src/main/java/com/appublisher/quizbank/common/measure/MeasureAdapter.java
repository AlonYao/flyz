package com.appublisher.quizbank.common.measure;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.lib_basic.gson.GsonManager;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureAdapter extends FragmentStatePagerAdapter{

    private List<MeasureQuestion> mQuestions;

    public MeasureAdapter(FragmentManager fm, List<MeasureQuestion> questions) {
        super(fm);
        mQuestions = questions;
    }

    @Override
    public Fragment getItem(int position) {
        MeasureQuestion measureQuestion = mQuestions.get(position);
        if (measureQuestion != null && measureQuestion.is_desc()) {
            // Tab说明页
            return MeasureTabDescFragment.newInstance(
                    measureQuestion.getCategory_name(), measureQuestion.getDesc_position());
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
}
