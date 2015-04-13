package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.PaperM;
import com.appublisher.quizbank.model.netdata.homepage.PaperNoteM;
import com.appublisher.quizbank.model.netdata.homepage.PaperTodayM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 首页
 */
public class HomePageFragment extends Fragment implements RequestCallback{

    private Activity mActivity;
    private TextView mTvEstimate;
    private TextView mTvRanking;
    private TextView mTvTodayExam;
    private TextView mTvSpecial;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        TextView tvQuickTest = (TextView) view.findViewById(R.id.homepage_quicktest);
        mTvEstimate = (TextView) view.findViewById(R.id.homepage_estimate);
        mTvRanking = (TextView) view.findViewById(R.id.homepage_ranking);
        mTvTodayExam = (TextView) view.findViewById(R.id.homepage_todayexam_tv);
        mTvSpecial = (TextView) view.findViewById(R.id.homepage_special_tv);

        // 获取&呈现 数据
        if (Globals.homepageResp != null) {
            setContent(Globals.homepageResp);
        } else {
            ProgressBarManager.showProgressBar(view);
            new Request(mActivity, this).getEntryData();
        }

        // 快速练习
        tvQuickTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MeasureActivity.class);
                intent.putExtra("flag", "auto_training");
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 设置内容
     * @param homepageResp 首页数据回调
     */
    private void setContent(JSONObject homepageResp) {
        Gson gson = new Gson();
        HomePageResp homepageData = gson.fromJson(homepageResp.toString(), HomePageResp.class);
        if (homepageData == null || homepageData.getResponse_code() != 1) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        // 估分&排名
        AssessmentM assessment = homepageData.getAssessmentM();
        if (assessment != null) {
            mTvEstimate.setText(String.valueOf(assessment.getScore()));
            mTvRanking.setText(String.valueOf(assessment.getRank()));
        }

        PaperM pager = homepageData.getPaper();
        if (pager != null) {
            // 今日模考
            PaperTodayM todayExam = pager.getToday();
            if (todayExam != null) {
                mTvTodayExam.setText("已有" + String.valueOf(todayExam.getPersons_num()) + "人参加");
            }

            // 知识点专项训练
            PaperNoteM note = pager.getNote();
            if (note != null) {
                mTvSpecial.setText(note.getName());
            }
        }

        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        if ("entry_data".equals(apiName)) {
            setContent(response);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ToastManager.showToast(mActivity, getString(R.string.netdata_overtime));
        ProgressBarManager.hideProgressBar();
    }
}
