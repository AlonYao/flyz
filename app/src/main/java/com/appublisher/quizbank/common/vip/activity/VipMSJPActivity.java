package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

public class VipMSJPActivity extends BaseActivity {

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_msjp);
        setToolBar(this);
        initData();
        initView();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_msjp_tablayout);

        mTabLayout.addTab(mTabLayout.newTab().setText("问题"));
        mTabLayout.addTab(mTabLayout.newTab().setText("材料"));
    }

    private void initData() {

    }
}
