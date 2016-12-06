package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.GuFenListAdapter;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.model.netdata.mock.GufenM;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;

import java.util.HashMap;
import java.util.List;

/**
 * 模考&估分列表页面
 */
public class GuFenListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mLvMock;
    private ImageView mIvNull;
    private List<GufenM.PaperListBean> mMockPapers;
    private MockGufenResp mockGufenResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gufen_list);

        // Toolbar
        setToolBar(this);

        // 获取数据
        String data = getIntent().getStringExtra("mock_gufen");
        mockGufenResp = GsonManager.getModel(data, MockGufenResp.class);

        // View 初始化
        mLvMock = (ListView) findViewById(R.id.mock_lv);
        mIvNull = (ImageView) findViewById(R.id.quizbank_null);

        setValue();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMockPapers == null || position >= mMockPapers.size()) return;

        GufenM.PaperListBean paperListBean = mMockPapers.get(position);

        if (paperListBean == null) return;

        Intent intent = new Intent(this, MeasureActivity.class);
        intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.EVALUATE);
        intent.putExtra(MeasureConstants.INTENT_PAPER_ID, paperListBean.getId());
        startActivity(intent);

        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", String.valueOf(paperListBean.getId()));
        UmengManager.onEvent(this, "Evaluate", map);
    }


    private void setValue() {
        if (mockGufenResp == null) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        }

        mMockPapers = mockGufenResp.getGufen().getPaper_list();

        if (mMockPapers == null || mMockPapers.size() == 0) {
            mIvNull.setVisibility(View.VISIBLE);
            return;
        } else {
            mIvNull.setVisibility(View.GONE);
        }

        GuFenListAdapter guFenListAdapter = new GuFenListAdapter(this, mMockPapers);
        mLvMock.setAdapter(guFenListAdapter);
        mLvMock.setOnItemClickListener(this);
    }

}
