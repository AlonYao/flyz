package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

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

    }

    private void initView() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.measure_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("常识"));
        tabLayout.addTab(tabLayout.newTab().setText("言语"));
        tabLayout.addTab(tabLayout.newTab().setText("数量"));
        tabLayout.addTab(tabLayout.newTab().setText("判断"));
        tabLayout.addTab(tabLayout.newTab().setText("资料"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MeasureActivity.this, tab.getText(), Toast.LENGTH_SHORT).show();
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
