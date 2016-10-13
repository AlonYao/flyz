package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipHPTSAdapter;
import com.appublisher.quizbank.common.vip.model.VipHPTSModel;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;

public class VipHPTSActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipHPTSAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_hpts);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
        VipHPTSModel model = new VipHPTSModel(this);
        showLoading();
        model.getExerciseDetail();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_hpts_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_hpts_viewpager);
    }

    public void showLoading() {
        ProgressDialogManager.showProgressDialog(this);
    }

    public void showContent(VipHPTSResp resp) {
        mAdapter = new VipHPTSAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
