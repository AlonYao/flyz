package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.model.MeasureMockReportModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MeasureMockReportActivity extends MeasureReportBaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, MeasureConstants, View.OnClickListener{

    private static final String MENU_SHARE = "分享";
    private static final int START_REFRESH = 1;

    private MeasureMockReportModel mModel;
    private TextView mTvName;
    private TextView mTvScore;
    private TextView mTvAvgDur;

    public SwipeRefreshLayout mSwipeRefreshLayout;
    public MsgHandler mHandler;

    public static class MsgHandler extends Handler {
        private WeakReference<MeasureMockReportActivity> mActivity;

        MsgHandler(MeasureMockReportActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final MeasureMockReportActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case START_REFRESH:
                        // 显示时间
                        activity.mSwipeRefreshLayout.setRefreshing(true);
                        break;
                }
            }
        }
    }

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mock_report_all) {
            // 全部
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(INTENT_ANALYSIS_BEAN, GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(INTENT_PAPER_TYPE, MOCK);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "All");
            UmengManager.onEvent(this, "Report", map);

        } else if (v.getId() == R.id.mock_report_error) {
            // 错题
            if (mModel.isAllRight()) return;
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(INTENT_ANALYSIS_BEAN, GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(INTENT_ANALYSIS_IS_ERROR_ONLY, true);
            intent.putExtra(INTENT_PAPER_TYPE, MOCK);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Error");
            UmengManager.onEvent(this, "Report", map);
        }
    }

    private void initData() {
        mModel = new MeasureMockReportModel(this);
        mHandler = new MsgHandler(this);
        mModel.mPaperId = 2627;
        mModel.mPaperType = MOCK;
        mModel.getData();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mock_report_srl);
        mTvName = (TextView) findViewById(R.id.mock_report_name);
        mTvScore = (TextView) findViewById(R.id.mock_report_score);
        mTvAvgDur = (TextView) findViewById(R.id.mock_report_statistics_avg_duration);
        Button btnAll = (Button) findViewById(R.id.mock_report_all);
        Button btnError = (Button) findViewById(R.id.mock_report_error);

        if (btnAll != null) {
            btnAll.setOnClickListener(this);
        }

        if (btnError != null) {
            btnError.setOnClickListener(this);
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.themecolor);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }

    @Override
    public void onRefresh() {
        mModel.getData();
    }

    public void startRefresh() {
        mHandler.sendEmptyMessage(START_REFRESH);
    }

    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showMockName(String name) {
        mTvName.setText(name);
    }

    public void showScore(String score) {
        mTvScore.setText(score);
    }

    public void showAvgDur(String dur) {
        dur = dur + "秒";
        mTvAvgDur.setText(dur);
    }

}
