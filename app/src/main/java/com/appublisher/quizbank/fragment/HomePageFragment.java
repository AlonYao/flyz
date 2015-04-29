package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.HistoryMokaoActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.LiveCourseM;
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
    private TextView mTvZhiboke;
    private LinearLayout mLlMokao;
    private LinearLayout mLlSpecial;
    private TextView mTvQuickTest;

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
        ImageView ivHistoryMokao = (ImageView) view.findViewById(R.id.homepage_history);
        LinearLayout llEvaluation = (LinearLayout) view.findViewById(R.id.homepage_evaluation);
        mTvQuickTest = (TextView) view.findViewById(R.id.homepage_quicktest);
        mTvEstimate = (TextView) view.findViewById(R.id.homepage_estimate);
        mTvRanking = (TextView) view.findViewById(R.id.homepage_ranking);
        mTvTodayExam = (TextView) view.findViewById(R.id.homepage_todayexam_tv);
        mTvSpecial = (TextView) view.findViewById(R.id.homepage_special_tv);
        mTvZhiboke = (TextView) view.findViewById(R.id.homepage_zhiboke);
        mLlMokao = (LinearLayout) view.findViewById(R.id.homepage_todayexam_ll);
        mLlSpecial = (LinearLayout) view.findViewById(R.id.homepage_special_ll);

        // 获取&呈现 数据
        if (Globals.homepageResp != null) {
            setContent(Globals.homepageResp);
        } else {
            ProgressBarManager.showProgressBar(view);
            new Request(mActivity, this).getEntryData();
        }

        // 历史模考
        ivHistoryMokao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HistoryMokaoActivity.class);
                startActivity(intent);
            }
        });

        // 能力评估
        llEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EvaluationActivity.class);
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
        AssessmentM assessment = homepageData.getAssessment();
        if (assessment != null) {
            mTvEstimate.setText(String.valueOf(assessment.getScore()));
            mTvRanking.setText(String.valueOf(assessment.getRank()));
        }

        PaperM pager = homepageData.getPaper();
        if (pager != null) {
            // 今日模考
            final PaperTodayM todayExam = pager.getToday();
            if (todayExam != null) {
                mTvTodayExam.setText("已有" + String.valueOf(todayExam.getPersons_num()) + "人参加");

                mLlMokao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = todayExam.getStatus();

                        if ("done".equals(status)) {
                            ToastManager.showToast(mActivity, "模考解析 施工中……");
                        } else {
                            Intent intent = new Intent(mActivity, MeasureActivity.class);
                            intent.putExtra("paper_id", todayExam.getId());
                            intent.putExtra("paper_type", "mokao");
                            intent.putExtra("paper_name", "今日模考");

                            if ("fresh".equals(status)) {
                                intent.putExtra("redo", false);
                            } else {
                                intent.putExtra("redo", true);
                            }

                            startActivity(intent);
                        }
                    }
                });
            }

            // 知识点专项训练
            PaperNoteM note = pager.getNote();
            if (note != null) {
                mTvSpecial.setText(note.getName());

                mLlSpecial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, SpecialProjectActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

        // 快速练习
        mTvQuickTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                intent.putExtra("paper_type", "auto");
                intent.putExtra("paper_name", "快速智能练习");
                startActivity(intent);
            }
        });

        // 直播课
        LiveCourseM liveCourse = homepageData.getLive_course();
        if (liveCourse != null && liveCourse.getId() != 0) {
            mTvZhiboke.setBackgroundResource(R.drawable.homepage_item_bg);
            mTvZhiboke.setTextColor(getResources().getColor(R.color.homepage_todayexam));

            if (liveCourse.isStarted()) {
                // 正在上课
                mTvZhiboke.setText("正在直播：" + liveCourse.getName());
            } else {
                // 即将上课
                mTvZhiboke.setText("即将开始：" + liveCourse.getName());
            }

            mTvZhiboke.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastManager.showToast(mActivity, "直播课 施工中……");
                }
            });
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
