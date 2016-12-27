package com.appublisher.quizbank.common.interview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.fragment.InterviewPurchasedFragment;
import com.appublisher.quizbank.common.interview.fragment.InterviewUnPurchasedFragment;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.util.List;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewDetailAdapter extends FragmentStatePagerAdapter {

    private List<InterviewPaperDetailResp.QuestionsBean> mList;
    private boolean isPurchased;
    private final InterviewPaperDetailActivity mActivity;

    public InterviewDetailAdapter(FragmentManager fm, List<InterviewPaperDetailResp.QuestionsBean> list, InterviewPaperDetailActivity activity) {
        super(fm);
        mList = list;
        mActivity = activity;
    }


    @Override
    public Fragment getItem(int position) {
        InterviewPaperDetailResp.QuestionsBean bean = mList.get(position);
        String questionbean = GsonManager.modelToString(mList.get(position));
        int listLength = mList.size();
        boolean isPurchased_audio = bean.isPurchased_audio();   // 是否为单次购买
        boolean isPurchased_review = bean.isPurchased_review();   // 是否为全部购买
        boolean is_collected = bean.isPurchased_review();   // 是否收藏

        if (bean != null) {
            if (isPurchased_audio == true && isPurchased_review == true) {
                return InterviewPurchasedFragment.newInstance(questionbean, position, listLength);       // 已付费页面
            } else {
                return InterviewUnPurchasedFragment.newInstance(questionbean, position, listLength, mActivity);    // 未付费页面
            }
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

}
