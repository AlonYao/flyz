package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipMSJPAdapter;
import com.appublisher.quizbank.common.vip.fragment.VipMSJPQuestionFragment;
import com.appublisher.quizbank.common.vip.model.VipMSJPModel;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

/**
 * 小班：名师精批
 */
public class VipMSJPActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipMSJPAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_msjp);
        setToolBar(this);
        initData();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VipMSJPQuestionFragment fragment =
                (VipMSJPQuestionFragment) mAdapter.getItem(0);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_msjp_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_msjp_viewpager);
    }

    private void initData() {
        VipMSJPModel model = new VipMSJPModel(this);
        showLoading();
        model.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        model.getExerciseDetail();
    }

    public void showContent(VipMSJPResp resp) {
        mAdapter = new VipMSJPAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void showLoading() {
        ProgressDialogManager.showProgressDialog(this);
    }
}
