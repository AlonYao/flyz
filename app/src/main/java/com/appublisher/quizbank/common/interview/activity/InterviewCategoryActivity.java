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
import com.appublisher.quizbank.common.interview.adapter.CategoryAdapter;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterviewCategoryActivity extends BaseActivity implements RequestCallback {

    private InterviewRequest mRequest;
    private ListView listView;
    private CategoryAdapter adapter;
    private List<InterviewFilterResp.NotesBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_category);

        setToolBar(this);

        list = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new CategoryAdapter(this, list);


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
                final Intent intent = new Intent(InterviewCategoryActivity.this, InterviewPaperListActivity.class);
                intent.putExtra("from", "note");
                intent.putExtra("note_id", list.get(position).getNote_id());
                intent.putExtra("note", list.get(position).getNote());
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
                list.addAll(interviewFilterResp.getNotes());
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
