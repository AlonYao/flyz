package com.appublisher.quizbank.common.interview.activity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.PaperDetailAdaper;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private int paper_id;
    private InterviewRequest mRequest;
    private ViewPager viewPager;
    private PaperDetailAdaper adaper;
    private List<InterviewPaperDetailResp.QuestionsBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);

        setToolBar(this);

        paper_id = getIntent().getIntExtra("paper_id", 0);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        list = new ArrayList<>();
        adaper = new PaperDetailAdaper(this, list);
        viewPager.setAdapter(adaper);

        mRequest = new InterviewRequest(this, this);

        mRequest.getPaperDetail(paper_id);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) return;

        if ("paper_detail".equals(apiName)) {
            InterviewPaperDetailResp interviewPaperDetailResp = GsonManager.getModel(response, InterviewPaperDetailResp.class);
            if (interviewPaperDetailResp.getResponse_code() == 1) {
                list.clear();
                list.addAll(interviewPaperDetailResp.getQuestions());
                adaper.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
