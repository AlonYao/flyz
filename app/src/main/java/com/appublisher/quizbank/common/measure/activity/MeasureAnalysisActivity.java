package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureAnalysisBean;
import com.appublisher.quizbank.common.measure.model.MeasureAnalysisModel;

public class MeasureAnalysisActivity extends BaseActivity implements MeasureConstants{

    private ViewPager mViewPager;
    public MeasureAnalysisModel mModel;
    public MeasureAnalysisAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_analysis);
        setToolBar(this);
        initView();
        initData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_analysis_viewpager);
    }

    private void initData() {
        mModel = new MeasureAnalysisModel(this);
        mModel.mAnalysisBean = GsonManager.getModel(
                getIntent().getStringExtra(INTENT_ANALYSIS_BEAN), MeasureAnalysisBean.class);
        mModel.mIsErrorOnly = getIntent().getBooleanExtra(INTENT_ANALYSIS_IS_ERROR_ONLY, false);
        mModel.showContent();
        mAdapter = new MeasureAnalysisAdapter(getSupportFragmentManager());
    }
}
