package com.appublisher.quizbank.common.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.GuoKaoYearAdapter;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterviewGuoKaoActivity extends BaseActivity implements RequestCallback {

    private InterviewRequest mRequest;
    private ListView listView;
    private GuoKaoYearAdapter adapter;
    private List<Integer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guo_kao);

        setToolBar(this);

        list = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new GuoKaoYearAdapter(this, list);


        mRequest = new InterviewRequest(this, this);
        mRequest.getInterviewFilter();
        showLoading();

        setValue();
    }

    public void setValue() {
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list == null || list.size() <= 0) return;
                final Intent intent = new Intent(InterviewGuoKaoActivity.this, InterviewPaperListActivity.class);
                intent.putExtra("from", "guokao");
                intent.putExtra("year", list.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;
        if ("interview_filter".equals(apiName)) {
            InterviewFilterResp interviewFilterResp = GsonManager.getModel(response, InterviewFilterResp.class);
            if (interviewFilterResp.getResponse_code() == 1) {
                list.clear();
                list.addAll(interviewFilterResp.getYear());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }
}
