package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MeasureAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.utils.AlertManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 做题
 */
public class MeasureActivity extends ActionBarActivity {

    public int mScreenHeight;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ViewPager mViewPager;
    public long mCurTimestamp;

    private int mDuration;
    private HashMap<String, Object> mUserAnswerMap;
    private int mCurPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        mCurTimestamp = System.currentTimeMillis();
        mCurPosition = 0;

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 初始化用户答案
        int size = 10;
        mUserAnswerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();
            mUserAnswerList.add(map);
        }

        // 设置ViewPager
        MeasureAdapter measureAdapter = new MeasureAdapter(this);
        mViewPager.setAdapter(measureAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveQuestionTime();
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("暂停").setIcon(R.drawable.measure_icon_pause),
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getTitle().equals("暂停")) {
            saveQuestionTime();
            AlertManager.pauseAlert(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存做题时间
     */
    private void saveQuestionTime() {
        mDuration = (int) ((System.currentTimeMillis() - mCurTimestamp) / 1000);
        mCurTimestamp = System.currentTimeMillis();
        mUserAnswerMap = mUserAnswerList.get(mCurPosition);

        if (mUserAnswerMap.containsKey("duration")) {
            mDuration = mDuration + (int) mUserAnswerMap.get("duration");
        } else {
            mUserAnswerMap.put("duration", mDuration);
        }

        mUserAnswerList.set(mCurPosition, mUserAnswerMap);
    }
}
