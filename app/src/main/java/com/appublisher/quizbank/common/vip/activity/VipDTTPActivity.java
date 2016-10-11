package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipDTTPAdapter;
import com.appublisher.quizbank.common.vip.model.VipDTTPModel;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

public class VipDTTPActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipDTTPModel mModel;
    private VipDTTPAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_dttp);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
        mModel = new VipDTTPModel(this);
        showLoading();

    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_msjp_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_msjp_viewpager);
    }

    public void showContent(VipDTTPResp resp) {
        mAdapter = new VipDTTPAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void showLoading() {
        ProgressDialogManager.showProgressDialog(this);
    }
}
