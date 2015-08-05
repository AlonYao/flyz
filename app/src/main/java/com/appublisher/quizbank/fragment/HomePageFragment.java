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
import com.appublisher.quizbank.activity.MockActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.model.business.HomePageModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.globalsettings.MockM;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.PaperM;
import com.appublisher.quizbank.model.netdata.homepage.PaperNoteM;
import com.appublisher.quizbank.model.netdata.homepage.PaperTodayM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
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
    private TextView mTvMockTitle;
    private TextView mTvMockName;
    private TextView mTvQuickTest;
    private LinearLayout mLlMiniMokao;
    private LinearLayout mLlMock;
    private LinearLayout mLlSpecial;
    private PaperTodayM mTodayExam;
    private PaperNoteM mNote;
    private int mOnResumeCount;

    public View mView;
    public Request mRequest;
    public LinearLayout mPromote;
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
        mLlMiniMokao = (LinearLayout) mView.findViewById(R.id.homepage_todayexam_ll);
        mLlSpecial = (LinearLayout) mView.findViewById(R.id.homepage_special_ll);
        mPromote = (LinearLayout) mView.findViewById(R.id.course_promote);
        mLlMock = (LinearLayout) mView.findViewById(R.id.homepage_mock);
        mTvMockTitle = (TextView) mView.findViewById(R.id.homepage_mock_title);
        mTvMockName = (TextView) mView.findViewById(R.id.homepage_mock_name);
        ImageView ivHistoryMokao = (ImageView) mView.findViewById(R.id.homepage_history);
        ImageView ivSpecial = (ImageView) mView.findViewById(R.id.homepage_special);
        LinearLayout llEvaluation = (LinearLayout) mView.findViewById(R.id.homepage_evaluation);
        TextView tvExam = (TextView) mView.findViewById(R.id.homepage_exam);
        RoundedImageView ivAvatar = (RoundedImageView) mView.findViewById(R.id.homepage_avatar);

        // 成员变量初始化
        mRequest = new Request(mActivity, this);
        mOnResumeCount = 0;

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

        // 如果本地没有全局配置，获取全局配置，保证能获取到模考估分的状态
        if (GlobalSettingDAO.findById() == null) {
            mRequest.getGlobalSettings();
        } else {
            setMockBtn();
        }

        // 获取课程快讯
        if (Globals.promoteLiveCourseResp == null) {
            mRequest.getPromoteLiveCourse();
        } else {
            HomePageModel.setPromoteLiveCourse(mActivity, mView);
        }

        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // 获取&呈现 数据
            getData();

            // 显示评分Alert
            dealGradeAlert();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            // 获取&呈现 数据
            getData();

            // 显示评分Alert
            dealGradeAlert();
        }

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
     * 获取数据
     */
    private void getData() {
        ProgressBarManager.showProgressBar(mView);
        mRequest.getEntryData();
        mRequest.getFreeOpenCourseStatus();
    }

    /**
     * 处理评价Alert的逻辑
     */
    private void dealGradeAlert() {
        if (GradeDAO.isGrade(Globals.appVersion) == 1) return;

        long gradeTimestamp = GradeDAO.getGradeTimestamp(Globals.appVersion);
        if (gradeTimestamp == 0) {
            mOnResumeCount++;
            if (mOnResumeCount >= 2 && GradeDAO.isShowGradeAlert(Globals.appVersion)) {
                AlertManager.showGradeAlert(mActivity, "Alert");
            }
        } else {
            long curTimestamp = System.currentTimeMillis();
            long dex = (curTimestamp - gradeTimestamp) / 1000;
            if (dex >= 5) {
                // 视为评价完成，开通课程
                HomePageModel.openupCourse(this);
            } else {
                // 视为未完成评价
                AlertManager.showGradeFailAlert(mActivity);
            }
        }
    }

    /**
     * 设置内容
     * @param response 首页数据回调
     */
    private void setContent(JSONObject response) {
        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        HomePageResp homePageResp = Globals.gson.fromJson(response.toString(), HomePageResp.class);
        if (homePageResp == null || homePageResp.getResponse_code() != 1) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        // 估分&排名
        AssessmentM assessment = homePageResp.getAssessment();
        if (assessment != null) {
            mTvEstimate.setText(String.valueOf(assessment.getScore()));
            mTvRanking.setText(Utils.rateToPercent(assessment.getRank()));
        }

        PaperM pager = homePageResp.getPaper();
        if (pager != null) {
            // 今日模考
            mTodayExam = pager.getToday();
            if (mTodayExam != null) {
                if (mTodayExam.getDefeat() == 0) {
                    mTvTodayExam.setText(
                            "已有" + String.valueOf(mTodayExam.getPersons_num()) + "人参加");
                } else {
                    mTvTodayExam.setText(
                            "已有" + String.valueOf(mTodayExam.getPersons_num()) + "人参加，击败"
                            + Utils.rateToPercent(mTodayExam.getDefeat()) + "%");
                }
                mLlMiniMokao.setOnClickListener(this);
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

    /**
     * 设置模考按钮
     */
    private void setMockBtn() {
        GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();

        if (globalSettingsResp == null || globalSettingsResp.getResponse_code() != 1) {
            mLlMock.setVisibility(View.GONE);
            return;
        }

        MockM mock = globalSettingsResp.getMock();

        if (mock == null || mock.getType() == null) {
            mLlMock.setVisibility(View.GONE);
            return;
        }

        mLlMock.setVisibility(View.VISIBLE);
        mLlMock.setOnClickListener(this);

        if ("mock".equals(mock.getType())) {
            mTvMockTitle.setText("模考总动员");
        } else if ("evaluate".equals(mock.getType())) {
            mTvMockTitle.setText("估分进行时");
        } else {
            mTvMockTitle.setText("模考估分");
        }

        mTvMockName.setText(mock.getName());
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        if ("entry_data".equals(apiName)) {
            setContent(response);
        } else if ("free_open_course_status".equals(apiName)) {
            OpenCourseModel.dealOpenCourseStatusResp(response);
            OpenCourseModel.setOpenCourseBtn(mActivity, mTvZhiboke);
        } else if ("promote_live_course".equals(apiName)) {
            HomePageModel.dealPromoteResp(response, this);
        } else if ("global_settings".equals(apiName)) {
            GlobalSettingDAO.save(response.toString());
            setMockBtn();
        } else if ("get_rate_course".equals(apiName)) {
            ProgressDialogManager.closeProgressDialog();
            HomePageModel.dealOpenupCourseResp(response, this);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (!isAdded()) return;
        ToastManager.showToast(mActivity, getString(R.string.netdata_overtime));
        ProgressBarManager.hideProgressBar();
        ProgressDialogManager.closeProgressDialog();
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
                    intent.putExtra("paper_name", mTodayExam.getName());
                    intent.putExtra("from", "mokao_homepage");
                    startActivity(intent);

                } else {
                    intent = new Intent(mActivity, PracticeDescriptionActivity.class);
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", "今日模考");
                    intent.putExtra("umeng_entry", "Home");

                    if ("fresh".equals(status)) {
                        intent.putExtra("paper_id", mTodayExam.getId());
                        intent.putExtra("redo", false);
                    } else {
                        intent.putExtra("exercise_id", mTodayExam.getId());
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

            case R.id.homepage_mock:
                // 模考&估分
                intent = new Intent(mActivity, MockActivity.class);
                intent.putExtra("title", mTvMockTitle.getText().toString());
                startActivity(intent);
                break;
        }
    }
}
