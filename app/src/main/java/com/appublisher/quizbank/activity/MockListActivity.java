package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.MockListAdapter;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;

public class MockListActivity extends BaseActivity {

    private MockListAdapter adapter;
    private ListView listView;
    private TextView explainTv;
    private MockPreResp mockPreResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_list);

        setToolBar(this);

        String data = getIntent().getStringExtra("mock_list");
        mockPreResp = GsonManager.getModel(data, MockPreResp.class);

        adapter = new MockListAdapter(this, mockPreResp.getMock_list());
        explainTv = (TextView) findViewById(R.id.explain_text);
        listView = (ListView) findViewById(R.id.listView);

        setValue();
    }

    public void setValue() {
        explainTv.setText(mockPreResp.getList_intro());
        Logger.i("list_size" + mockPreResp.getMock_list().size());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Intent intent = new Intent(MockListActivity.this, LegacyMeasureActivity.class);
                intent.putExtra("from", "mockpre");
                intent.putExtra("paper_id", mockPreResp.getMock_list().get(position).getPaper_id());
                intent.putExtra("paper_type", "mock");
                intent.putExtra("mock_time", mockPreResp.getMock_time());
                intent.putExtra("redo", false);
                startActivity(intent);
            }
        });
    }
}
