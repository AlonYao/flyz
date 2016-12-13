package com.appublisher.quizbank.common.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.adapter.PaperListAdapter;
import com.appublisher.quizbank.common.interview.model.InterviewPaperListModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperListResp;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterviewPaperListActivity extends BaseActivity implements RequestCallback {

    private String mFrom;
    private InterviewRequest mRequest;
    private int page = 1;
    private XListView xListView;
    private List<InterviewPaperListResp.PapersBean> list;
    private PaperListAdapter adapter;
    private int note_id = 0;
    public int area_id = 0;
    public int year = 0;
    public View yearFilterView;
    public View areaFilterView;
    public TextView yearFilterTv;
    public TextView areaFilterTv;
    private InterviewPaperListModel interviewPaperListModel;


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
        interviewPaperListModel = new InterviewPaperListModel();

        setValue();
    }

    public void setValue() {
        if ("history".equals(mFrom)) {
            View filterView = findViewById(R.id.filter);
            filterView.setVisibility(View.VISIBLE);
            mRequest.getInterviewFilter();
            yearFilterView = findViewById(R.id.interview_year_rl);
            areaFilterView = findViewById(R.id.interview_area_rl);
            yearFilterTv = (TextView) findViewById(R.id.interview_year_tv);
            areaFilterTv = (TextView) findViewById(R.id.interview_area_tv);

            yearFilterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interviewPaperListModel.showYearPop(InterviewPaperListActivity.this);
                }
            });

            areaFilterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interviewPaperListModel.showAreaPop(InterviewPaperListActivity.this);
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
                final Intent intent = new Intent(InterviewPaperListActivity.this, InterviewPaperDetailActivity.class);
                intent.putExtra("paper_id", list.get(position - 1).getId());
                // 类型处理
                if ("guokao".equals(mFrom) || "history".equals(mFrom)) {
                    intent.putExtra("paper_type", "entire");
                } else if ("teacher".equals(mFrom)) {
                    intent.putExtra("paper_type", "teacher");
                } else if ("note".equals(mFrom)) {
                    intent.putExtra("paper_type", "note");
                    intent.putExtra("note_id", note_id);
                }
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                if ("guokao".equals(mFrom)) {
                    map.put("PaperID", String.valueOf(list.get(position - 1).getId()));
                    UmengManager.onEvent(InterviewPaperListActivity.this, "Jingxuan", map);
                } else if ("teacher".equals(mFrom)) {
                    map.put("Action", String.valueOf(list.get(position - 1).getId()));
                    UmengManager.onEvent(InterviewPaperListActivity.this, "Jiexi", map);
                } else if ("note".equals(mFrom)) {
                    map.put("Action", String.valueOf(list.get(position - 1).getId()));
                    UmengManager.onEvent(InterviewPaperListActivity.this, "Tupo", map);
                } else if ("history".equals(mFrom)) {
                    map.put("Action", String.valueOf(list.get(position - 1).getId()));
                    UmengManager.onEvent(InterviewPaperListActivity.this, "Zhenti", map);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        if ("teacher".equals(mFrom)) {
            mRequest.getTeacherPaperList(page);
        } else {
            mRequest.getPaperList(area_id, year, note_id, page);
        }

        showLoading();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null || apiName == null) return;
        if ("interview_paper_list".equals(apiName)) {
            xListView.stopRefresh();
            xListView.stopLoadMore();
            InterviewPaperListResp interviewPaperListResp = GsonManager.getModel(response, InterviewPaperListResp.class);
            if (interviewPaperListResp.getResponse_code() == 1) {

                if (page == 1) {
                    list.clear();
                    xListView.setPullLoadEnable(true);
                }

                list.addAll(interviewPaperListResp.getPapers());
                adapter.notifyDataSetChanged();

                if (interviewPaperListResp.getPapers().size() < 15) {
                    xListView.setPullLoadEnable(false);
                    if (page == 1 && interviewPaperListResp.getPapers().size() == 0) {
                        ToastManager.showToast(this, "没有相关试卷");
                    } else {
                        ToastManager.showToast(this, "已加载全部试卷");
                    }
                }
            }
        } else if ("interview_filter".equals(apiName)) {
            InterviewFilterResp interviewFilterResp = GsonManager.getModel(response, InterviewFilterResp.class);
            if (interviewFilterResp.getResponse_code() == 1) {
                interviewPaperListModel.dealFilterResp(response);
            }
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
        xListView.stopRefresh();
        xListView.stopLoadMore();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
        xListView.stopRefresh();
        xListView.stopLoadMore();
    }
}
