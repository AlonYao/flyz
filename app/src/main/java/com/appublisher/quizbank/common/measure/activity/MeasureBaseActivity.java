package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

import java.util.List;

public class MeasureBaseActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private boolean mIsFromClick;
    private MeasureModel mModel;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showTabLayout(List<MeasureTabBean> tabs) {
        if (tabs == null) return;
        ViewStub vs = (ViewStub) findViewById(R.id.measure_tablayout_viewstub);
        if (vs == null) return;
        vs.inflate();

        mTabLayout = (TabLayout) findViewById(R.id.measure_tablayout);
        if (mTabLayout == null) return;

        for (MeasureTabBean tab : tabs) {
            if (tab == null) continue;
            String name = tab.getName();
            if (name.length() > 2) {
                name = name.substring(0, 2);
            }
            mTabLayout.addTab(mTabLayout.newTab().setText(name));
        }

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mIsFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    mIsFromClick = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mIsFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    mIsFromClick = false;
                }
            }
        });

        // 设置click事件
        setTabLayoutOnItemClick();
    }

    private void setTabLayoutOnItemClick() {
        if (mTabLayout == null) return;

        ViewGroup root = (ViewGroup) mTabLayout.getChildAt(0);
        if (root == null) return;

        int size = mTabLayout.getTabCount();
        for (int i = 0; i < size; i++) {
            View tabView = root.getChildAt(i);
            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsFromClick = true;
                }
            });
        }
    }

    private void pageSkipFromTabClick(int tabPosition) {
        if (mViewPager == null || mModel == null) return;
        mViewPager.setCurrentItem(mModel.getPositionByTab(tabPosition));
    }

    public void scrollTabLayout(int position) {
        if (mTabLayout == null || mModel == null) return;

        int scrollToPositon = mModel.getTabPositionScrollTo(position);
        int curTabPotision = mTabLayout.getSelectedTabPosition();
        if (scrollToPositon != curTabPotision) {
            TabLayout.Tab tab = mTabLayout.getTabAt(scrollToPositon);
            if (tab == null) return;
            tab.select();
        }
    }

    public void setModel(MeasureModel model) {
        this.mModel = model;
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
    }
}
