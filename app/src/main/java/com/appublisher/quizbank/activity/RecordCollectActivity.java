package com.appublisher.quizbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.InterviewCollectAdapter;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewCollectResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class RecordCollectActivity extends BaseActivity implements RequestCallback {

    private ListView mListview;
    private InterviewRequest mRequest;
    private List<InterviewCollectResp.InterviewM> mList;
    private InterviewCollectAdapter mAdapter;
    private Context context;
    private ImageView mNullView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_collect);
        setToolBar(this);
        setTitle("面试收藏");
        context = getApplicationContext();

        initData();
        initView();
        initListener();
    }

    private void initData() {
        // 获取数据
        mRequest = new InterviewRequest(this, this);
        mRequest.getRecordInterviewCollectDetail();
    }

    private void initView() {
        mListview = (ListView) findViewById(R.id.record_collect_lv);
        mNullView = (ImageView) findViewById(R.id.quizbank_null);
    }
    private void initListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mList == null || mList.size() == 0) return;
                InterviewCollectResp.InterviewM interviewM = mList.get(position);
                if(interviewM == null){
                    return;
                }else{
                    int note_id = interviewM.getNote_id();
                    Intent intent = new Intent(context, InterviewPaperDetailActivity.class);
                    intent.putExtra("dataFrom","recordCollect");
                    intent.putExtra("note_id",note_id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;

        if ("get_note_list".equals(apiName)) {
            InterviewCollectResp interviewCollectResp = GsonManager.getModel(response, InterviewCollectResp.class); // 将数据封装成bean对象
            if (interviewCollectResp != null && interviewCollectResp.getResponse_code() == 1) {

                // 获取问题的数据集合
                mList = interviewCollectResp.getQuestions();

                if (mList == null || mList.size() == 0) {
                    mNullView.setVisibility(View.VISIBLE);
                    mListview.setVisibility(View.GONE);
                } else {
                    mNullView.setVisibility(View.GONE);
                    mListview.setVisibility(View.VISIBLE);
                    mAdapter = new InterviewCollectAdapter(this, mList);
                    mListview.setAdapter(mAdapter);
                }
            } else if (interviewCollectResp != null && interviewCollectResp.getResponse_code() == 1001) {
                mNullView.setVisibility(View.VISIBLE);
                mListview.setVisibility(View.GONE);
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
