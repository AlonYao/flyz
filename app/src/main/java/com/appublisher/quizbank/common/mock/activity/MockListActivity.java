package com.appublisher.quizbank.common.mock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.MultiListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MockListAdapter;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;

import java.util.HashMap;
import java.util.List;

public class MockListActivity extends BaseActivity implements MeasureConstants{

    private MockListAdapter adapter;
    private MultiListView listView;
    private TextView explainTv;
//    private MockPreResp mockPreResp;
    private MockPreResp.MockListBean mMock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_list);

        setToolBar(this);

        String data = getIntent().getStringExtra("data");
        mMock = GsonManager.getModel(data, MockPreResp.MockListBean.class);
        if (mMock == null) return;

        adapter = new MockListAdapter(this, mMock.getPapers());
        explainTv = (TextView) findViewById(R.id.explain_text);
        listView = (MultiListView) findViewById(R.id.listView);

        setValue();
    }

    public void setValue() {
        explainTv.setText(mMock.getDescription());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<MockPreResp.MockListBean.PapersBean> papers = mMock.getPapers();
                if (papers == null || position >= papers.size()) return;
                MockPreResp.MockListBean.PapersBean paper = papers.get(position);
                if (paper == null) return;

                final Intent intent = new Intent(MockListActivity.this, MeasureActivity.class);
                intent.putExtra(INTENT_PAPER_ID, paper.getPaper_id());
                intent.putExtra(INTENT_PAPER_TYPE, MeasureConstants.MOCK);
                intent.putExtra(INTENT_MOCK_TIME, mMock.getStart_time());
                startActivity(intent);

                MeasureModel.saveCacheMockId(MockListActivity.this, mMock.getMock_id());

                finish();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Mocklist", String.valueOf(paper.getPaper_id()));
                UmengManager.onEvent(MockListActivity.this, "Mock", map);
            }
        });
    }
}
