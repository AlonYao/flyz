package com.appublisher.quizbank.common.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.coursecenter.CourseFragment;
import com.appublisher.lib_course.offline.activity.OfflineActivity;
import com.appublisher.lib_course.opencourse.fragment.OpenCourseFragment;
import com.appublisher.lib_course.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.common.interview.adapter.InterviewCommentListAdapter;
import com.appublisher.quizbank.common.interview.fragment.InterviewIndexFragment;
import com.appublisher.quizbank.common.interview.model.InterviewCommentListModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentM;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.fragment.StudyIndexFragment;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.network.QApiConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherCommentListActivity extends BaseActivity implements RequestCallback {

    public View mCommentStatusRl;
    public View mCommentNoteRl;
    public TextView mCommentStatusTv;
    public TextView mCommentNoteTv;
    public ImageView mCommentStatusIv;
    public ImageView mCommentNoteIv;
    public XListView mListView;
    public View mNullView;
    public TextView mNullStatus;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("购买").setTitle("购买"),
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("购买".equals(item.getTitle())) {
            final Intent intent = new Intent(this, InterviewBuyTeacherCommentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
        mNullStatus = (TextView) findViewById(R.id.null_status);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > mList.size()) return;
                final Intent intent = new Intent(TeacherCommentListActivity.this, InterviewPaperDetailActivity.class);
                intent.putExtra("dataFrom", "record_comment");
                intent.putExtra("record_id", mList.get(position - 1).getRecord_id());
                startActivity(intent);
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
        hideLoading();
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
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }
}
