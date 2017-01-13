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
 * 面试模块
 */

public class InterviewDetailAdapter extends FragmentStatePagerAdapter {

    private List<InterviewPaperDetailResp.QuestionsBean> mList;
    private final InterviewPaperDetailActivity mActivity;
    private final String questionType;

    public InterviewDetailAdapter(FragmentManager fm, List<InterviewPaperDetailResp.QuestionsBean> list, InterviewPaperDetailActivity activity,String questionFrom) {
        super(fm);
        mList = list;
        mActivity = activity;
        questionType = questionFrom;      // 问题的类型
    }

    @Override
    public Fragment getItem(int position) {

        InterviewPaperDetailResp.QuestionsBean bean = mList.get(position);    // 具体的哪一道题
        String questionbean = GsonManager.modelToString(mList.get(position));

        int listLength = mList.size();
        if (hasPurchasedAction(bean)) {
            return InterviewPurchasedFragment.newInstance(
                    questionbean, position, listLength,questionType);       // 已付费页面
        } else {
            return InterviewUnPurchasedFragment.newInstance(
                    questionbean, position, listLength, questionType);    // 未付费页面
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    private boolean hasPurchasedAction(InterviewPaperDetailResp.QuestionsBean bean) {
        // 判断是否开启完整版
        if (mActivity != null) {
            InterviewPaperDetailResp.AllAudioBean allAudioBean = mActivity.getAllAudioBean();
            if (allAudioBean != null && allAudioBean.is_purchased()) {
                return true;
            }
        }

        // 判断是否有单次购买
        return bean != null && bean.isPurchased_audio();
    }

}
