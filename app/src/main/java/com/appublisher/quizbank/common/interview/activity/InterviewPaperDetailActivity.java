package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewDetailAdapter;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.viewgroup.MyViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterviewPaperDetailActivity extends BaseActivity implements RequestCallback {

    private int paper_id;
    private InterviewRequest mRequest;
    public MyViewPager viewPager;
    private InterviewDetailAdapter mAdaper;
    private List<InterviewPaperDetailResp.QuestionsBean> list;
    public boolean isCanBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_paper_detail);

        setToolBar(this);

        paper_id = getIntent().getIntExtra("paper_id", 0);
        String paper_type = getIntent().getStringExtra("paper_type");
        int note_id = getIntent().getIntExtra("note_id", 0);
        viewPager = (MyViewPager) findViewById(R.id.viewpager);   //自定义的viewpager



        list = new ArrayList<>();
        mAdaper = new InterviewDetailAdapter(getSupportFragmentManager(), list, this);
        viewPager.setAdapter(mAdaper);
        viewPager.setScroll(true);

        mRequest = new InterviewRequest(this, this);

        mRequest.getPaperDetail(paper_id, paper_type, note_id);
        showLoading();
    }
    public boolean setCanBack(boolean isCanBack){
        this.isCanBack = isCanBack;
        return isCanBack;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回键
            if( isCanBack ==false){
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isCanBack ==false){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;

        if ("paper_detail".equals(apiName)) {
            InterviewPaperDetailResp interviewPaperDetailResp = GsonManager.getModel(response, InterviewPaperDetailResp.class);
            if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1) {
                list.clear();
                list.addAll(interviewPaperDetailResp.getQuestions());
                mAdaper.notifyDataSetChanged();
                if (list.size() == 0) {
                    ToastManager.showToast(this, "没有面试题目");
                }
            } else if (interviewPaperDetailResp != null && interviewPaperDetailResp.getResponse_code() == 1001) {
                ToastManager.showToast(this, "没有面试题目");
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
