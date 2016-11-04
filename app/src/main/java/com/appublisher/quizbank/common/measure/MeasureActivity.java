package com.appublisher.quizbank.common.measure;

import android.os.Bundle;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
//        MeasureAdapter measureAdapter = new MeasureAdapter(getSupportFragmentManager())
    }

    private void initView() {

    }
}
