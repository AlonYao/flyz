package com.appublisher.quizbank.model.business;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.HistoryPapersListAdapter;
import com.appublisher.quizbank.adapter.InterviewHistoryPapersListAdapter;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureMockReportActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.model.netdata.history.HistoryPapersResp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * StudyRecordFragment Model
 */
public class StudyRecordModel {

    public HistoryPapersListAdapter mHistoryPapersListAdapter;
    private final Context mContext;
    private final StudyRecordFragment mFragment;
    public InterviewHistoryPapersListAdapter mInterviewHistoryPapersListAdapter;

    public StudyRecordModel(Context context, StudyRecordFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    /**
     * 处理学习记录回调
     *
     * @param fragment StudyRecordFragment
     * @param response 回调数据
     */
    public void dealHistoryPapersResp(final StudyRecordFragment fragment,
                                      JSONObject response, final String from) {

        final String mFrom = from;
        if (response == null) {
            if (fragment.mIsRefresh) {
                fragment.showIvNull();
            }
            return;
        }

        HistoryPapersResp historyPapersResp =
                GsonManager.getModel(response.toString(), HistoryPapersResp.class);
        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        final ArrayList<HistoryPaperM> historyPapers = historyPapersResp.getList();
        if (historyPapers == null || historyPapers.size() == 0) {
            if (fragment.mIsRefresh) {
                fragment.showIvNull();
            }
            return;
        }

        fragment.showXListview();
        // 拼接数据
        if (fragment.mOffset == 0) {
            fragment.mHistoryPapers = historyPapers;
            mHistoryPapersListAdapter = new HistoryPapersListAdapter(
                    fragment.mActivity, fragment.mHistoryPapers);
            fragment.mXListView.setAdapter(mHistoryPapersListAdapter);
        } else {
            fragment.mHistoryPapers.addAll(historyPapers);
            mHistoryPapersListAdapter.notifyDataSetChanged();
        }

        fragment.mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if (fragment.mHistoryPapers == null
                        || position - 1 >= fragment.mHistoryPapers.size()
                        || !"write".equals(mFrom))
                    return;

                HistoryPaperM historyPaper = fragment.mHistoryPapers.get(position - 1);

                if (historyPaper == null) return;
                String status = historyPaper.getStatus();

                if ("done".equals(status)) {
                    // 跳转至练习报告页面
                    Intent intent;
                    if (MeasureConstants.MOCK.equals(historyPaper.getPaper_type())) {
                        // 模考报告页面
                        intent = new Intent(
                                fragment.mActivity, MeasureMockReportActivity.class);
                    } else {
                        intent = new Intent(
                                fragment.mActivity, MeasureReportActivity.class);
                        intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE,
                                historyPaper.getPaper_type());
                    }
                    intent.putExtra(MeasureConstants.INTENT_PAPER_ID,
                            historyPaper.getPaper_id());
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "List");
                    UmengManager.onEvent(fragment.getContext(), "Record", map);

                } else if ("undone".equals(status)) {
                    // 跳转至做题页面
                    Intent intent = new Intent(
                            fragment.mActivity, MeasureActivity.class);
                    intent.putExtra(
                            MeasureConstants.INTENT_PAPER_ID,
                            historyPaper.getPaper_id());
                    intent.putExtra(
                            MeasureConstants.INTENT_PAPER_TYPE,
                            historyPaper.getPaper_type());
                    intent.putExtra(MeasureConstants.INTENT_REDO, true);
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Redo", historyPaper.getPaper_type());
                    UmengManager.onEvent(fragment.getContext(), "Record", map);
                }
            }
        });
    }

    /**
     * 处理面试页面的数据和点击事件
     */
    public void dealInterviewHistoryPapersResp(final StudyRecordFragment fragment,
                                               JSONObject response, String from) {
        final String mFrom = from;
        if (response == null) {
            if (fragment.mIsRefresh) {
                fragment.showIvNull();
            }
            fragment.setmPage();    // 加载数据失败,将累加的页数减去
            return;
        }

        HistoryPapersResp historyPapersResp =
                GsonManager.getModel(response.toString(), HistoryPapersResp.class);  // 将数据封装到了一个bean中

        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) {
            fragment.showIvNull();
            fragment.setmPage();
            return;
        }

        final ArrayList<HistoryPaperM> mhistoryPapers = historyPapersResp.getList();
        if (mhistoryPapers == null || mhistoryPapers.size() == 0) {
            if (fragment.mIsRefresh) {
                fragment.showIvNull();
            }
            fragment.setmPage();
            return;
        }

        fragment.showXListview();

        // 拼接数据
        if (fragment.mPage == 1) {
            fragment.mInterviewHistoryPapers = mhistoryPapers;
            mInterviewHistoryPapersListAdapter = new InterviewHistoryPapersListAdapter(
                    fragment.mActivity, fragment.mInterviewHistoryPapers);

            fragment.mXListView.setAdapter(mInterviewHistoryPapersListAdapter);
        } else {
            fragment.mInterviewHistoryPapers.addAll(mhistoryPapers);
            mInterviewHistoryPapersListAdapter.notifyDataSetChanged();
        }

        /**
         *    条目的点击事件
         */
        fragment.mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if (fragment.mInterviewHistoryPapers == null
                        || position - 1 >= fragment.mInterviewHistoryPapers.size()
                        || !"interview".equals(mFrom))
                    return;

                HistoryPaperM mInterviewhistoryPaper =
                        fragment.mInterviewHistoryPapers.get(position - 1);

                if (mInterviewhistoryPaper == null) return;
                String itemType = mInterviewhistoryPaper.getType();
                String time = mInterviewhistoryPaper.getTime();
                Intent intent = new Intent(fragment.mActivity, InterviewPaperDetailActivity.class); // 直接进入数据展示界面
                intent.putExtra("dataFrom", "studyRecordInterview");
                intent.putExtra("itemType", itemType);
                intent.putExtra("time", time);
                fragment.mActivity.startActivity(intent);
            }
        });
    }

    /**
     * 显示空白图片
     *
     * @param fragment StudyRecordFragment
     */
    public static void showNullImg(StudyRecordFragment fragment) {

        fragment.mIvNull.setVisibility(View.VISIBLE);
       fragment.mXListView.setVisibility(View.GONE);
    }

    /*
    *   得到颜色值
    * */
    public int getThemeColor() {
        TypedValue value = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }
}
