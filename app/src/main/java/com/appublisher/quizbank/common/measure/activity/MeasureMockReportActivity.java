package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.model.MeasureMockReportModel;
import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.LineChartViewForMock;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

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

    public void showCategory(List<MeasureReportCategoryBean> list) {
        showCategory(list, FROM_MOCK_REPORT);
    }

    public void showBarChart(float[] lineValues) {
        ViewStub vs = (ViewStub) findViewById(R.id.mock_report_barchart_vs);
        if (vs == null) return;
        vs.inflate();

        String[] lineLabels = new String[]{
                "0", "10", "20", "30", "40", "50", "60", "70", "80", "90"};  // X轴上显示的文字

        BarChartView chartView = (BarChartView) findViewById(R.id.mock_report_barChartView);
        if (chartView == null) return;

        chartView.reset();
        chartView.setChartType(ChartView.ChartType.MOCK_BAR);

        BarSet barSet = new BarSet();
        barSet.addBars(lineLabels, lineValues);
        barSet.setColor(ContextCompat.getColor(this, R.color.themecolor));

        chartView.addData(barSet);
        chartView.setYLabels(AxisController.LabelPosition.NONE);
        chartView.setXAxis(false);
        chartView.setYAxis(false);
        chartView.setBarSpacing(Tools.fromDpToPx(15));
        chartView.show();
    }

    public void showLineChart() {
        LineChartViewForMock yChart =
                (LineChartViewForMock) findViewById(R.id.mock_report_linechart_y);
        if (yChart == null) return;

        yChart.setChartType(ChartView.ChartType.MOCK);
        yChart.reset();

        String[] lineLabelsY = new String[]{"1"};  // X轴上显示的文字
        float[] lineValuesY = new float[]{0};  // 各个点的分值

        Paint lineGridPaintbbb = new Paint();
        lineGridPaintbbb.setColor(ContextCompat.getColor(this, R.color.common_line));
        lineGridPaintbbb.setStyle(Paint.Style.STROKE);
        lineGridPaintbbb.setAntiAlias(true);
        lineGridPaintbbb.setStrokeWidth(Tools.fromDpToPx(.75f));

        LineSet dataSetbbb = new LineSet();
        dataSetbbb.addPoints(lineLabelsY, lineValuesY);
        dataSetbbb.setSmooth(true);
        dataSetbbb.setDashed(false);
        dataSetbbb.setDots(false)
                .setDotsColor(ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setDotsRadius(Tools.fromDpToPx(4))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(
                        ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setLineColor(ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setLineThickness(Tools.fromDpToPx(3))
                .beginAt(0).endAt(1);
        yChart.addData(dataSetbbb);

        yChart.setBorderSpacing(Tools.fromDpToPx(0))
                .setGrid(LineChartView.GridType.NONE, lineGridPaintbbb)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(0, 10, 2)
                .show();


        LineChartViewForMock chart =
                (LineChartViewForMock) findViewById(R.id.mock_report_linechart);
        if (chart == null) return;

        chart.setLineAmount(2);
        chart.setChartType(ChartView.ChartType.MOCK);
        chart.reset();

        // 根据值绘图
        String[] lineLabels = new String[]{
                "09/18", "09/18", "09/18", "09/18", "09/18", "09/18",
                "09/18", "09/18", "09/18", "09/18", "09/18", ""};  // X轴上显示的文字

        float[] lineValues = new float[]{
                1, 2, 3, 4, 5, 6,
                1, 2, 3, 4, 5, 6};  // 各个点的分值

        Paint lineGridPaint = new Paint();
        lineGridPaint.setColor(ContextCompat.getColor(this, R.color.common_line));
        lineGridPaint.setStyle(Paint.Style.STROKE);
        lineGridPaint.setAntiAlias(true);
        lineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        LineSet dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues);
        dataSet.setSmooth(false);
        dataSet.setDashed(false);
        dataSet.setDots(true)
                .setDotsColor(ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setDotsRadius(Tools.fromDpToPx(2))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(
                        ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setLineColor(ContextCompat.getColor(this, R.color.evaluation_diagram_line))
                .setLineThickness(Tools.fromDpToPx(2))
                .beginAt(0).endAt(11);
        chart.addData(dataSet);

        lineValues = new float[]{
                8, 9, 10, 7, 6, 5,
                8, 9, 10, 7, 6, 5};  // 各个点的分值

        dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues);
        dataSet.setSmooth(false);
        dataSet.setDashed(false);
        dataSet.setDots(true)
                .setDotsColor(Color.parseColor("#4E90F5"))
                .setDotsRadius(Tools.fromDpToPx(2))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#4E90F5"))
                .setLineColor(Color.parseColor("#4E90F5"))
                .setLineThickness(Tools.fromDpToPx(2))
                .beginAt(0).endAt(11);
        chart.addData(dataSet);

        chart.setBorderSpacing(Tools.fromDpToPx(0))
                .setGrid(LineChartView.GridType.HORIZONTAL, lineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setAxisBorderValues(0, 10, 2)
                .show();
    }

}
