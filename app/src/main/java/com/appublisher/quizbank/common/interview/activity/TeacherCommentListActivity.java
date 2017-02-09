package com.appublisher.quizbank.common.interview.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.InterviewCommentListAdapter;
import com.appublisher.quizbank.common.interview.model.InterviewCommentListModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentM;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherCommentListActivity extends BaseActivity implements RequestCallback {

    public View mCommentStatusRl;
    public View mCommentNoteRl;
    public TextView mCommentStatusTv;
    public TextView mCommentNoteTv;
    public ImageView mCommentStatusIv;
    public ImageView mCommentNoteIv;
    private XListView mListView;
    public View mNullView;
    public ImageView mCommentIntroduction;

    private InterviewRequest mRequest;
    private InterviewCommentListModel mCommentListModel;
    public List<InterviewCommentM> mList;
    public InterviewCommentListAdapter mAdapter;

    public int status_id = -1;
    public int note_id = -1;
    public int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_comment_list);

        setToolBar(this);
        mRequest = new InterviewRequest(this, this);
        mCommentListModel = new InterviewCommentListModel();

        mList = new ArrayList<>();
        mAdapter = new InterviewCommentListAdapter(this, mList);

        initViews();
        setValue();
        getData();
    }

    public void initViews() {
        mCommentStatusRl = findViewById(R.id.comment_status_rl);
        mCommentNoteRl = findViewById(R.id.comment_note_rl);
        mCommentStatusTv = (TextView) findViewById(R.id.comment_status_tv);
        mCommentStatusIv = (ImageView) findViewById(R.id.comment_status_arrow);
        mCommentNoteTv = (TextView) findViewById(R.id.comment_note_tv);
        mCommentNoteIv = (ImageView) findViewById(R.id.comment_note_arrow);
        mListView = (XListView) findViewById(R.id.listView);
        mNullView = findViewById(R.id.null_view);
        mCommentIntroduction = (ImageView) findViewById(R.id.comment_introduction);
        mListView.setPullLoadEnable(true);
        mListView.setAdapter(mAdapter);
    }

    public void setValue() {
        mCommentStatusRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentListModel.showCommentStatusPop(TeacherCommentListActivity.this);
            }
        });

        mCommentNoteRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentListModel.showCommentNotePop(TeacherCommentListActivity.this);
            }
        });

        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                page = 1;
                refreshData();
            }

            @Override
            public void onLoadMore() {
                page++;
                refreshData();
            }
        });

        mCommentIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getData() {
        mRequest.getCommentFilter();
        refreshData();
    }

    public void refreshData() {
        showLoading();
        mRequest.getCommentList(status_id, note_id, page);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) return;
        if ("comment_filter".equals(apiName)) {
            mCommentListModel.dealCommentFilterResp(response);
        } else if ("comment_list".equals(apiName)) {
            mListView.stopLoadMore();
            mListView.stopRefresh();
            mCommentListModel.dealCommentListResp(response, this);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
