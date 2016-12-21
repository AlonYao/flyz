package com.appublisher.quizbank.common.interview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.lib_basic.gson.GsonManager;
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

    public InterviewDetailAdapter(FragmentManager fm,
                                  List<InterviewPaperDetailResp.QuestionsBean> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        InterviewPaperDetailResp.QuestionsBean bean = mList.get(position);
        String questionbean = GsonManager.modelToString(mList.get(position));
        int listLength = mList.size();

        isPurchased = false;
        if (bean != null ) {
            if(isPurchased){
                return InterviewPurchasedFragment.newInstance(questionbean,position,listLength);
            }else{
                return InterviewUnPurchasedFragment.newInstance(questionbean,position,listLength);
            }
        } else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

}
