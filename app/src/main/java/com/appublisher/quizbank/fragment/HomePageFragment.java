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
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.HistoryMokaoActivity;
import com.appublisher.quizbank.activity.MockActivity;
import com.appublisher.quizbank.activity.MockPreActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.common.measure.MeasureActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.model.business.HomePageModel;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.globalsettings.MockM;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.PaperM;
import com.appublisher.quizbank.model.netdata.homepage.PaperNoteM;
import com.appublisher.quizbank.model.netdata.homepage.PaperTodayM;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 首页
 */
public class HomePageFragment extends Fragment implements RequestCallback, View.OnClickListener {

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
    public TextView mTvExam;
    public View mView;
    public QRequest mQRequest;
    public LinearLayout mPromote;
    public TextView mTvZhiboke;
    public Activity mActivity;
    private String type = "evaluate";
    public int mock_id = 0;
    private String mock_name;

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
        mTvExam = (TextView) mView.findViewById(R.id.homepage_exam);
        ImageView ivHistoryMokao = (ImageView) mView.findViewById(R.id.homepage_history);
        ImageView ivSpecial = (ImageView) mView.findViewById(R.id.homepage_special);
        LinearLayout llEvaluation = (LinearLayout) mView.findViewById(R.id.homepage_evaluation);
        RoundedImageView ivAvatar = (RoundedImageView) mView.findViewById(R.id.homepage_avatar);

        // 成员变量初始化
        mQRequest = new QRequest(mActivity, this);
        mOnResumeCount = 0;

        // 设置头像
        LoginModel.setAvatar(mActivity, ivAvatar);

        // 历史模考
        ivHistoryMokao.setOnClickListener(this);

        // 能力评估
        llEvaluation.setOnClickListener(this);

        // 全部专项
        ivSpecial.setOnClickListener(this);

        // 如果本地没有全局配置，获取全局配置，保证能获取到模考估分的状态
        if (GlobalSettingDAO.findById() == null) {
            mQRequest.getGlobalSettings();
        } else {
            setMockBtn();
        }

        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            refreshData();
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
//        ProgressBarManager.showProgressBar(mView);

        // 获取首页信息
        mQRequest.getEntryData();

        // 获取公开课信息
        mQRequest.getFreeOpenCourseStatus();

        // 获取快讯
        mQRequest.getPromoteLiveCourse();
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
            if (dex >= 10) {
                // 视为评价完成，开通课程
                HomePageModel.openupCourse(this);
            } else {
                // 视为未完成评价
                GradeDAO.saveGradeTimestamp(Globals.appVersion, 0);
                AlertManager.showGradeFailAlert(mActivity);
            }
        }
    }

    /**
     * 更新数据
     */
    private void refreshData() {
        // 获取&呈现 数据
        getData();

        // 显示评分Alert
        dealGradeAlert();

        // 考试项目倒计时
        HomePageModel.setExamCountDown(mTvExam, mQRequest);
    }

    /**
     * 设置内容
     *
     * @param response 首页数据回调
     */
    private void setContent(JSONObject response) {

        HomePageResp homePageResp = GsonManager.getModel(response.toString(), HomePageResp.class);
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
                    String text = "已有" + String.valueOf(mTodayExam.getPersons_num()) + "人参加";
                    mTvTodayExam.setText(text);
                } else {
                    String text = "已有"
                            + String.valueOf(mTodayExam.getPersons_num())
                            + "人参加，击败"
                            + Utils.rateToPercent(mTodayExam.getDefeat())
                            + "%";
                    mTvTodayExam.setText(text);
                }
                mLlMiniMokao.setOnClickListener(this);
            }

            // 推荐专项训练
            mNote = pager.getNote();
            if (mNote != null) {
                String text = "推荐：" + mNote.getName();
                mTvSpecial.setText(text);
                mLlSpecial.setOnClickListener(this);
            }
        }

        // 快速练习
        mTvQuickTest.setOnClickListener(this);

        // 记录最近的系统通知的id
        Globals.last_notice_id = homePageResp.getLatest_notify();

        // 显示红点
        HomePageModel.setDrawerRedPoint();

        // 更新用户考试项目
        HomePageModel.updateExam(homePageResp.getExam_info(), this);

        ProgressBarManager.hideProgressBar();
    }

    /**
     * 设置模考按钮
     */
    private void setMockBtn() {
        GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
        mLlMock.setOnClickListener(this);//添加
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
            type = "mock";
            mock_name = mock.getName();
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
        if (response == null || apiName == null) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        switch (apiName) {
            case "entry_data":
                setContent(response);
                break;

            case "free_open_course_status":
                HomePageModel.dealOpenCourseStatusResp(response);
                HomePageModel.setOpenCourseBtn(mActivity, mTvZhiboke);
                break;

            case "promote_live_course":
                HomePageModel.dealPromoteResp(response, this);
                break;

            case "global_settings":
                GlobalSettingDAO.save(response.toString());
                setMockBtn();
                break;

            case "get_rate_course":
                ProgressDialogManager.closeProgressDialog();
                HomePageModel.dealOpenupCourseResp(response, this);
                break;

            case "exam_list":
                // 更新考试时间
                ExamDetailModel exam = GsonManager.getModel(
                        response.toString(), ExamDetailModel.class);
                HomePageModel.updateExam(exam, mTvExam);
                break;
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (!isAdded() || apiName == null) return;
        ToastManager.showToast(mActivity, getString(R.string.netdata_overtime));
        ProgressBarManager.hideProgressBar();
        ProgressDialogManager.closeProgressDialog();

        if ("promote_live_course".equals(apiName)) {
            LinearLayout llPromote = (LinearLayout) mView.findViewById(R.id.course_promote);
            llPromote.setVisibility(View.GONE);
        }
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
                    intent.putExtra("paper_name", mTodayExam.getName());
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
//                intent = new Intent(mActivity, PracticeDescriptionActivity.class);
//                intent.putExtra("paper_type", "auto");
//                intent.putExtra("paper_name", "快速智能练习");
//                intent.putExtra("umeng_entry", "Home");
//                startActivity(intent);
                intent = new Intent(mActivity, MeasureActivity.class);
                intent.putExtra(MeasureActivity.PAPER_TYPE, "auto");
                startActivity(intent);
                break;

            case R.id.homepage_mock:
                // 模考&估分
                Class<?> cls;
                if ("mock".equals(type)) {
                    cls = MockPreActivity.class;
                } else {
                    cls = MockActivity.class;
                }
                intent = new Intent(mActivity, cls);
                intent.putExtra("title", mTvMockTitle.getText().toString());
                intent.putExtra("type", type);
                intent.putExtra("paper_name", mock_name);
                startActivity(intent);
                break;
        }
    }
}
