package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.PracticeReportModel;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 练习报告Activity
 */
public class PracticeReportActivity extends ActionBarActivity implements RequestCallback{

    public int mHierarchyId;
    public int mHierarchyLevel;
    public LinearLayout mLlCategoryContainer;
    public LinearLayout mLlNoteContainer;
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
    public ImageView mIvNoteNoChange;

    public int mRightNum;
    public int mTotalNum;
    public boolean mIsFromError;
    public ArrayList<NoteM> mNotes;
    public ArrayList<QuestionM> mQuestions;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public HashMap<String, HashMap<String, Object>> mCategoryMap;

    /** Umeng */
    public boolean mUmengIsPressHome;
    public long mUmengTimestamp;
    public String mUmengStatus; // 1：未看 2：全部 3：错题
    public String mUmengEntry;
    private HomeWatcher mHomeWatcher;

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

        // 成员变量初始化
        mUmengStatus = "1";
        mHomeWatcher = new HomeWatcher(this);

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

        // 显示考试类型
        PracticeReportModel.showPaperType(this);

        // 显示考试描述
        PracticeReportModel.showPaperDesc(this);

        if ("study_record".equals(mFrom)
                || "mokao_homepage".equals(mFrom)
                || "mokao_history_list".equals(mFrom)) {
            int exerciseId = getIntent().getIntExtra("exercise_id", 0);

            ProgressDialogManager.showProgressDialog(this, true);
            new Request(this, this).getHistoryExerciseDetail(exerciseId, mPaperType);
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
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                mUmengIsPressHome = true;
                UmengManager.sendToUmeng(PracticeReportActivity.this, "Back");
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
        UmengManager.sendCountEvent(this, "Report", "Analysis", mUmengStatus);
    }

    @Override
    public void onBackPressed() {
        // Umeng
        UmengManager.sendToUmeng(this, "Back");

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Umeng
            UmengManager.sendToUmeng(this, "Back");

            finish();
        }

        return super.onOptionsItemSelected(item);
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
}
