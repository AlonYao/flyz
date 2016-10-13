package com.appublisher.quizbank.common.vip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appublisher.quizbank.common.vip.fragment.VipDTTPMaterialFragment;
import com.appublisher.quizbank.common.vip.fragment.VipDTTPQuestionFragment;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

/**
 * 小班：单题突破
 */

public class VipDTTPAdapter extends FragmentPagerAdapter{

    private VipDTTPResp mResp;
    private VipDTTPQuestionFragment mQuestionFragment;

    public VipDTTPAdapter(FragmentManager fm, VipDTTPResp resp) {
        super(fm);
        mResp = resp;
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

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mQuestionFragment == null) {
                    mQuestionFragment = VipDTTPQuestionFragment.newInstance(mResp);
                }
                return mQuestionFragment;

            case 1:
                return VipDTTPMaterialFragment.newInstance(mResp);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
