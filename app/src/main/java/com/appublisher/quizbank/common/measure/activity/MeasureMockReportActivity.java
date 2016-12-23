package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

public class MeasureMockReportActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener{

    private static final String MENU_SHARE = "分享";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_mock_report);
        setToolBar(this);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(
                menu.add(MENU_SHARE).setIcon(R.drawable.quiz_share),
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (MENU_SHARE.equals(item.getTitle())) {
            // Empty
        }

        return false;
    }

    private void initData() {

    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mock_report_srl);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.themecolor);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
