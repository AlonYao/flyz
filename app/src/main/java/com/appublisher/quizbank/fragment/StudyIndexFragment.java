package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
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
import com.appublisher.quizbank.adapter.CarouselAdapter;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.business.StudyIndexModel;
import com.appublisher.quizbank.model.netdata.CarouselM;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    public View carouselView;
    public ViewPager viewPager;
    public CarouselAdapter carouselAdapter;
    public List<CarouselM> carouselWrittenList;

    private RoundedImageView avatarIv;
    private TextView assessScoreTv;
    private TextView rankTv;
    public TextView examNameTv;
    public TextView mockNameTv;
    public TextView assessNameTv;
    private TextView miniCuntTv;
    private TextView noteNameTv;
    private LinearLayout mLlDots;

    public QRequest mQRequest;

    private PaperTodayM mTodayExam;
    private PaperNoteM mNote;
    public MockGufenResp mockGufenResp;

    public int mock_id = -1;
    private static final int CAROUSEL_SLIDE = 1;

    //设置当前 第几个图片 被选中
    private int autoCurrIndex = 0;
    private Timer timer = new Timer();
    private MsgHandler mHandler;

    public static class MsgHandler extends Handler {
        private WeakReference<Fragment> mFragemt;

        public MsgHandler(Fragment fragment) {
            mFragemt = new WeakReference<>(fragment);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final StudyIndexFragment studyIndexFragment = (StudyIndexFragment) mFragemt.get();
            if (studyIndexFragment != null) {
                switch (msg.what) {
                    case CAROUSEL_SLIDE:
                        if (studyIndexFragment.carouselWrittenList.size() != 0) {
                            studyIndexFragment.viewPager.setCurrentItem(msg.arg1);
                        }
                        break;
                }
            }
        }
    }

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

        carouselView = view.findViewById(R.id.carousel_view_rl);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        avatarIv = (RoundedImageView) view.findViewById(R.id.avatar);
        assessScoreTv = (TextView) view.findViewById(R.id.score);
        rankTv = (TextView) view.findViewById(R.id.rank);
        examNameTv = (TextView) view.findViewById(R.id.exam_name);
        mockNameTv = (TextView) view.findViewById(R.id.mock_name);
        assessNameTv = (TextView) view.findViewById(R.id.assess_name);
        miniCuntTv = (TextView) view.findViewById(R.id.mini_count);
        noteNameTv = (TextView) view.findViewById(R.id.note_name);
        mLlDots = (LinearLayout) view.findViewById(R.id.carousel_dot_ll);

        mQRequest = new QRequest(getActivity(), this);
        mHandler = new MsgHandler(this);

        carouselWrittenList = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(getActivity(), carouselWrittenList);

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

        viewPager.setAdapter(carouselAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                autoCurrIndex = position;

                for (int i = 0; i < mLlDots.getChildCount(); i++) {
                    if (i == position) {
                        mLlDots.getChildAt(i).setSelected(true);
                    } else {
                        mLlDots.getChildAt(i).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void getData() {
        mQRequest.getGlobalSettings();
        mQRequest.getEntryData();
        mQRequest.getMockGufen();
        mQRequest.getCarousel();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getData();
            mHandler.removeMessages(CAROUSEL_SLIDE);
        }
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
            case "get_carousel":
                StudyIndexModel.dealCarouselResp(response, this);
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
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Mine");
                UmengManager.onEvent(getContext(), "Home", map);
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

                // Umeng
                map = new HashMap<>();
                map.put("Action", "Mini");
                UmengManager.onEvent(getContext(), "Home", map);

                break;
            case R.id.history_mini_view:
                // 历史模考
                intent = new Intent(getActivity(), HistoryMokaoActivity.class);
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Minilist");
                UmengManager.onEvent(getContext(), "Home", map);
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
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Note");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
            case R.id.notes_view:
                // 全部专项
                intent = new Intent(getActivity(), SpecialProjectActivity.class);
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Notlist");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
            case R.id.quick_test_view:
                // 快速智能练习
//                intent = new Intent(getActivity(), PracticeDescriptionActivity.class);
//                intent.putExtra("paper_type", "auto");
//                intent.putExtra("paper_name", "快速智能练习");
//                intent.putExtra("umeng_entry", "Home");
//                startActivity(intent);
                intent = new Intent(getActivity(), MeasureActivity.class);
                intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.AUTO);
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Auto");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
            case R.id.wholepage_view:
                intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "wholepage");
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Entirelist");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
            case R.id.mock_view:
                intent = new Intent(getActivity(), MockPreActivity.class);
                intent.putExtra("mock_id", mock_id);
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Mock");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
            case R.id.assess_view:
                intent = new Intent(getActivity(), GuFenListActivity.class);
                intent.putExtra("mock_gufen", GsonManager.modelToString(mockGufenResp));
                startActivity(intent);
                // Umeng
                map = new HashMap<>();
                map.put("Action", "Evaluate");
                UmengManager.onEvent(getContext(), "Home", map);
                break;
        }
    }

    public void initDots(int length) {
        mLlDots.removeAllViews();
        for (int j = 0; j < length; j++) {
            mLlDots.addView(initDot());
        }
        mLlDots.getChildAt(0).setSelected(true);

        startCarousel();
    }

    private View initDot() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.carousel_dot, null);
    }

    private void startCarousel() {
        // 设置自动轮播图片，5s后执行，周期是5s
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = CAROUSEL_SLIDE;
                if (autoCurrIndex == carouselWrittenList.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 5000, 5000);
    }
}
