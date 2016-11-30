package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipDTTPAdapter;
import com.appublisher.quizbank.common.vip.fragment.VipDTTPQuestionFragment;
import com.appublisher.quizbank.common.vip.model.VipDTTPModel;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

import java.util.HashMap;

public class VipDTTPActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipDTTPAdapter mAdapter;
    private VipDTTPModel mModel;
    private long mUMTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_dttp);
        setToolBar(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        int dur = (int) ((System.currentTimeMillis() - mUMTimeStamp) / 1000);
        HashMap<String, String> map = new HashMap<>();
        UmengManager.onEventValue(this, "Danti", map, dur);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VipDTTPQuestionFragment fragment =
                (VipDTTPQuestionFragment) mAdapter.getItem(0);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initData() {
        mModel = new VipDTTPModel(this);
        showLoading();
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        mModel.getExerciseDetail();
        // Umeng
        mUMTimeStamp = System.currentTimeMillis();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_dttp_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_dttp_viewpager);
    }

    public void showContent(VipDTTPResp resp) {
        mAdapter = new VipDTTPAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
