package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
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
/*
*  此类是新的界面的具体的展示每套题的类:
*  特点:
*       1.两种状态:未付费和已付费:可以创建两个不同的Fragment,在这两个不同的Fragment中处理对应的逻辑
*       2.在这里面存在toolbar,可以考虑将此类对象传入子Fragment.然后设置toolbar的标题 setTiltle
*       3.需要在此类中获取getSupportFragmnetManager来创建两个Fragment来表示未付费和已付费的Fragment逻辑,并根据服务器的交互来根据字段,动态转换显示具体的哪一个Fragment
*       4.比如:与后台交互后,传来数据,为已付费状态,那么展示的这套题应该需要已付费形式的adapter,可以将PaperDetailAdapter变成基类,然后给出两个状态形式的adapter
*                                       5.PaperDeatilAdapter中处理了点击事件,那么popupwindow应该是在点击事件中处理的,因为已付费也有四种录音状态,那么这录音状态的逻辑怎么一次性整理出来呢?通过什么处理
* */
public class InterviewPaperDetailActivity_new extends BaseActivity implements RequestCallback {

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
        String paper_type = getIntent().getStringExtra("paper_type");
        int note_id = getIntent().getIntExtra("note_id", 0);
        viewPager = (ViewPager) findViewById(R.id.viewpager);


        list = new ArrayList<>();
        adaper = new PaperDetailAdaper(this, list);
        viewPager.setAdapter(adaper);

        mRequest = new InterviewRequest(this, this);

        mRequest.getPaperDetail(paper_id, paper_type, note_id);
        showLoading();
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
                adaper.notifyDataSetChanged();

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
