package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.EvaluationModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.db.chart.view.LineChartView;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.sso.UMSsoHandler;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 能力评估
 */
public class EvaluationActivity extends ActionBarActivity implements RequestCallback {

    public LineChartView mLineChart;
    public TextView mTvScore;
    public TextView mTvRank;
    public TextView mTvLearningDays;
    public TextView mTvTotalTime;
    public TextView mTvTotalQuestions;
    public TextView mTvAvarageQuestions;
    public TextView mTvAccuracy;
    public TextView mTvAvarageAccuracy;
    public TextView mTvSummarySource;
    public TextView mTvCalculationBasis;
    public TextView mTvSummaryDate;
    public LinearLayout mLlHistory;
    public ScrollView mSvMain;
    public View parentView;
    public int mLearningDays;
    public int mScore;
    public float mRank;
    public LinearLayout mContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mLineChart = (LineChartView) findViewById(R.id.linechart);
        mTvScore = (TextView) findViewById(R.id.evaluation_score_tv);
        mTvRank = (TextView) findViewById(R.id.evaluation_rank_tv);
        mTvLearningDays = (TextView) findViewById(R.id.evaluation_learningdays_tv);
        mTvTotalTime = (TextView) findViewById(R.id.evaluation_totaltime_tv);
        mTvTotalQuestions = (TextView) findViewById(R.id.evaluation_totalquestions_tv);
        mTvAvarageQuestions = (TextView) findViewById(R.id.evaluation_avaragequestions_tv);
        mTvAccuracy = (TextView) findViewById(R.id.evaluation_accuracy_tv);
        mTvAvarageAccuracy = (TextView) findViewById(R.id.evaluation_avarageaccuracy_tv);
        mLlHistory = (LinearLayout) findViewById(R.id.evaluation_history_ll);
        mTvSummarySource = (TextView) findViewById(R.id.evaluation_summarysource);
        mTvCalculationBasis = (TextView) findViewById(R.id.evaluation_calculationbasis);
        mTvSummaryDate = (TextView) findViewById(R.id.evaluation_summarydate);
        mSvMain = (ScrollView) findViewById(R.id.evaluation_sv);
        parentView = findViewById(R.id.baseView);
        mContainer = (LinearLayout)findViewById(R.id.category_container);
        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        new Request(this, this).getEvaluation();

        // Umeng
        UmengManager.sendCountEvent(this, "Mine", "Entry", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("EvaluationActivity");
        MobclickAgent.onResume(this);
        // TalkingData
        TCAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("EvaluationActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = UmengManager.mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("分享").setIcon(R.drawable.quiz_share), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            UmengManager.checkUmengShare(this);
        } else if ("分享".equals(item.getTitle())) {
            EvaluationModel.setUmengShare(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        UmengManager.checkUmengShare(this);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("evaluation".equals(apiName)) EvaluationModel.dealEvaluationResp(this, response);

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
