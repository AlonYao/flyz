package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

import java.util.List;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends BaseActivity {

    public static final String PAPER_TYPE = "paper_type";
    public static final String HIERARCHY_ID = "hierarchy_id";

    public ViewPager mViewPager;
    public MeasureModel mModel;

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
        showLoading();
        mModel.getData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
    }

    public void showViewPager(List<MeasureQuestion> questions) {
        MeasureAdapter adapter = new MeasureAdapter(getSupportFragmentManager(), questions);
        mViewPager.setAdapter(adapter);
    }
}
