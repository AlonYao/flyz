package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.ExpandableHeightGridView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.AnswerSheetAdapter;
import com.appublisher.quizbank.model.business.AnswerSheetModel;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.entity.measure.MeasureEntity;
import com.appublisher.quizbank.model.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.SubmitPaperResp;
import com.appublisher.quizbank.network.QRequest;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 答题卡
 */
public class AnswerSheetActivity extends BaseActivity implements RequestCallback {

    private String mPaperName;
    private ExpandableHeightGridView mGridView;

    public int mTotalNum;
    public int mRightNum;
    public int mPaperId;
    public String mFrom;
    public String mPaperType;
    public String mMockTime;
    public LinearLayout mLlEntireContainer;
    public HashMap<String, HashMap<String, Object>> mCategoryMap;
    public ArrayList<HashMap<String, Object>> mUserAnswerList;
    public ArrayList<HashMap<String, Integer>> mEntirePaperCategory;

    public long mUmengTimestamp;
    public String mUmengEntry;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_sheet);

        // Toolbar
        setToolBar(this);

        // View 初始化
        mGridView = (ExpandableHeightGridView) findViewById(R.id.answer_sheet_gv);
        mLlEntireContainer = (LinearLayout) findViewById(R.id.answer_sheet_entire_container);
        TextView tvSubmit = (TextView) findViewById(R.id.answer_sheet_submit);
        ScrollView sv = (ScrollView) findViewById(R.id.answer_sheet_sv);
        ScrollView svEntire = (ScrollView) findViewById(R.id.answer_sheet_sv_entire);

        // 成员变量初始化

        // 获取数据
        mUserAnswerList = (ArrayList<HashMap<String, Object>>)
                getIntent().getSerializableExtra("user_answer");
        mPaperName = getIntent().getStringExtra("paper_name");
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mEntirePaperCategory = (ArrayList<HashMap<String, Integer>>)
                getIntent().getSerializableExtra("category");
        mFrom = getIntent().getStringExtra("from");
        mUmengTimestamp = getIntent().getLongExtra("umeng_timestamp", 0);
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mMockTime = getIntent().getStringExtra("mock_time");

        // 整卷、非整卷 显示不同的页面
        if (mEntirePaperCategory != null && mEntirePaperCategory.size() != 0) {
            // 整卷
            svEntire.setVisibility(View.VISIBLE);
            sv.setVisibility(View.GONE);
            AnswerSheetModel.setEntireContent(this);

        } else {
            // 非整卷
            svEntire.setVisibility(View.GONE);
            sv.setVisibility(View.VISIBLE);
            setContent();
        }

        // 交卷
        if ("analysis".equals(mFrom)) {
            tvSubmit.setVisibility(View.GONE);
            tvSubmit.setOnClickListener(null);
        } else {
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("mockpre".equals(mFrom)) {
                        ProgressDialogManager.showProgressDialog(AnswerSheetActivity.this);
                        new QRequest(AnswerSheetActivity.this, AnswerSheetActivity.this)
                                .getServerCurrentTime();
                    } else if ("vip".equals(mPaperType)) {
                        // 小班系统特殊处理
//                        AnswerSheetModel.(AnswerSheetActivity.this);
                    } else {
                        AnswerSheetModel.submitPaper(AnswerSheetActivity.this);
                    }
                }
            });
        }
    }

    /**
     * 设置内容
     */
    private void setContent() {
        if (mUserAnswerList == null || mUserAnswerList.size() == 0) return;

        AnswerSheetAdapter answerSheetAdapter = new AnswerSheetAdapter(this, mUserAnswerList);
        mGridView.setAdapter(answerSheetAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class<?> cls;
                if ("analysis".equals(mFrom)) {
                    cls = MeasureAnalysisActivity.class;
                } else {
                    cls = MeasureActivity.class;
                }

                Intent intent = new Intent(AnswerSheetActivity.this, cls);
                intent.putExtra("position", position);
                setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理提交试卷的回调
     * @param response 回调
     */
    private void dealSubmitPaperResp(JSONObject response) {
        if (response == null) return;

        SubmitPaperResp submitPaperResp =
                GsonManager.getModel(response.toString(), SubmitPaperResp.class);

        if (submitPaperResp == null || submitPaperResp.getResponse_code() != 1) return;

        ArrayList<NoteM> notes = submitPaperResp.getNotes();

        MeasureEntity measureEntity = new MeasureEntity();
        measureEntity.setDefeat(submitPaperResp.getDefeat());
        measureEntity.setScore(submitPaperResp.getScore());
        measureEntity.setScores(submitPaperResp.getScores());
        measureEntity.setExercise_id(submitPaperResp.getExercise_id());
        measureEntity.setAvg_score(submitPaperResp.getAvg_score());
        Intent intent = new Intent(AnswerSheetActivity.this, MeasureActivity.class);
        intent.putExtra("notes", notes);
        intent.putExtra("paper_name", mPaperName);
        intent.putExtra("right_num", mRightNum);
        intent.putExtra("total_num", mTotalNum);
        intent.putExtra("category", mCategoryMap);
        intent.putExtra("measure_entity", measureEntity);
        setResult(ActivitySkipConstants.ANSWER_SHEET_SUBMIT, intent);

        finish();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        switch (apiName) {
            case "submit_paper":
                dealSubmitPaperResp(response);
                break;

            case "server_current_time":
                ServerCurrentTimeResp resp = GsonManager.getModel(
                        response.toString(), ServerCurrentTimeResp.class);
                AnswerSheetModel.dealServerCurrentTimeResp(resp, this);
                break;
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
