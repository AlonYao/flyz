package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipHPTSAdapter;
import com.appublisher.quizbank.common.vip.model.VipHPTSModel;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;

import java.util.HashMap;

public class VipHPTSActivity extends VipBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipHPTSModel mModel;

    // Umeng
    private long mUMTimeStamp;
    private int mUMSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_hpts);
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
        map.put("Switch", String.valueOf(mUMSwitch));
        UmengManager.onEventValue(this, "Huping", map, dur);
    }

    private void initData() {
        mModel = new VipHPTSModel(this);
        showLoading();
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        mModel.getExerciseDetail();
        // Umeng
        mUMTimeStamp = System.currentTimeMillis();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.vip_hpts_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vip_hpts_viewpager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mUMSwitch++;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void showContent(VipHPTSResp resp) {
        VipHPTSAdapter adapter = new VipHPTSAdapter(getSupportFragmentManager(), resp);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}
