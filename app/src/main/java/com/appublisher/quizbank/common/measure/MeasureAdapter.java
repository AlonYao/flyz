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
        String question = GsonManager.modelToString(mQuestions.get(position));
        return MeasureItemFragment.newInstance(question, position, getCount());
    }

    @Override
    public int getCount() {
        return mQuestions == null ? 0 : mQuestions.size();
    }

}
