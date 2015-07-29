package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.EvaluationModel;
import com.appublisher.quizbank.model.entity.umeng.UMShareContentEntity;
import com.appublisher.quizbank.model.entity.umeng.UMShareUrlEntity;
import com.appublisher.quizbank.model.entity.umeng.UmengShareEntity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.db.chart.view.LineChartView;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 能力评估
 */
public class EvaluationActivity extends ActionBarActivity implements RequestCallback{

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

    public int mLearningDays;
    public int mScore;
    public float mRank;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("分享"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if ("分享".equals(item.getTitle())) {
            // 构造友盟分享实体
            UmengShareEntity umengShareEntity = new UmengShareEntity();
            umengShareEntity.setActivity(this);
            umengShareEntity.setBitmap(Utils.getBitmapByView(mSvMain));

            // 友盟分享文字处理
            UMShareContentEntity umShareContentEntity = new UMShareContentEntity();
            umShareContentEntity.setType("evaluation");
            umShareContentEntity.setLearningDays(mLearningDays);
            umShareContentEntity.setExamName(LoginModel.getUserExamName());
            umShareContentEntity.setScore(mScore);
            umShareContentEntity.setRank(mRank);
            umengShareEntity.setContent(UmengManager.getShareContent(umShareContentEntity));

            // 友盟分享跳转链接处理
            UMShareUrlEntity urlEntity = new UMShareUrlEntity();
            urlEntity.setType("evaluation");
            urlEntity.setUser_id(LoginModel.getUserId());
            urlEntity.setUser_token(LoginModel.getUserToken());
            umengShareEntity.setUrl(UmengManager.getUrl(urlEntity));

            UmengManager.openShare(umengShareEntity);
        }

        return super.onOptionsItemSelected(item);
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
