package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

import java.util.List;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends BaseActivity implements MeasureConstants{

    public ViewPager mViewPager;
    public MeasureModel mModel;
    public MeasureAdapter mAdapter;
    public TabLayout mTabLayout;

    private boolean isFromClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
        mModel = new MeasureModel(this);
        mModel.mPaperType = getIntent().getStringExtra(PAPER_TYPE);
        mModel.mHierarchyId = getIntent().getIntExtra(HIERARCHY_ID, 0);
        mModel.mPaperId = getIntent().getIntExtra(PAPER_ID, 0);
        showLoading();
        mModel.getData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
    }

    public void showViewPager(List<MeasureQuestion> questions) {
        mAdapter = new MeasureAdapter(getSupportFragmentManager(), questions);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                scrollTabLayout(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void scrollTabLayout(int position) {
        if (mTabLayout == null) return;
        int scrollToPositon = mModel.getTabPositionScrollTo(position);
        int curTabPotision = mTabLayout.getSelectedTabPosition();
        if (scrollToPositon != curTabPotision) {
            TabLayout.Tab tab = mTabLayout.getTabAt(scrollToPositon);
            if (tab == null) return;
            tab.select();
        }
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
                if (isFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    isFromClick = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (isFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    isFromClick = false;
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
                    isFromClick = true;
                }
            });
        }
    }

    private void pageSkipFromTabClick(int tabPosition) {
        mViewPager.setCurrentItem(mModel.getPositionByTab(tabPosition));
    }
}
