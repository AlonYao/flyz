package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipHPTSAdapter;
import com.appublisher.quizbank.common.vip.model.VipHPTSModel;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;

public class VipHPTSActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipHPTSModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_hpts);
        setToolBar(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.sendToUmeng();
    }

    private void initData() {
        mModel = new VipHPTSModel(this);
        showLoading();
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        mModel.getExerciseDetail();
        // Umeng
        mModel.mUMBegin = System.currentTimeMillis();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_hpts_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_hpts_viewpager);
    }

    public void showContent(VipHPTSResp resp) {
        VipHPTSAdapter adapter = new VipHPTSAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
