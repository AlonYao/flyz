package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipMSJPAdapter;
import com.appublisher.quizbank.common.vip.model.VipMSJPModel;

/**
 * 小班：名师精批
 */
public class VipMSJPActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VipMSJPModel mModel;

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
        mViewPager = (ViewPager) findViewById(R.id.vip_msjp_viewpager);

        VipMSJPAdapter adapter = new VipMSJPAdapter(this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initData() {
        mModel = new VipMSJPModel(this);
    }

    /**
     * 显示材料
     * @param material 材料
     */
    public void showMaterial(String material) {
        
    }
}
