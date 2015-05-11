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
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.model.HomePageModel;
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
import com.umeng.analytics.MobclickAgent;

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
    private View mView;

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
        mView = inflater.inflate(R.layout.fragment_homepage, container, false);
        mTvQuickTest = (TextView) mView.findViewById(R.id.homepage_quicktest);
        mTvEstimate = (TextView) mView.findViewById(R.id.homepage_estimate);
        mTvRanking = (TextView) mView.findViewById(R.id.homepage_ranking);
        mTvTodayExam = (TextView) mView.findViewById(R.id.homepage_todayexam_tv);
        mTvSpecial = (TextView) mView.findViewById(R.id.homepage_special_tv);
        mTvZhiboke = (TextView) mView.findViewById(R.id.homepage_zhiboke);
        mLlMokao = (LinearLayout) mView.findViewById(R.id.homepage_todayexam_ll);
        mLlSpecial = (LinearLayout) mView.findViewById(R.id.homepage_special_ll);
        ImageView ivHistoryMokao = (ImageView) mView.findViewById(R.id.homepage_history);
        ImageView ivSpecial = (ImageView) mView.findViewById(R.id.homepage_special);
        LinearLayout llEvaluation = (LinearLayout) mView.findViewById(R.id.homepage_evaluation);
        TextView tvExam = (TextView) mView.findViewById(R.id.homepage_exam);

        // 倒计时
        HomePageModel.setExamCountDown(tvExam);

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

        // 全部专项
        ivSpecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SpecialProjectActivity.class);
                startActivity(intent);
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取&呈现 数据
        ProgressBarManager.showProgressBar(mView);
        new Request(mActivity, this).getEntryData();

        // Umeng
        MobclickAgent.onPageStart("HomePageFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("HomePageFragment");
    }

    /**
     * 设置内容
     * @param response 首页数据回调
     */
    private void setContent(JSONObject response) {
        Gson gson = new Gson();
        HomePageResp homePageResp = gson.fromJson(response.toString(), HomePageResp.class);
        if (homePageResp == null || homePageResp.getResponse_code() != 1) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        // 估分&排名
        AssessmentM assessment = homePageResp.getAssessment();
        if (assessment != null) {
            mTvEstimate.setText(String.valueOf(assessment.getScore()));
            mTvRanking.setText(String.valueOf((int) assessment.getRank()*100));
        }

        PaperM pager = homePageResp.getPaper();
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
                            Intent intent = new Intent(mActivity, MeasureAnalysisActivity.class);
                            intent.putExtra("analysis_type", "mokao");
                            intent.putExtra("exercise_id", todayExam.getId());
                            startActivity(intent);

                        } else {
                            Intent intent =
                                    new Intent(mActivity, PracticeDescriptionActivity.class);
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

            // 推荐专项训练
            final PaperNoteM note = pager.getNote();
            if (note != null) {
                mTvSpecial.setText(note.getName());

                mLlSpecial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                        intent.putExtra("hierarchy_id", note.getId());
                        intent.putExtra("hierarchy_level", 3);
                        intent.putExtra("paper_type", "note");
                        intent.putExtra("note_type", "all");
                        intent.putExtra("paper_name", note.getName());
                        intent.putExtra("redo", false);
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
        LiveCourseM liveCourse = homePageResp.getLive_course();
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

        // 记录最近的系统通知的id
        Globals.last_notice_id = homePageResp.getLatest_notify();

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
