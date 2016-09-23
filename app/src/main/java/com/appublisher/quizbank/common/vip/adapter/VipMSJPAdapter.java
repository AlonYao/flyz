package com.appublisher.quizbank.common.vip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appublisher.quizbank.common.vip.fragment.VipMSJPMaterialFragment;
import com.appublisher.quizbank.common.vip.fragment.VipMSJPQuestionFragment;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

/**
 * 小班：名师精批
 */
public class VipMSJPAdapter extends FragmentPagerAdapter{

    private VipMSJPResp mVipMSJPResp;

    public VipMSJPAdapter(FragmentManager fm, VipMSJPResp resp) {
        super(fm);
        mVipMSJPResp = resp;
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
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return VipMSJPQuestionFragment.newInstance();

            case 1:
                return VipMSJPMaterialFragment.newInstance(mVipMSJPResp);
        }
        return null;
    }

}
