package com.appublisher.quizbank.activity;

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
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;

import java.util.HashMap;
import java.util.List;

public class MockListActivity extends BaseActivity {

    private MockListAdapter adapter;
    private MultiListView listView;
    private TextView explainTv;
    private MockPreResp mockPreResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_list);

        setToolBar(this);

        String data = getIntent().getStringExtra("mock_list");
        mockPreResp = GsonManager.getModel(data, MockPreResp.class);
        if (mockPreResp == null) return;

        adapter = new MockListAdapter(this, mockPreResp.getMock_list());
        explainTv = (TextView) findViewById(R.id.explain_text);
        listView = (MultiListView) findViewById(R.id.listView);

        setValue();
    }

    public void setValue() {
        explainTv.setText(mockPreResp.getList_intro());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<MockPreResp.MockListBean> list = mockPreResp.getMock_list();
                if (list == null || position >= list.size()) return;

                MockPreResp.MockListBean mockListBean = list.get(position);
                if (mockListBean == null) return;

                final Intent intent = new Intent(MockListActivity.this, MeasureActivity.class);
                intent.putExtra(MeasureConstants.INTENT_PAPER_ID, mockListBean.getPaper_id());
                intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.MOCK);
                intent.putExtra(MeasureConstants.INTENT_MOCK_TIME, mockPreResp.getMock_time());
                startActivity(intent);
                finish();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Mocklist", String.valueOf(mockListBean.getPaper_id()));
                UmengManager.onEvent(MockListActivity.this, "Mock", map);
            }
        });
    }
}
