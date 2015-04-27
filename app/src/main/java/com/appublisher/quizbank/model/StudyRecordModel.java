package com.appublisher.quizbank.model;

import android.view.View;
import android.widget.AdapterView;

import com.appublisher.quizbank.adapter.HistoryPapersListAdapter;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.model.netdata.history.HistoryPapersResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * StudyRecordFragment Model
 */
public class StudyRecordModel {

    public static void dealHistoryPapersResp(StudyRecordFragment studyRecordFragment,
                                             JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        HistoryPapersResp historyPapersResp =
                gson.fromJson(response.toString(), HistoryPapersResp.class);

        if (historyPapersResp == null || historyPapersResp.getResponse_code() != 1) return;

        ArrayList<HistoryPaperM> historyPapers = historyPapersResp.getList();

        if (historyPapers == null || historyPapers.size() == 0) return;

        // 拼接数据
        if (studyRecordFragment.mOffset == 0) {
            studyRecordFragment.mHistoryPapers = historyPapers;
        } else {
            studyRecordFragment.mHistoryPapers.addAll(historyPapers);
        }

        HistoryPapersListAdapter historyPapersListAdapter =
                new HistoryPapersListAdapter(
                        studyRecordFragment.mActivity, studyRecordFragment.mHistoryPapers);

        studyRecordFragment.mXListView.setAdapter(historyPapersListAdapter);

        studyRecordFragment.mXListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
