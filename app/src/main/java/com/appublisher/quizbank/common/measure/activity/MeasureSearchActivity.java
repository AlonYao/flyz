package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

public class MeasureSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_search);
        setToolBar(this);
        initView();
    }

    private void initView() {

    }
}
