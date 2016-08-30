package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MockListAdapter;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.mock.MockListResp;
import com.appublisher.quizbank.model.netdata.mock.MockPaperM;
import com.appublisher.quizbank.network.QRequest;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 模考&估分列表页面
 */
public class MockActivity extends ActionBarActivity implements
        RequestCallback, AdapterView.OnItemClickListener{

    private ListView mLvMock;
    private ImageView mIvNull;
    private ArrayList<MockPaperM> mMockPapers;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        // Toolbar
        CommonModel.setToolBar(this);

        // 获取数据
        String title = getIntent().getStringExtra("title");
        CommonModel.setBarTitle(this, title);

        // 成员变量初始化
        QRequest QRequest = new QRequest(this, this);

        // View 初始化
        mLvMock = (ListView) findViewById(R.id.mock_lv);
        mIvNull = (ImageView) findViewById(R.id.quizbank_null);

        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        QRequest.getMockExerciseList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("MockActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("MockActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("mock_exercise_list".equals(apiName)) dealMockListResp(response);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
//        ToastManager.showOvertimeToash(this);
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMockPapers == null || position >= mMockPapers.size()) return;

        MockPaperM mockPaper = mMockPapers.get(position);

        if (mockPaper == null) return;

        Intent intent = new Intent(this, PracticeDescriptionActivity.class);
        intent.putExtra("paper_type", mType);
        intent.putExtra("paper_name", mockPaper.getName());
        intent.putExtra("redo", false);
        intent.putExtra("paper_id", mockPaper.getId());
        intent.putExtra("umeng_entry", "Home");
        startActivity(intent);
    }

    /**
     * 处理模考&估分列表数据回调
     * @param response 回调数据
     */
    private void dealMockListResp(JSONObject response) {
        if (response == null) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        MockListResp mockListResp = GsonManager.getModel(response.toString(), MockListResp.class);

        if (mockListResp == null || mockListResp.getResponse_code() != 1) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        mMockPapers = mockListResp.getPaper_list();
        mType = mockListResp.getType();

        if (mMockPapers == null || mMockPapers.size() == 0) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        } else {
            mIvNull.setVisibility(View.GONE);
        }

        MockListAdapter mockListAdapter = new MockListAdapter(this, mMockPapers);
        mLvMock.setAdapter(mockListAdapter);
        mLvMock.setOnItemClickListener(this);
    }

}
