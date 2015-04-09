package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MeasureAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.AutoTrainingResp;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 做题
 */
public class MeasureActivity extends ActionBarActivity implements RequestCallback{

    public int mScreenHeight;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ViewPager mViewPager;
    public long mCurTimestamp;
    public int mCurPosition;

    private Request mRequest;
    private Gson mGson;

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
        mRequest = new Request(this, this);
        mGson = new Gson();

        // 获取ToolBar高度
        int toolBarHeight = MeasureModel.getViewHeight(toolbar);

        // 获取屏幕高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels - 50 - toolBarHeight; // 50是状态栏高度

        // 获取数据
        String flag = getIntent().getStringExtra("flag");
        if ("auto_training".equals(flag)) {
            ProgressDialogManager.showProgressDialog(this);
            mRequest.getAutoTraining();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("答题卡").setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("暂停").setIcon(
                R.drawable.measure_icon_pause), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getTitle().equals("暂停")) {
            saveQuestionTime();
            AlertManager.pauseAlert(this);
        } else if (item.getTitle().equals("答题卡")) {
            Intent intent = new Intent(this, AnswerSheetActivity.class);
            intent.putExtra("user_answer", mUserAnswerList);
            startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 答题卡回调
        if (resultCode == ActivitySkipConstants.ANSWER_SHEET_SKIP && data != null) {
            int position = data.getIntExtra("position", 0);

            if (mViewPager == null) return;
            mViewPager.setCurrentItem(position);
        }
    }

    /**
     * 保存做题时间
     */
    private void saveQuestionTime() {
        int duration = (int) ((System.currentTimeMillis() - mCurTimestamp) / 1000);
        mCurTimestamp = System.currentTimeMillis();
        HashMap<String, Object> userAnswerMap = mUserAnswerList.get(mCurPosition);

        if (userAnswerMap.containsKey("duration")) {
            duration = duration + (int) userAnswerMap.get("duration");
        }

        userAnswerMap.put("duration", duration);
        mUserAnswerList.set(mCurPosition, userAnswerMap);
    }

    /**
     * 设置内容
     * @param autoTrainingResp 快速智能练习回调
     */
    private void setContent(AutoTrainingResp autoTrainingResp) {
        ArrayList<QuestionM> questions = autoTrainingResp.getQuestions();

        if (questions == null) return;

        // 初始化用户答案
        int size = questions.size();
        mUserAnswerList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();
            mUserAnswerList.add(map);
        }

        // 设置ViewPager
        MeasureAdapter measureAdapter = new MeasureAdapter(this, questions);
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
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if ("auto_training".equals(apiName)) {
            AutoTrainingResp autoTrainingResp = mGson.fromJson(
                    response.toString(), AutoTrainingResp.class);

            if (autoTrainingResp.getResponse_code() == 1) {
                setContent(autoTrainingResp);
            } else {
                ToastManager.showToast(this, getString(R.string.netdata_error));
            }
        }

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
