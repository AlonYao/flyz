package com.appublisher.quizbank.common.vip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appublisher.quizbank.common.vip.fragment.VipHPTSMaterialFragment;
import com.appublisher.quizbank.common.vip.fragment.VipHPTSQuestionFragment;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;

/**
 * 小班：互评提升
 */

public class VipHPTSAdapter extends FragmentStatePagerAdapter{

    private VipHPTSResp mResp;
    private VipHPTSQuestionFragment mQuestionFragment;

    public VipHPTSAdapter(FragmentManager fm, VipHPTSResp resp) {
        super(fm);
        mResp = resp;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mQuestionFragment == null) {
                    mQuestionFragment = VipHPTSQuestionFragment.newInstance(mResp);
                }
                return mQuestionFragment;

            case 1:
                return VipHPTSMaterialFragment.newInstance(mResp);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "问题";
        } else if (position == 1) {
            return "材料";
        }
        return super.getPageTitle(position);
    }
}
