package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.appublisher.lib_basic.HomeButtonManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.PracticeReportModel;
import com.appublisher.quizbank.model.entity.measure.MeasureEntity;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.UMengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习报告Activity
 */
public class PracticeReportActivity extends ActionBarActivity implements RequestCallback {

    public int mHierarchyId;
    public int mHierarchyLevel;
    public LinearLayout mLlCategoryContainer;
    public LinearLayout mLlNoteContainer;
    public LinearLayout mLlRatio;
    public LinearLayout mLlBorderLine;
    public LinearLayout mLlEvaluate;
    public String mPaperName;
    public String mPaperType;
    public String mFrom;
    public String mPaperTime;
    public TextView mTvPaperName;
    public TextView mTvRightNum;
    public TextView mTvTotalNum;
    public TextView mTvAll;
    public TextView mTvError;
    public TextView mTvPaperType;
    public TextView mTvMiniMokaoRank;
    public TextView mTvEvaluateNum;
    public TextView mTvEntireInfoRank;
    public TextView mTvEntireInfoScore;
    public ImageView mIvNoteNoChange;
    public RelativeLayout mRlMiniMokao;
    public RelativeLayout mRlEntireInfo;
    public ScrollView mSvMain;
    public View parentView;
    public int mRightNum;
    public int mTotalNum;
    public int mPaperId;
    public int mExerciseId;
    public boolean mIsFromError;
    public float mScore;
    public float mDefeat;
    public ArrayList<NoteM> mNotes;
    public ArrayList<QuestionM> mQuestions;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public HashMap<String, HashMap<String, Object>> mCategoryMap;
    public MeasureEntity mMeasureEntity;

    /**
     * Umeng
     */
    public boolean mUmengIsPressHome;
    public long mUmengTimestamp;
    public String mUmengStatus; // 1：未看 2：全部 3：错题
    public String mUmengEntry;
    private HomeButtonManager mHomeWatcher;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_report);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mTvPaperName = (TextView) findViewById(R.id.practice_report_name);
        mTvRightNum = (TextView) findViewById(R.id.practice_report_rightnum);
        mTvTotalNum = (TextView) findViewById(R.id.practice_report_totalnum);
        mIvNoteNoChange = (ImageView) findViewById(R.id.practice_report_notenochange);
        mLlCategoryContainer =
                (LinearLayout) findViewById(R.id.practice_report_category_container);
        mLlNoteContainer = (LinearLayout) findViewById(R.id.practice_report_note_container);
        mTvAll = (TextView) findViewById(R.id.practice_report_all);
        mTvError = (TextView) findViewById(R.id.practice_report_error);
        mTvPaperType = (TextView) findViewById(R.id.practice_report_type);
        mRlMiniMokao = (RelativeLayout) findViewById(R.id.practice_report_minimokao);
        mTvMiniMokaoRank = (TextView) findViewById(R.id.practice_report_minimokao_rank);
        mLlRatio = (LinearLayout) findViewById(R.id.practice_report_ratio);
        mLlBorderLine = (LinearLayout) findViewById(R.id.practice_report_borderline);
        mLlEvaluate = (LinearLayout) findViewById(R.id.practice_report_evaluate);
        mTvEvaluateNum = (TextView) findViewById(R.id.practice_report_evaluate_num);
        mSvMain = (ScrollView) findViewById(R.id.practice_report_sv);
        parentView = findViewById(R.id.parentView);
        mRlEntireInfo = (RelativeLayout) findViewById(R.id.practice_report_entire_info);
        mTvEntireInfoRank = (TextView) findViewById(R.id.entire_info_rank);
        mTvEntireInfoScore = (TextView) findViewById(R.id.entire_info_score);

        // 成员变量初始化
        mUmengStatus = "1";
        mHomeWatcher = new HomeButtonManager(this);

        // 获取数据
        mFrom = getIntent().getStringExtra("from");
        mHierarchyId = getIntent().getIntExtra("hierarchy_id", 0);
        mHierarchyLevel = getIntent().getIntExtra("hierarchy_level", 0);
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mPaperTime = getIntent().getStringExtra("paper_time");
        if (mPaperTime == null) mPaperTime = Utils.DateToString(new Date(), "yyyy/MM/dd");
        mUmengTimestamp = getIntent().getLongExtra("umeng_timestamp", System.currentTimeMillis());
        mMeasureEntity = (MeasureEntity) getIntent().getSerializableExtra("measure_entity");
        mPaperId = getIntent().getIntExtra("paper_id", 0);

        // 获取exercise_id，现在有两个来源，后续版本会统一整理到MeasureEntity中
        mExerciseId = getIntent().getIntExtra("exercise_id", 0);
        if (mExerciseId == 0 && mMeasureEntity != null) {
            mExerciseId = mMeasureEntity.getExercise_id();
        }

        // 显示考试类型
        PracticeReportModel.showPaperType(this);

        // 显示考试描述
        PracticeReportModel.showPaperDesc(this);

        if ("study_record".equals(mFrom)
                || "mokao_homepage".equals(mFrom)
                || "mokao_history_list".equals(mFrom)
                || "mock_list".equals(mFrom)
                || "mock".equals(mFrom)) {
            // 从学习记录、mini模考、历史模考、估分模考进入，需要重新获取数据
            ProgressDialogManager.showProgressDialog(this, true);
            new QRequest(this, this).getHistoryExerciseDetail(mExerciseId, mPaperType);
        } else {
            PracticeReportModel.getData(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng 统计时长处理
        if (mUmengIsPressHome) {
            // 如果已经按过Home键后，再次回到App，更新参数状态
            mUmengEntry = "Continue";
            mUmengTimestamp = System.currentTimeMillis();
            mUmengIsPressHome = false;
        }

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeButtonManager.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                mUmengIsPressHome = true;
                UmengManager.onEvent(PracticeReportActivity.this, "Back");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

        // Umeng
        MobclickAgent.onPageStart("PracticeReportActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

        // Umeng
        MobclickAgent.onPageEnd("PracticeReportActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        final Map<String, String> um_map = new HashMap<String, String>();
        um_map.put("Analysis", mUmengStatus);
        UmengManager.onEvent(this, "Report", um_map);
    }

    @Override
    public void onBackPressed() {
        UMengManager.checkUmengShare(this);
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
            UMengManager.checkUmengShare(this);
        } else if ("分享".equals(item.getTitle())) {
            PracticeReportModel.setUmengShare(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("history_exercise_detail".equals(apiName))
            PracticeReportModel.dealHistoryExerciseDetailResp(this, response);

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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        PracticeReportModel.updateNotice(this);
    }
}
