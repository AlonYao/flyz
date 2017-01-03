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
import com.appublisher.quizbank.common.interview.activity.InterviewCategoryActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewGuoKaoActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperListActivity;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.fragment.StudyRecordFragment_new;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.model.netdata.history.HistoryPapersResp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * StudyRecordFragment Model
 */
public class StudyRecordModel_new {

    private HistoryPapersListAdapter mHistoryPapersListAdapter;
    private final Context mContext;
    private final StudyRecordFragment_new mFragment;

    public StudyRecordModel_new(Context context, StudyRecordFragment_new fragment) {
        mContext = context;
        mFragment = fragment;
    }

    /**
     * 处理学习记录回调
     *
     * @param fragment StudyRecordFragment
     * @param response 回调数据
     */
    public void dealHistoryPapersResp(final StudyRecordFragment_new fragment,
                                             JSONObject response) {
        if (response == null) {
            if (fragment.mIsRefresh) {
                showNullImg(fragment);
            }
            return;
        }

        HistoryPapersResp historyPapersResp =
                GsonManager.getModel(response.toString(), HistoryPapersResp.class);
        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        final ArrayList<HistoryPaperM> historyPapers = historyPapersResp.getList();
        if (historyPapers == null || historyPapers.size() == 0) {
            if (fragment.mIsRefresh) {
                showNullImg(fragment);
            }
            return;
        }
        /**
         *   在此处通过传进的常量判断进入哪一个adapter
         * **/
        String type = "write";
        // 拼接数据
        if (fragment.mOffset == 0) {
            fragment.mHistoryPapers = historyPapers;
            mHistoryPapersListAdapter = new HistoryPapersListAdapter(
                    fragment.mActivity, fragment.mHistoryPapers, type);
            fragment.mXListView.setAdapter(mHistoryPapersListAdapter);
        } else {
            fragment.mHistoryPapers.addAll(historyPapers);
            mHistoryPapersListAdapter.notifyDataSetChanged();
        }

        fragment.mIvNull.setVisibility(View.GONE);
        fragment.mXListView.setVisibility(View.VISIBLE);

        fragment.mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {
                        if (fragment.mHistoryPapers == null
                                || position - 2 >= fragment.mHistoryPapers.size())
                            return;

                        HistoryPaperM historyPaper = fragment.mHistoryPapers.get(position - 2);

                        if (historyPaper == null) return;

                        String status = historyPaper.getStatus();

                        if ("done".equals(status)) {
                            // 跳转至练习报告页面
                            Intent intent = new Intent(
                                    fragment.mActivity, MeasureReportActivity.class);
                            intent.putExtra(MeasureConstants.INTENT_PAPER_ID,
                                    historyPaper.getPaper_id());
                            intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE,
                                    historyPaper.getPaper_type());
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
     *  再创建一个方法:专门用来处理
     * */
    public void dealInterviewHistoryPapersResp(final StudyRecordFragment_new fragment,
                                               JSONObject response) {
        if (response == null) {
            if (fragment.mIsRefresh) {
                showNullImg(fragment);
            }
            return;
        }

        HistoryPapersResp historyPapersResp =
                GsonManager.getModel(response.toString(), HistoryPapersResp.class);  // 将数据封装到了一个bean中
//        InterviewHistoryPapersResp mInterviewHistoryPapersResp =
//                GsonManager.getModel(response.toString(), InterviewHistoryPapersResp.class);

        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        final ArrayList<HistoryPaperM> mhistoryPapers = historyPapersResp.getList();
        if (mhistoryPapers == null || mhistoryPapers.size() == 0) {
            if (fragment.mIsRefresh) {
                showNullImg(fragment);
            }
            return;
        }

        String type = "interview";
        // 拼接数据
        if (fragment.mOffset == 0) {

            fragment.mHistoryPapers = mhistoryPapers;
            mHistoryPapersListAdapter = new HistoryPapersListAdapter(           // 将数据集合封装给adapter
                    fragment.mActivity, fragment.mHistoryPapers, type);

            fragment.mXListView.setAdapter(mHistoryPapersListAdapter);
        } else {

            fragment.mHistoryPapers.addAll(mhistoryPapers);
            mHistoryPapersListAdapter.notifyDataSetChanged();
        }

        fragment.mIvNull.setVisibility(View.GONE);
        fragment.mXListView.setVisibility(View.VISIBLE);


        /**
         *    条目的点击事件
         */
        fragment.mXListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if (fragment.mHistoryPapers == null
                        || position - 2 >= fragment.mHistoryPapers.size())
                    return;

                HistoryPaperM mInterviewhistoryPaper = fragment.mHistoryPapers.get(position - 2);

                if (mInterviewhistoryPaper == null) return;

                String type = mInterviewhistoryPaper.getType();

                if ("guokao".equals(type)) {
                    // 跳转至国考精选界面
                    final Intent intent = new Intent(fragment.mActivity, InterviewGuoKaoActivity.class);
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "List");
                    UmengManager.onEvent(fragment.getContext(), "Record", map);

                } else if ("teacher".equals(type)) {
                    // 跳转至名师解析界面
                    final Intent intent = new Intent(fragment.mActivity, InterviewPaperListActivity.class);
                    intent.putExtra("from", "teacher");
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Redo", mInterviewhistoryPaper.getPaper_type());
                    UmengManager.onEvent(fragment.getContext(), "Record", map);

                }else if ("category".equals(type)) {
                    // 跳转至名师解析界面
                    final Intent intent = new Intent(fragment.mActivity, InterviewCategoryActivity.class);
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Redo", mInterviewhistoryPaper.getPaper_type());
                    UmengManager.onEvent(fragment.getContext(), "Record", map);

                }else if ("history".equals(type)) {
                    // 跳转至名师解析界面
                    final Intent intent = new Intent(fragment.mActivity, InterviewPaperListActivity.class);
                    intent.putExtra("from", "history");
                    fragment.mActivity.startActivity(intent);

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Redo", mInterviewhistoryPaper.getPaper_type());
                    UmengManager.onEvent(fragment.getContext(), "Record", map);
                }
            }
        });
    }
    /**
     * 显示空白图片
     *
     * @param fragment StudyRecordFragment
     */
    public static void showNullImg(StudyRecordFragment_new fragment) {
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
