package com.appublisher.quizbank.common.interview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.PaperListAdapter;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;
import com.appublisher.quizbank.common.interview.netdata.PaperListResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterviewPaperListActivity extends BaseActivity implements RequestCallback {

    private String mFrom;
    private InterviewRequest mRequest;
    private int page = 1;
    private XListView xListView;
    private List<PaperListResp.PapersBean> list;
    private PaperListAdapter adapter;
    private int area_id = 0;
    private int note_id = 0;
    private int year = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);

        setToolBar(this);

        mFrom = getIntent().getStringExtra("from");
        if ("guokao".equals(mFrom)) {
            year = getIntent().getIntExtra("year", 0);
            setTitle(year + "年");
        } else if ("teacher".equals(mFrom)) {
            setTitle("名师解析");
        } else if ("note".equals(mFrom)) {
            note_id = getIntent().getIntExtra("note_id", 0);
            setTitle(getIntent().getStringExtra("note"));
        } else if ("history".equals(mFrom)) {
            setTitle("历年真题");
        }

        xListView = (XListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new PaperListAdapter(this, list);

        mRequest = new InterviewRequest(this, this);
        if ("teacher".equals(mFrom)) {
            mRequest.getTeacherPaperList(page);
        } else {
            mRequest.getPaperList(area_id, year, note_id, page);
        }

        showLoading();

        setValue();
    }

    public void setValue() {
        if ("history".equals(mFrom)) {
            View filterView = findViewById(R.id.filter);
            filterView.setVisibility(View.VISIBLE);
            mRequest.getInterviewFilter();
            View yearFilterView = findViewById(R.id.interview_year_rl);
            View areaFilterView = findViewById(R.id.interview_area_rl);
            yearFilterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            areaFilterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        xListView.setPullLoadEnable(true);
        xListView.setAdapter(adapter);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (page > 1)
                    page--;
                if ("teacher".equals(mFrom)) {
                    mRequest.getTeacherPaperList(page);
                    return;
                }
                mRequest.getPaperList(area_id, year, note_id, page);
            }

            @Override
            public void onLoadMore() {
                page++;
                if ("teacher".equals(mFrom)) {
                    mRequest.getTeacherPaperList(page);
                    return;
                }
                mRequest.getPaperList(area_id, year, note_id, page);
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;
        if ("interview_paper_list".equals(apiName)) {
            xListView.stopRefresh();
            xListView.stopLoadMore();
            PaperListResp paperListResp = GsonManager.getModel(response, PaperListResp.class);
            if (paperListResp.getResponse_code() == 1) {
                if (page == 1) {
                    list.clear();
                    xListView.setPullLoadEnable(true);
                }
                list.addAll(paperListResp.getPapers());
                adapter.notifyDataSetChanged();
                if (paperListResp.getPapers().size() == 0) {
                    xListView.setPullLoadEnable(false);
                    if (page == 1) {
                        ToastManager.showToast(this, "没有相关试卷");
                    } else {
                        ToastManager.showToast(this, "已加载全部试卷");
                    }
                }
            }
        } else if ("interview_filter".equals(apiName)) {
            InterviewFilterResp interviewFilterResp = GsonManager.getModel(response, InterviewFilterResp.class);
            if (interviewFilterResp.getResponse_code() == 1) {

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
