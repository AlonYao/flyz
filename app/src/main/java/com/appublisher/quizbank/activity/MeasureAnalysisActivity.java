package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MeasureAnalysisActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ViewPager mViewPager;

    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_analysis);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 初始化成员变量
        Request request = new Request(this, this);
        mGson = new Gson();

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measure_analysis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {

        Logger.i(response.toString());

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
