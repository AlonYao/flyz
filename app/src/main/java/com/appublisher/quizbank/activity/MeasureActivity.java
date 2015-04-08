package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MeasureAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;

/**
 * 做题
 */
public class MeasureActivity extends ActionBarActivity {

    public int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        ViewPager viewPager = (ViewPager) findViewById(R.id.measure_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 设置ViewPager
        MeasureAdapter measureAdapter = new MeasureAdapter(this);
        viewPager.setAdapter(measureAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
