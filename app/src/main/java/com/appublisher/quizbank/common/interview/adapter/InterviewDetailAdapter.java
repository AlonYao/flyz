package com.appublisher.quizbank.common.interview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.quizbank.common.interview.fragment.InterviewPurchasedFragment;
import com.appublisher.quizbank.common.interview.fragment.InterviewUnPurchasedFragment;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.util.List;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewDetailAdapter extends FragmentStatePagerAdapter {

    private List<InterviewPaperDetailResp.QuestionsBean> mList;

    public InterviewDetailAdapter(FragmentManager fm,
                                  List<InterviewPaperDetailResp.QuestionsBean> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        boolean isPurchased = false;

        if (isPurchased) {
            return InterviewPurchasedFragment.newInstance();
        } else {
            return InterviewUnPurchasedFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

}
