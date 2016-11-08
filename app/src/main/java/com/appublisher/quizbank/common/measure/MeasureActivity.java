package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewStub;
import android.widget.Toast;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

import java.util.List;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends BaseActivity implements MeasureConstants{

    public ViewPager mViewPager;
    public MeasureModel mModel;
    public MeasureAdapter mAdapter;

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
        mModel.mPaperId = getIntent().getIntExtra(PAPER_ID, 0);
        showLoading();
        mModel.getData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
    }

    public void showViewPager(List<MeasureQuestion> questions) {
        mAdapter = new MeasureAdapter(getSupportFragmentManager(), questions);
        mViewPager.setAdapter(mAdapter);
    }

    public void showTabLayout(List<MeasureTabBean> tabs) {
        if (tabs == null) return;
        ViewStub vs = (ViewStub) findViewById(R.id.measure_tablayout_viewstub);
        if (vs == null) return;
        vs.inflate();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.measure_tablayout);
        if (tabLayout == null) return;
        for (MeasureTabBean tab : tabs) {
            if (tab == null) continue;
            String name = tab.getName();
            if (name.length() > 2) {
                name = name.substring(0, 2);
            }
            tabLayout.addTab(tabLayout.newTab().setText(name));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Toast.makeText(
                            MeasureActivity.this,
                            String.valueOf(tab.getPosition()),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }
}
