package com.appublisher.quizbank.model;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.adapter.HistoryPapersListAdapter;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.model.netdata.history.HistoryPapersResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * StudyRecordFragment Model
 */
public class StudyRecordModel {

    /**
     * 处理学习记录回调
     * @param fragment StudyRecordFragment
     * @param response 回调数据
     */
    public static void dealHistoryPapersResp(final StudyRecordFragment fragment,
                                             JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        HistoryPapersResp historyPapersResp =
                gson.fromJson(response.toString(), HistoryPapersResp.class);

        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        final ArrayList<HistoryPaperM> historyPapers = historyPapersResp.getList();

        if (historyPapers == null || historyPapers.size() == 0) return;

        // 拼接数据
        if (fragment.mOffset == 0) {
            fragment.mHistoryPapers = historyPapers;
        } else {
            fragment.mHistoryPapers.addAll(historyPapers);
        }

        HistoryPapersListAdapter historyPapersListAdapter =
                new HistoryPapersListAdapter(
                        fragment.mActivity, fragment.mHistoryPapers);

        fragment.mXListView.setAdapter(historyPapersListAdapter);

        fragment.mXListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fragment.mHistoryPapers == null
                        || position - 1 >= fragment.mHistoryPapers.size())
                    return;

                HistoryPaperM historyPaper = fragment.mHistoryPapers.get(position - 1);

                if (historyPaper == null) return;

                String status = historyPaper.getStatus();

                if ("done".equals(status)) {
                    ToastManager.showToast(fragment.mActivity, "施工中……");
                } else if ("undone".equals(status)) {
                    Intent intent = new Intent(fragment.mActivity, MeasureActivity.class);
                    intent.putExtra("exercise_id", historyPaper.getPaper_id());
                    intent.putExtra("paper_type",
                            convertPaperType(historyPaper.getPaper_type()));
                    intent.putExtra("redo", true);
                    fragment.mActivity.startActivity(intent);
                }
            }
        });
    }

    /**
     * 试卷类型转换
     * @param paperType 试卷类型
     * @return 转换后的数据类型
     */
    private static String convertPaperType(String paperType) {
        if ("整卷练习".equals(paperType)) {
            return "entire";
        } else if ("天天模考".equals(paperType)) {
            return "mokao";
        } else if ("专项练习".equals(paperType)) {
            return "note";
        } else if ("快速练习".equals(paperType)) {
            return "auto";
        } else if ("错题练习".equals(paperType)) {
            return "error";
        } else if ("收藏练习".equals(paperType)) {
            return "collect";
        } else if ("估分".equals(paperType)) {
            return "evaluate";
        }

        return "";
    }
}
