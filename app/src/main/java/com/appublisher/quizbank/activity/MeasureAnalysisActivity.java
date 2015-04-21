package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.MeasureAnalysisResp;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeasureAnalysisActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_analysis);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_analysis_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        Request request = new Request(this, this);

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        String analysisType = getIntent().getStringExtra("analysis_type");
        if ("collect".equals(analysisType) || "error".equals(analysisType)) {
            int hierarchy_id = getIntent().getIntExtra("hierarchy_id", 0);
            int hierarchy_level = getIntent().getIntExtra("hierarchy_level", 0);

            switch (hierarchy_level) {
                case 1:
                    ProgressDialogManager.showProgressDialog(this, true);
                    request.collectErrorQuestions(
                            String.valueOf(hierarchy_id), "", "", analysisType);
                    break;

                case 2:
                    ProgressDialogManager.showProgressDialog(this, true);
                    request.collectErrorQuestions(
                            "", String.valueOf(hierarchy_id), "", analysisType);
                    break;

                case 3:
                    ProgressDialogManager.showProgressDialog(this, true);
                    request.collectErrorQuestions(
                            "", "", String.valueOf(hierarchy_id), analysisType);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("收藏").setIcon(
                R.drawable.measure_analysis_uncollect), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("反馈").setIcon(
                R.drawable.measure_analysis_feedback), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if ("收藏".equals(item.getTitle())) {

        } else if ("反馈".equals(item.getTitle())) {

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理解析回调
     * @param response 解析数据回调
     */
    private void dealMeasureAnalysisResp(JSONObject response) {
        if (response == null) return;

        Gson gson = new Gson();
        MeasureAnalysisResp measureAnalysisResp =
                gson.fromJson(response.toString(), MeasureAnalysisResp.class);

        if (measureAnalysisResp == null || measureAnalysisResp.getResponse_code() != 1) return;
        ArrayList<QuestionM> questions = measureAnalysisResp.getQuestions();

        if (questions == null || questions.size() == 0) return;

        MeasureAnalysisAdapter adapter = new MeasureAnalysisAdapter(
                this,
                questions,
                measureAnalysisResp.getAnswers());
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("collect_error_questions".equals(apiName)) dealMeasureAnalysisResp(response);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
