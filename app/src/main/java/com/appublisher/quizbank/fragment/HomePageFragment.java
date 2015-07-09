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
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.model.business.HomePageModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.PaperM;
import com.appublisher.quizbank.model.netdata.homepage.PaperNoteM;
import com.appublisher.quizbank.model.netdata.homepage.PaperTodayM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 首页
 */
public class HomePageFragment extends Fragment implements RequestCallback, View.OnClickListener{

    private TextView mTvEstimate;
    private TextView mTvRanking;
    private TextView mTvTodayExam;
    private TextView mTvSpecial;
    private LinearLayout mLlMokao;
    private LinearLayout mLlSpecial;
    private TextView mTvQuickTest;
    private View mView;
    private PaperTodayM mTodayExam;
    private PaperNoteM mNote;
    private Request mRequest;

    public TextView mTvZhiboke;
    public Activity mActivity;

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
        mTvZhiboke = (TextView) mView.findViewById(R.id.opencourse_btn);
        mLlMokao = (LinearLayout) mView.findViewById(R.id.homepage_todayexam_ll);
        mLlSpecial = (LinearLayout) mView.findViewById(R.id.homepage_special_ll);
        ImageView ivHistoryMokao = (ImageView) mView.findViewById(R.id.homepage_history);
        ImageView ivSpecial = (ImageView) mView.findViewById(R.id.homepage_special);
        LinearLayout llEvaluation = (LinearLayout) mView.findViewById(R.id.homepage_evaluation);
        TextView tvExam = (TextView) mView.findViewById(R.id.homepage_exam);
        RoundedImageView ivAvatar = (RoundedImageView) mView.findViewById(R.id.homepage_avatar);

        // 成员变量初始化
        mRequest = new Request(mActivity, this);

        // 设置头像
        LoginModel.setAvatar(mActivity, ivAvatar);

        // 考试项目倒计时
        HomePageModel.setExamCountDown(tvExam);

        // 历史模考
        ivHistoryMokao.setOnClickListener(this);

        // 能力评估
        llEvaluation.setOnClickListener(this);

        // 全部专项
        ivSpecial.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取&呈现 数据
        ProgressBarManager.showProgressBar(mView);
        mRequest.getEntryData();
        mRequest.getFreeOpenCourseStatus();

        // Umeng
        MobclickAgent.onPageStart("HomePageFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "HomePageFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("HomePageFragment");

        // TalkingData
        TCAgent.onPageEnd(mActivity, "HomePageFragment");
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
            mTvRanking.setText(Utils.rateToString(assessment.getRank()));
        }

        PaperM pager = homePageResp.getPaper();
        if (pager != null) {
            // 今日模考
            mTodayExam = pager.getToday();
            if (mTodayExam != null) {
                mTvTodayExam.setText("已有" + String.valueOf(
                        mTodayExam.getPersons_num()) + "人参加");
                mLlMokao.setOnClickListener(this);
            }

            // 推荐专项训练
            mNote = pager.getNote();
            if (mNote != null) {
                mTvSpecial.setText("推荐："  + mNote.getName());
                mLlSpecial.setOnClickListener(this);
            }
        }

        // 快速练习
        mTvQuickTest.setOnClickListener(this);

        // 记录最近的系统通知的id
        Globals.last_notice_id = homePageResp.getLatest_notify();

        // 显示红点
        HomePageModel.setDrawerRedPoint();

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

        if ("free_open_course_status".equals(apiName)) {
            OpenCourseModel.dealOpenCourseStatusResp(response);
            OpenCourseModel.setOpenCourseBtn(mActivity, mTvZhiboke);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_history:
                // 历史模考
                Intent intent = new Intent(mActivity, HistoryMokaoActivity.class);
                startActivity(intent);
                break;

            case R.id.homepage_evaluation:
                // 能力评估
                intent = new Intent(mActivity, EvaluationActivity.class);
                startActivity(intent);
                break;

            case R.id.homepage_special:
                // 全部专项
                intent = new Intent(mActivity, SpecialProjectActivity.class);
                startActivity(intent);
                break;

            case R.id.homepage_todayexam_ll:
                // 今日模考
                if (mTodayExam == null || mTodayExam.getId() == 0) {
                    ToastManager.showToast(mActivity, "今日暂时没有模考……");
                    break;
                }

                String status = mTodayExam.getStatus();

                if ("done".equals(status)) {
                    // 跳转至练习报告页面
                    intent = new Intent(mActivity, PracticeReportActivity.class);
                    intent.putExtra("exercise_id", mTodayExam.getId());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("from", "mokao_homepage");
                    startActivity(intent);

                } else {
                    intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                    intent.putExtra("paper_id", mTodayExam.getId());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", "今日模考");
                    intent.putExtra("umeng_entry", "Home");

                    if ("fresh".equals(status)) {
                        intent.putExtra("redo", false);
                    } else {
                        intent.putExtra("redo", true);
                    }

                    startActivity(intent);
                }

                break;

            case R.id.homepage_special_ll:
                // 推荐专项
                if (mNote == null || mNote.getId() == 0) break;
                intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                intent.putExtra("hierarchy_id", mNote.getId());
                intent.putExtra("hierarchy_level", 3);
                intent.putExtra("paper_type", "note");
                intent.putExtra("note_type", "all");
                intent.putExtra("paper_name", mNote.getName());
                intent.putExtra("redo", false);
                intent.putExtra("umeng_entry", "Home");
                startActivity(intent);
                break;

            case R.id.homepage_quicktest:
                // 快速智能练习
                intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                intent.putExtra("paper_type", "auto");
                intent.putExtra("paper_name", "快速智能练习");
                intent.putExtra("umeng_entry", "Home");
                startActivity(intent);
                break;
        }
    }
}
