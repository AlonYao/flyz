package com.appublisher.quizbank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.activity.UserInfoActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.GuFenListActivity;
import com.appublisher.quizbank.activity.HistoryMokaoActivity;
import com.appublisher.quizbank.activity.MockPreActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.activity.SpecialProjectActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.business.StudyIndexModel;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.homepage.AssessmentM;
import com.appublisher.quizbank.model.netdata.homepage.HomePageResp;
import com.appublisher.quizbank.model.netdata.homepage.PaperM;
import com.appublisher.quizbank.model.netdata.homepage.PaperNoteM;
import com.appublisher.quizbank.model.netdata.homepage.PaperTodayM;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jinbao on 2016/10/31.
 */

public class StudyIndexFragment extends Fragment implements RequestCallback, View.OnClickListener {

    private View userInfoView;
    public View mockView;
    public View assessView;
    private View miniView;
    private View historyMiniView;
    private View noteView;
    private View notesView;
    private View quickTestView;
    private View wholepageView;

    private RoundedImageView avatarIv;
    private TextView assessScoreTv;
    private TextView rankTv;
    public TextView examNameTv;
    public TextView mockNameTv;
    public TextView assessNameTv;
    private TextView miniCuntTv;
    private TextView noteNameTv;

    public QRequest mQRequest;

    private PaperTodayM mTodayExam;
    private PaperNoteM mNote;
    public MockGufenResp mockGufenResp;

    public int mock_id = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_index, container, false);

        userInfoView = view.findViewById(R.id.user_info_view);
        mockView = view.findViewById(R.id.mock_view);
        assessView = view.findViewById(R.id.assess_view);
        miniView = view.findViewById(R.id.mini_view);
        historyMiniView = view.findViewById(R.id.history_mini_view);
        noteView = view.findViewById(R.id.note_view);
        notesView = view.findViewById(R.id.notes_view);
        quickTestView = view.findViewById(R.id.quick_test_view);
        wholepageView = view.findViewById(R.id.wholepage_view);

        avatarIv = (RoundedImageView) view.findViewById(R.id.avatar);
        assessScoreTv = (TextView) view.findViewById(R.id.score);
        rankTv = (TextView) view.findViewById(R.id.rank);
        examNameTv = (TextView) view.findViewById(R.id.exam_name);
        mockNameTv = (TextView) view.findViewById(R.id.mock_name);
        assessNameTv = (TextView) view.findViewById(R.id.assess_name);
        miniCuntTv = (TextView) view.findViewById(R.id.mini_count);
        noteNameTv = (TextView) view.findViewById(R.id.note_name);

        mQRequest = new QRequest(getActivity(), this);

        setValue();

        return view;
    }

    public void setValue() {
        avatarIv.setOnClickListener(this);
        userInfoView.setOnClickListener(this);
        miniView.setOnClickListener(this);
        historyMiniView.setOnClickListener(this);
        notesView.setOnClickListener(this);
        noteView.setOnClickListener(this);
        quickTestView.setOnClickListener(this);
        wholepageView.setOnClickListener(this);
        mockView.setOnClickListener(this);
        assessView.setOnClickListener(this);
    }

    public void getData() {
        mQRequest.getGlobalSettings();
        mQRequest.getEntryData();
        mQRequest.getMockGufen();
    }

    @Override
    public void onResume() {
        super.onResume();
        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
        if (userInfoModel != null) {
            LoginModel.setAvatar(getActivity(), avatarIv);
        }

        getData();
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
            assessScoreTv.setText(String.valueOf(assessment.getScore()));
            rankTv.setText(Utils.rateToPercent(assessment.getRank()) + "%");
        }

        PaperM pager = homePageResp.getPaper();
        if (pager != null) {
            // 今日模考
            mTodayExam = pager.getToday();
            if (mTodayExam != null) {
                if (mTodayExam.getDefeat() == 0) {
                    String text = "已有" + String.valueOf(mTodayExam.getPersons_num()) + "人参加";
                    miniCuntTv.setText(text);
                } else {
                    String text = "已有"
                            + String.valueOf(mTodayExam.getPersons_num())
                            + "人参加，击败"
                            + Utils.rateToPercent(mTodayExam.getDefeat())
                            + "%";
                    miniCuntTv.setText(text);
                }
            }

            // 推荐专项训练
            mNote = pager.getNote();
            if (mNote != null) {
                String text = "推荐：" + mNote.getName();
                noteNameTv.setText(text);
            }
        }

        // 记录最近的系统通知的id
        Globals.last_notice_id = homePageResp.getLatest_notify();

        // 更新用户考试项目
        StudyIndexModel.updateExam(homePageResp.getExam_info(), this);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressBarManager.hideProgressBar();
            return;
        }

        switch (apiName) {
            case "global_settings":
                GlobalSettingDAO.save(response.toString());
                break;
            case "entry_data":
                setContent(response);
                break;
            case "exam_list":
                // 更新考试时间
                ExamDetailModel exam = GsonManager.getModel(
                        response.toString(), ExamDetailModel.class);
                StudyIndexModel.updateExam(exam, examNameTv);
                break;
            case "mock_gufen":
                StudyIndexModel.dealMockGufenResp(response, this);
                break;
            default:
                break;

        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

    @Override
    public void onClick(View v) {
        final Intent intent;
        switch (v.getId()) {
            case R.id.avatar:
                intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.user_info_view:
                // 能力评估
                intent = new Intent(getActivity(), EvaluationActivity.class);
                startActivity(intent);
                break;
            case R.id.mini_view:
                // 今日模考
                if (mTodayExam == null || mTodayExam.getId() == 0) {
                    ToastManager.showToast(getActivity(), "今日暂时没有模考……");
                    break;
                }

                String status = mTodayExam.getStatus();

                if ("done".equals(status)) {
                    // 跳转至练习报告页面
                    intent = new Intent(getActivity(), PracticeReportActivity.class);
                    intent.putExtra("exercise_id", mTodayExam.getId());
                    intent.putExtra("paper_type", "mokao");
                    intent.putExtra("paper_name", mTodayExam.getName());
                    intent.putExtra("from", "mokao_homepage");
                    startActivity(intent);

                } else {
                    intent = new Intent(getActivity(), PracticeDescriptionActivity.class);
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
            case R.id.history_mini_view:
                // 历史模考
                intent = new Intent(getActivity(), HistoryMokaoActivity.class);
                startActivity(intent);
                break;
            case R.id.note_view:
                // 推荐专项
                if (mNote == null || mNote.getId() == 0) break;
                intent = new Intent(getActivity(), PracticeDescriptionActivity.class);
                intent.putExtra("hierarchy_id", mNote.getId());
                intent.putExtra("hierarchy_level", 3);
                intent.putExtra("paper_type", "note");
                intent.putExtra("note_type", "all");
                intent.putExtra("paper_name", mNote.getName());
                intent.putExtra("redo", false);
                intent.putExtra("umeng_entry", "Home");
                startActivity(intent);
                break;
            case R.id.notes_view:
                // 全部专项
                intent = new Intent(getActivity(), SpecialProjectActivity.class);
                startActivity(intent);
                break;
            case R.id.quick_test_view:
                // 快速智能练习
                intent = new Intent(getActivity(), PracticeDescriptionActivity.class);
                intent.putExtra("paper_type", "auto");
                intent.putExtra("paper_name", "快速智能练习");
                intent.putExtra("umeng_entry", "Home");
                startActivity(intent);
                break;
            case R.id.wholepage_view:
                intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "wholepage");
                startActivity(intent);
                break;
            case R.id.mock_view:
                intent = new Intent(getActivity(), MockPreActivity.class);
                intent.putExtra("mock_id", mock_id);
                startActivity(intent);
                break;
            case R.id.assess_view:
                intent = new Intent(getActivity(), GuFenListActivity.class);
                intent.putExtra("mock_gufen", GsonManager.modelToString(mockGufenResp));
                startActivity(intent);
                break;
        }
    }
}
