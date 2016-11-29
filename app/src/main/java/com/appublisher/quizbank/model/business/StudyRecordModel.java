package com.appublisher.quizbank.model.business;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.activity.LegacyMeasureActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.adapter.HistoryPapersListAdapter;
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

    private static HistoryPapersListAdapter mHistoryPapersListAdapter;

    /**
     * 处理学习记录回调
     *
     * @param fragment StudyRecordFragment
     * @param response 回调数据
     */
    public static void dealHistoryPapersResp(final StudyRecordFragment fragment,
                                             JSONObject response) {
        if (response == null) return;

        HistoryPapersResp historyPapersResp =
                GsonManager.getModel(response.toString(), HistoryPapersResp.class);

        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        final ArrayList<HistoryPaperM> historyPapers = historyPapersResp.getList();

        if (historyPapers == null || historyPapers.size() == 0) return;

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
                                || position - 2 >= fragment.mHistoryPapers.size())
                            return;

                        HistoryPaperM historyPaper = fragment.mHistoryPapers.get(position - 2);

                        if (historyPaper == null) return;

                        String status = historyPaper.getStatus();

                        if ("done".equals(status)) {
                            // 跳转至练习报告页面
                            if ("auto".equals(historyPaper.getPaper_type())
                                    || "entire".equals(historyPaper.getPaper_type())) {
                                Intent intent = new Intent(
                                        fragment.mActivity, MeasureReportActivity.class);
                                intent.putExtra("from", "study_record");
                                intent.putExtra("paper_id", historyPaper.getPaper_id());
                                intent.putExtra("paper_type", historyPaper.getPaper_type());
                                intent.putExtra("paper_name", historyPaper.getName());
                                intent.putExtra("paper_time", historyPaper.getAction_time());
                                fragment.mActivity.startActivity(intent);
                            } else {
                                Intent intent = new Intent(
                                        fragment.mActivity, PracticeReportActivity.class);
                                intent.putExtra("from", "study_record");
                                intent.putExtra("exercise_id", historyPaper.getPaper_id());
                                intent.putExtra("paper_type", historyPaper.getPaper_type());
                                intent.putExtra("paper_name", historyPaper.getName());
                                intent.putExtra("paper_time", historyPaper.getAction_time());
                                fragment.mActivity.startActivity(intent);
                            }

                            // Umeng
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Action", "List");
                            UmengManager.onEvent(fragment.getContext(), "Record", map);

                        } else if ("undone".equals(status)) {
                            // 跳转至做题页面
                            Intent intent = new Intent(
                                    fragment.mActivity, LegacyMeasureActivity.class);
                            intent.putExtra("from", "study_record");
                            intent.putExtra("exercise_id", historyPaper.getPaper_id());
                            intent.putExtra("paper_type", historyPaper.getPaper_type());
                            intent.putExtra("paper_name", historyPaper.getName());
                            intent.putExtra("redo", true);
                            intent.putExtra("umeng_entry", "Continue");
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
     * 显示空白图片
     *
     * @param fragment StudyRecordFragment
     */
    public static void showNullImg(StudyRecordFragment fragment) {
        if (fragment.mHistoryPapers == null || fragment.mHistoryPapers.size() == 0) {
            fragment.mIvNull.setVisibility(View.VISIBLE);
        } else {
            fragment.mIvNull.setVisibility(View.GONE);
        }
    }
}
