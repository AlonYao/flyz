package com.appublisher.quizbank.common.interview.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.customui.RoundProgressBarWidthNumber;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewCommentProductActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewCommentGuideActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.model.InterviewModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;
import com.appublisher.quizbank.common.interview.service.MediaPlayingService;
import com.appublisher.quizbank.common.interview.view.IIterviewDetailBaseFragmentView;
import com.appublisher.quizbank.common.interview.view.InterviewDetailBaseFragmentCallBak;
import com.appublisher.quizbank.common.utils.MediaRecordManagerUtil;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by huaxiao on 2016/12/16.
 * // 在基类fragment中获取录音界面的四个布局,然后创建各自的model对象,在各自的model中处理点击事件
 */
public abstract class InterviewDetailBaseFragment extends Fragment implements IIterviewDetailBaseFragmentView, InterviewDetailBaseFragmentCallBak {

    public static final String SUBMIT = "submit";              //可提交
    public static final String HADSUBMIT = "hadSubmit";      // 已提交
    public static final String TEACHERREMARK = "teacherRemark";      // 名师点评
    private static final int NOTAPPLYFORREMARK = 3;   // 没有申请名师点评
    private static final int COMMENT = 2;          // 等待点评中
    private static final int HADREMARKED = 4;     // 已经点评
    private static final int UNLISTEN = 0;            // 没有收听
    public static final String QUESTIONITEM = "questionItem";
    public static final String ANALYSISITEM = "analysisItem";
    public static final String NONE = "none";
    private static final int UPPERLIMITRECORDTIME = 360;
    private static final int LOWERLIMITRECORDTIME = 15;
    private static final int SHOWREMINDERRECORDTIME = 330;
    public View mUnRecordView;
    public View mRecordingView;
    public InterviewPaperDetailActivity mActivity;
    public View mUnsubmitView;
    public View mRecordedView;
    public LinearLayout mUnrecordsound_ll;
    public RelativeLayout mRecordsounding_cancle;
    public RelativeLayout mRecordsounding_confirm;
    public LinearLayout mRecordsoundingll;
    public TextView mTvtimeRecording;
    public ImageView mIvRecordSound;
    public LinearLayout mRecordNotSubmit_rl;
    public RelativeLayout mRecordNotSubmit_ll_play;
    public LinearLayout mRecordNotSubmit_rl_submit;
    public TextView mTvtimeNotSubm;
    public TextView mTvtimeNotSubmPlay;
    public RelativeLayout mAnswer_listen_ll;
    public TextView mTvtimeHadSumbPlay;
    public String mStatus;
    public View mFragmentView;
    private boolean isCanSubmit;
    private String mRecordFolder;
    public String mUserAnswerFilePath;
    public InterviewPaperDetailResp.QuestionsBean mQuestionBean;
    private InterviewDetailModel mModel;
    private String mQuestionType;
    private TextView mAnalysisTv;
    private TextView mNoteTv;
    private TextView mSourceTv;
    private TextView mKeywordsTv;
    public LinearLayout mQuestionContent;
    public RelativeLayout mMerterialView;
    public RelativeLayout mAnalysisSwitchView;
    public LinearLayout mAnalysisView;
    public RelativeLayout mQuestionSwitchView;
    public ImageView mAnalysisIm;
    public TextView mReminderTv;
    private String mTemporaryFilePath;
    private RelativeLayout mTeacherRemarkRl;
    private TextView mRemarkNumb;
    private ImageView mQuestionHelpIv;
    private TextView mPurchasedLinkTv;
    private LinearLayout mNotRemarkLl;
    public TextView mTeacherRemarkPlayTimeTv;
    public TextView mTeacherRemarkPlayState;
    public TextView mUserAnswerPlayState;
    private LinearLayout mExistRemarkLl;
    private TextView mWaitRemarkingTv;
    public int mRemarkState;
    private String mTeacherRemarkRecordFolder;
    public boolean isCanTouch;
    public int mOffset;
    public int mQuestionAudioOffset;
    public int mAnalysisAudioOffset;
    public int mTeacherRemarkAudioOffset;
    public boolean isStop;           //是否停止播放
    public boolean isUserHadSubmitAnswerPause;
    public boolean isQuestionAudioPause;
    public boolean isAnalysisAudioPause;
    public boolean isTeacherAnswerPause;
    private String mTeacherRemarkRemainderNum;
    public RoundProgressBarWidthNumber mUserAnswerProgressBar;
    public RoundProgressBarWidthNumber mUserNotSubmitAnswerProgressBar;
    public RoundProgressBarWidthNumber mTeacherRemarkProgressBar;
    public RoundProgressBarWidthNumber mQuestionAudioProgressbar;
    public RoundProgressBarWidthNumber mAnalysisAudioProgressbar;
    public ImageView mQuestionAudioIv;
    public ImageView mAnalysisAudioIv;
    public TextView mQuestionAudioTv;
    public TextView mAnalysisAudioTv;
    public String mQuestionFileFolder;
    public String mAnalysisFileFolder;
    public String isPlaying;
    public String isUnPurchasedOrPurchasedView;
    private PhoneBroadcastReceiver mPhoneBroadcastReceiver;
    private AudioStreamFocusReceiver mAudioStreamFocusReceiver;
    private ImageView mTeacherRemarkOpenIv;
    private ImageView mTeacherRemarkColseIv;
    private static final int PAY_SUCCESS = 200;
    private String mTeacherRemarkAudioTimeStamp;
    private File[] mTeacherRemarkAudioFiles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (InterviewPaperDetailActivity) getActivity();
        mModel = new InterviewDetailModel(mActivity, this);

        // 动态注册广播
        IntentFilter phoneCallingFilter = new IntentFilter();
        phoneCallingFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        phoneCallingFilter.addAction("android.intent.action.PHONE_STATE");
        mPhoneBroadcastReceiver = new PhoneBroadcastReceiver();
        mActivity.registerReceiver(mPhoneBroadcastReceiver, phoneCallingFilter);

        //动态注册广播接收器
        IntentFilter audioFocusFilter = new IntentFilter();
        audioFocusFilter.addAction("com.appublisher.quizbank.common.interview.fragment.AUDIOSTREAMFOCUSRECEIVER");
        mAudioStreamFocusReceiver = new AudioStreamFocusReceiver();
        mActivity.registerReceiver(mAudioStreamFocusReceiver, audioFocusFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isStop = false;
        isUserHadSubmitAnswerPause = false;
        isCanTouch = true;
        isTeacherAnswerPause = false;
        isQuestionAudioPause = false;
        isAnalysisAudioPause = false;
        isPlaying = NONE;               // 正在播放的view
        mActivity.setCanBack(0);            // 默认设置返回键可以点击
        mFragmentView = inflater.inflate(setLayoutResouceId(), container, false); // 生成布局
        mQuestionBean = initChildData();
        if(mQuestionBean.getQuestion_type() != null){
            mQuestionType = mQuestionBean.getQuestion_type();
            mActivity.setQuestionType(mQuestionType);
        }else{
            mQuestionType = initChildQuestionType();
        }
        mTeacherRemarkAudioTimeStamp = mModel.changTimeStampToText(mQuestionBean.getReviewed_at());     // 转换名师点评老师提交录音的时间戳

        mRemarkState = mQuestionBean.getComment_status();
        mOffset = 0;
        mQuestionAudioOffset = 0;
        mAnalysisAudioOffset = 0;
        mTeacherRemarkAudioOffset = 0;
        mActivity.setPlayingViewState(NONE); // 初始化时没有播放器播放
        isUnPurchasedOrPurchasedView = getIsUnPurchasedOrPurchasedView();       // 获取是哪一个页面
        initRecordView();             // 初始化录音页面控件
        initChildView();
        initRecordView();             // 初始化录音页面控件
        checkIsAnswer();
        setIsCanTouch();
        initRecordFile();             // 初始化录音文件
        initChildListener();
        initRecordListener();
        showQuestion();
        showAnswer();
        return mFragmentView;
    }

    private void checkIsAnswer() {
        if (mQuestionBean.getUser_audio() != null && mQuestionBean.getUser_audio().length() > 0) {
            getTeacherRemarkRemainder();          // 获取名师点评剩余的次数
            changeRecordView(5);
            checkTeacherRemarkState();      // 检查点评的状态
            if (mQuestionBean.getUser_audio_duration() >= UPPERLIMITRECORDTIME) {
                mTvtimeHadSumbPlay.setText(mModel.formatDateTime(UPPERLIMITRECORDTIME));
            } else {
                mTvtimeHadSumbPlay.setText(mModel.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
            }
            mStatus = HADSUBMIT;
        } else {
            mUnRecordView.setVisibility(View.VISIBLE);
            mAnalysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }

    // 获取名师点评剩余的次数
    public void getTeacherRemarkRemainder() {
        mModel.mRequest.getTeacherRemarkRemainder(2);           // 通过model来获取
    }

    // 回调接口,获取名师点评的剩余的次数
    @Override
    public void refreshTeacherRemarkRemainder(String num) {
        mTeacherRemarkRemainderNum = num;
        // 修改点评次数
        changeTeacherRemarkNum();
    }

    /*
    *   检查已经答题后:名师点评的状态
    * */
    private void checkTeacherRemarkState() {
        if (!mQuestionBean.isPurchased_review()) {      // 没有申请名师点评
            mRemarkState = NOTAPPLYFORREMARK;
            mNotRemarkLl.setVisibility(View.VISIBLE);
            mWaitRemarkingTv.setVisibility(View.GONE);
            mExistRemarkLl.setVisibility(View.GONE);
        } else {                      // 申请了名师点评,需要判断:点评中 or 已点评
            if (mQuestionBean == null
                    || mQuestionBean.getComment_status() < 0) return;
            if (mQuestionBean.getComment_status() == COMMENT ) {
                // 点评中
                mNotRemarkLl.setVisibility(View.GONE);
                mWaitRemarkingTv.setVisibility(View.VISIBLE);
                mExistRemarkLl.setVisibility(View.GONE);
            } else {
                // 已经点评
                if (mQuestionBean.getTeacher_name() == null
                        || mQuestionBean.getTeacher_audio() == null
                        || mQuestionBean.getTeacher_audio_duration() <= 0) return;
                mRemarkState = HADREMARKED;
                mNotRemarkLl.setVisibility(View.GONE);
                mWaitRemarkingTv.setVisibility(View.GONE);
                mExistRemarkLl.setVisibility(View.VISIBLE);
                mTeacherRemarkPlayTimeTv.setText(mModel.formatDateTime(mQuestionBean.getTeacher_audio_duration()));
            }
        }
    }

    /*
    *   检查是否第一次提交题
    * */
    @Override
    public void checkIsFirstSubmit() {
        SharedPreferences sp = InterviewModel.getInterviewSharedPreferences(mActivity);
        boolean isFirstSubmit = sp.getBoolean("isFirstSubmitAudio", true);
        if (isFirstSubmit) {
            // 弹出引导浮层
            popupGuideFloating();
            SharedPreferences shp = InterviewModel.getInterviewSharedPreferences(mActivity);
            SharedPreferences.Editor edit = shp.edit();
            edit.putBoolean("isFirstSubmitAudio", false);
            edit.apply();
        }
    }

    private void initRecordView() {
        mMerterialView = (RelativeLayout) mFragmentView.findViewById(R.id.meterial_rl);
        mQuestionSwitchView = (RelativeLayout) mFragmentView.findViewById(R.id.analysis_quesition_rl);
        mQuestionContent = (LinearLayout) mFragmentView.findViewById(R.id.question_content);
        mAnalysisSwitchView = (RelativeLayout) mFragmentView.findViewById(R.id.analysis_switch_rl);
        mAnalysisView = (LinearLayout) mFragmentView.findViewById(R.id.analysis_ll);

        mAnalysisIm = (ImageView) mFragmentView.findViewById(R.id.analysis_im);
        mReminderTv = (TextView) mFragmentView.findViewById(R.id.open_analysis);

        mAnalysisTv = (TextView) mFragmentView.findViewById(R.id.analysis_tv);
        mNoteTv = (TextView) mFragmentView.findViewById(R.id.note_tv);
        mSourceTv = (TextView) mFragmentView.findViewById(R.id.source_tv);
        mKeywordsTv = (TextView) mFragmentView.findViewById(R.id.keywords_tv);

        // 抽取录音部分的控件
        mUnRecordView = mFragmentView.findViewById(R.id.interview_popup_unrecordsound);
        mRecordingView = mFragmentView.findViewById(R.id.interview_popup_recordsounding);
        mUnsubmitView = mFragmentView.findViewById(R.id.interview_popup_recordsounding_unsubmit);
        mRecordedView = mFragmentView.findViewById(R.id.interview_popup_recordsounded);

        // 初始化各自的控件
        mUnrecordsound_ll = (LinearLayout) mFragmentView.findViewById(R.id.interview_unrecordsound_ll);
        mTeacherRemarkOpenIv = (ImageView) mFragmentView.findViewById(R.id.teacher_remark_open_iv);
        mTeacherRemarkColseIv = (ImageView) mFragmentView.findViewById(R.id.teacher_remark_close_iv);

        mRecordsounding_cancle = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_cancle);
        mRecordsounding_confirm = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_rl_confirm);
        mRecordsoundingll = (LinearLayout) mFragmentView.findViewById(R.id.interview_recordsounding_ll);
        mTvtimeRecording = (TextView) mFragmentView.findViewById(R.id.tv_record_sounding_time);
        mIvRecordSound = (ImageView) mFragmentView.findViewById(R.id.imagview_confirm);

        mRecordNotSubmit_rl = (LinearLayout) mFragmentView.findViewById(R.id.interview_recordsound_rl_rerecording);
        mRecordNotSubmit_ll_play = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_ll_play);
        mUserNotSubmitAnswerProgressBar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.user_answer_progressbar_notsubmit);
        mUserNotSubmitAnswerProgressBar.setIsExistInsideText(false);
        mRecordNotSubmit_rl_submit = (LinearLayout) mFragmentView.findViewById(R.id.interview_recordsounding_rl_submit);
        mTvtimeNotSubm = (TextView) mFragmentView.findViewById(R.id.tv_record_play);
        mTvtimeNotSubmPlay = (TextView) mFragmentView.findViewById(R.id.tv_record_sounding_play_time);

        mAnswer_listen_ll = (RelativeLayout) mFragmentView.findViewById(R.id.interview_hadanswer_listen_rl);
        mTvtimeHadSumbPlay = (TextView) mFragmentView.findViewById(R.id.tv_recorded_sound_play_time);

        mTeacherRemarkRl = (RelativeLayout) mFragmentView.findViewById(R.id.teacher_remark_rl);         // 点评进度条整体

        // 用户答案进度条
        mUserAnswerProgressBar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.user_answer_progressbar_left);
        mUserAnswerProgressBar.setIsExistInsideText(false);
        mTeacherRemarkProgressBar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.teacher_remark_progressbar_right);    // 老师点评进度条
        mTeacherRemarkProgressBar.setIsExistInsideText(false);
        mRemarkNumb = (TextView) mFragmentView.findViewById(R.id.teacher_remark_number);            // 点评次数
        mQuestionHelpIv = (ImageView) mFragmentView.findViewById(R.id.question_help_iv);            // 问号图标
        mPurchasedLinkTv = (TextView) mFragmentView.findViewById(R.id.purchased_remark_tv);         // 购买链接
        mWaitRemarkingTv = (TextView) mFragmentView.findViewById(R.id.teacher_remark_waiting_tv);       // 点评中
        mUserAnswerPlayState = (TextView) mFragmentView.findViewById(R.id.user_answer_state_tv);           // 用户答案播放的状态
        mTeacherRemarkPlayTimeTv = (TextView) mFragmentView.findViewById(R.id.teacher_remark_time);   // 具有老师点评时的时间
        mTeacherRemarkPlayState = (TextView) mFragmentView.findViewById(R.id.teacher_remark_state_tv);  // 具有老师点评时播放的状态
        mNotRemarkLl = (LinearLayout) mFragmentView.findViewById(R.id.teacher_notremark_ll);      // 没有申请点评
        mExistRemarkLl = (LinearLayout) mFragmentView.findViewById(R.id.teacher_remark_offered_ll);     // 存在老师点评整体

        mQuestionAudioProgressbar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.question_audio_progressbar);
        mAnalysisAudioProgressbar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.analysis_audio_progressbar);
        mQuestionAudioIv = (ImageView) mFragmentView.findViewById(R.id.question_audio_listen_audio_iv);
        mAnalysisAudioIv = (ImageView) mFragmentView.findViewById(R.id.analysis_audio_listen_audio_iv);
        mQuestionAudioTv = (TextView) mFragmentView.findViewById(R.id.listenquestion_tv);
        mAnalysisAudioTv = (TextView) mFragmentView.findViewById(R.id.listenanswer_tv);
        if (isUnPurchasedOrPurchasedView.equals("PurchasedView")) {
            mQuestionAudioProgressbar.setIsExistInsideText(false);
            mAnalysisAudioProgressbar.setIsExistInsideText(false);
        }

        if (mQuestionBean != null) {
            //材料
            if (mQuestionBean.getMaterial() != null && !"".equals(mQuestionBean.getMaterial())) {
                mMerterialView.setVisibility(View.VISIBLE);
                mMerterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isCanTouch) {
                            ToastManager.showToast(mActivity, "请专心录音哦");
                            return;
                        }
                        final Intent intent = new Intent(mActivity, InterviewMaterialDetailActivity.class);
                        intent.putExtra("material", mQuestionBean.getMaterial());
                        mActivity.startActivity(intent);

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Material");
                        UmengManager.onEvent(mActivity, "InterviewQuestion", map);

                    }
                });
            } else {
                mMerterialView.setVisibility(View.GONE);
            }
            mAnalysisView.setVisibility(View.GONE);  // 解析答案的容器默认不显示
        }
    }

    /*
    *   初始化录音文件
    * */
    private void initRecordFile() {
        String userId = LoginModel.getUserId();
        mRecordFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + userId + "/user_answer/";            // 自己录音的路径
        mTeacherRemarkRecordFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + userId + "/teacher_audio/" + mQuestionBean.getId();     // 名师点评录音的路径
        FileManager.mkDir(mRecordFolder);
        FileManager.mkDir(mTeacherRemarkRecordFolder);

        mQuestionFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + LoginModel.getUserId() + "/question_audio/";
        mAnalysisFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + LoginModel.getUserId() + "/analysis_audio/";
        FileManager.mkDir(mQuestionFileFolder);
        FileManager.mkDir(mAnalysisFileFolder);

        mUserAnswerFilePath = mRecordFolder + mQuestionBean.getId() + ".amr";        // 录音存储的文件路径
        mTemporaryFilePath = mRecordFolder + mQuestionBean.getId() + "temp.amr";        // 临时文件的存储路径
    }

    public void initRecordListener() {   // 录音控件的监听事件
        // 监听录音控件
        mUnrecordsound_ll.setOnClickListener(OnClickListener);
        mRecordsounding_cancle.setOnClickListener(OnClickListener);
        mRecordsounding_confirm.setOnClickListener(OnClickListener);
        mRecordsoundingll.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl.setOnClickListener(OnClickListener);
        mRecordNotSubmit_ll_play.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl_submit.setOnClickListener(OnClickListener);
        mAnswer_listen_ll.setOnClickListener(OnClickListener);
        mTeacherRemarkColseIv.setOnClickListener(OnClickListener);
        mTeacherRemarkOpenIv.setOnClickListener(OnClickListener);
        // 名师点评部分
        mTeacherRemarkRl.setOnClickListener(OnClickListener);   // 名师点评
        mQuestionHelpIv.setOnClickListener(OnClickListener);    // 问号
        mPurchasedLinkTv.setOnClickListener(OnClickListener);   // 购买链接
    }

    public void showReminderToast() {
        ToastManager.showToast(mActivity, "还有30秒");
    }

    /*
    *   展示题目的文字
    * */
    public void showQuestion() {
        //下面的是展示问题的文字的处理
        String rich = getChildFragmentRich();
        addRichTextToContainer(mActivity, mQuestionContent, rich, true);
    }

    /*
    *   展示解析行的文字
    * */
    public void showAnswer() {
        // 解析行的文字处理
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.themecolor));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        //解析
        SpannableString analysis = new SpannableString("【解析】" + mQuestionBean.getAnalysis());
        analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAnalysisTv.setLineSpacing(0, 1.4f);
        mAnalysisTv.setText(analysis);

        //知识点
        SpannableString note = new SpannableString("【知识点】" + mQuestionBean.getNotes());
        note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNoteTv.setLineSpacing(0, 1.4f);
        mNoteTv.setText(note);

        //来源
        SpannableString source = new SpannableString("【来源】" + mQuestionBean.getFrom());
        source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSourceTv.setLineSpacing(0, 1.4f);
        mSourceTv.setText(source);

        //关键词
        SpannableString keywords = new SpannableString("【关键词】" + mQuestionBean.getKeywords());
        keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mKeywordsTv.setLineSpacing(0, 1.4f);
        mKeywordsTv.setText(keywords);
    }

    /**
     * 动态添加富文本
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public void addRichTextToContainer(final Activity activity,
                                       LinearLayout container,
                                       String rich,
                                       boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(activity);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(activity);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        for (final ParseManager.ParsedSegment segment : segments) {
            if (segment.text == null || segment.text.length() == 0) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(ContextCompat.getColor(mActivity, R.color.common_text));
                textView.setLineSpacing(0, 1.4f);
                textView.setText(segment.text);
                flowLayout.addView(textView);
                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }
            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);
                imgView.setImageResource(R.drawable.measure_loading_img);
                flowLayout.addView(imgView);

                // 异步加载图片
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度

                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();
                        if (data == null) return;
                        // 对小于指定尺寸的图片进行放大(2倍)
                        int width = data.getWidth();
                        int height = data.getHeight();
                        if (height < minHeight) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(2.0f, 2.0f);
                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                        }
                        imgView.setImageBitmap(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                };
                request.loadImage(segment.text.toString(), imageListener);
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        activity.startActivity(intent);
                    }
                });
            }
        }
        container.addView(flowLayout);
    }

    /**
     * 底部录音页面中各个控件的点击事件
     */
    public View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.interview_unrecordsound_ll) {    //如果点击了录音功能
                changePlayingMediaToPauseState();
                if(mActivity.mPlayingChildViewId != getChildViewPosition()){    //判断其他页面是否存在播放状态的播放器
                    if (mActivity.mMediaRecorderManagerUtil != null) {
                        stopPlay();
                    }
                    mActivity.changPlayingViewToDeafault();
                    mActivity.setIsExitsPlayingMedia(false);
                }
                isCanTouch = false;
                setIsCanTouch();
                mActivity.setCanBack(1);                  // 不可以按返回键
                changeRecordView(2);
                prepareRecord(); // 先准备录音
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Record");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_recordsounding_cancle) {         // 点击取消功能: 判断有效文件是否存在
                stopRecord();
                isCanTouch = true;
                setIsCanTouch();
                changePlayingMediaToPauseState();
                // 检查可提交文件是否存在,如果存在,回到未提交页面,否则回到未录音页面
                if (checkIsRecordFileExist() && isCanSubmit) {     // 录音文件存在,进入可提交页面
                    mActivity.setCanBack(2);
                    mOffset = 0;
                    mUserNotSubmitAnswerProgressBar.setProgress(100);
                    mTvtimeNotSubm.setText("听语音");
                    // 修改录音文字
                    String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
                    mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration) + 1));
                    changeRecordView(6);
                } else {
                    mActivity.setCanBack(0);                    // 不可以按返回键
                    String zero = "0\"";
                    mTvtimeRecording.setText(zero);
                    changeRecordView(1);
                }
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_recordsounding_rl_confirm) {     //点击确认功能
                if (isCanSubmit) {
                    stopRecord();
                    isCanTouch = true;
                    setIsCanTouch();
                    mActivity.setCanBack(2);                // 返回键设置不可返回,点击有弹窗
                    mOffset = 0;
                    mUserNotSubmitAnswerProgressBar.setProgress(100);
                    mTvtimeNotSubm.setText("听语音");
                    isStop = false;
                    // 此时录音文件已经存在,判断确认文件是否存在,时长是否为零
                    changeFileName();
                    changeRecordView(3);
                    showRecordedDuration();             // 显示录音的时长
                } else {
                    ToastManager.showToast(mActivity, "答题至少要一分钟哦");
                }
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Conform");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_recordsounding_ll) {             // 点击录音整体
                if (isCanTouch)
                    ToastManager.showToast(getActivity(), "正在录音,答题至少要一分钟哦");
            } else if (id == R.id.interview_recordsound_rl_rerecording) {      //点击重录
                if (mActivity.mMediaRecorderManagerUtil != null) {
                    changePlayingMediaToPauseState();
                    String zero = "0\"";
                    mTvtimeRecording.setText(zero);
                    mTvtimeNotSubm.setText("听一下");
                    isCanSubmit = true;
                    mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
                    isCanTouch = false;
                    setIsCanTouch();
                    mActivity.setCanBack(1);
                    changeRecordView(4);
                    prepareRecord();
                }
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Remake");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_recordsounding_ll_play) {       //点击未提交播放按钮
                if (isPlaying.equals(SUBMIT)) {
                    isStop = true;
                } else {
                    // 判断是否存在正在播放的语音
                    changePlayingMediaToPauseState();
                }
                if (isStop) {
                    isStop = false;
                    pausePlay();
                    mTvtimeNotSubm.setText("继续听");
                } else {
                    isStop = true;
                    mStatus = SUBMIT;
                    mTvtimeNotSubm.setText("听语音");
                    play(mUserAnswerFilePath);
                }
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Playaudio");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮
                changePlayingMediaToPauseState();
                if(mUserAnswerFilePath == null || mUserAnswerFilePath.length() <=0
                        || mQuestionBean == null || mQuestionType == null || mQuestionType.length() <= 0) {
                    ToastManager.showToast(mActivity, " 提交失败,请稍后再试");
                    return;
                }
                mModel.showSubmitAnswerProgressBar(mUserAnswerFilePath, mQuestionBean, FileManager.getVideoDuration(mUserAnswerFilePath), mQuestionType);
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Submit");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_hadanswer_listen_rl) {           // 已提交播放按钮
                if (isPlaying.equals(HADSUBMIT)) {
                    isUserHadSubmitAnswerPause = true;
                } else {
                    // 判断是否存在其他的正在播放的语音
                    changePlayingMediaToPauseState();
                }
                // 处理已提交录音
                dealUserHadSubmittedAudioPlayState();
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Answer");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.teacher_remark_rl) {        // 名师点评部分
                if (isPlaying.equals(TEACHERREMARK)) {
                    isTeacherAnswerPause = true;
                } else {
                    // 判断是否存在其他的正在播放的语音
                    changePlayingMediaToPauseState();
                }
                dealTeacherRemarkPart();                    // 处理名师点评部分
            } else if (id == R.id.question_help_iv) {         // 问号
                // 跳转到帮助页面
                skipToRemarkHelpActivity();
            } else if (id == R.id.purchased_remark_tv) {      // 购买链接
                // 直接跳转到购买页面
                Intent intent = new Intent(getActivity(), InterviewCommentProductActivity.class);
                startActivityForResult(intent, PAY_SUCCESS);
            } else if (id == R.id.teacher_remark_open_iv) {
                // 动画:从左向右
                Animation translateAnimation = new TranslateAnimation(0, 360, 0, 0);
                translateAnimation.setDuration(500);
                mTeacherRemarkOpenIv.startAnimation(translateAnimation);
//                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTeacherRemarkOpenIv.setVisibility(View.GONE);
                        mTeacherRemarkColseIv.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }else if (id == R.id.teacher_remark_close_iv) {
                // 动画:从右向左展开
                mTeacherRemarkColseIv.setVisibility(View.GONE);
                Animation translateAnimation = new TranslateAnimation(360, 0, 0, 0);
                translateAnimation.setDuration(500);
                mTeacherRemarkOpenIv.startAnimation(translateAnimation);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mTeacherRemarkOpenIv.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTeacherRemarkOpenIv.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }
    };

    /*
    *   购买名师点评后的回调
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PAY_SUCCESS && resultCode == PAY_SUCCESS) {
            mActivity.getData();    // 重新刷新页面
        }
    }

    /*
    *   将正在播放的语音变成暂停状态
    * */
    public void changePlayingMediaToPauseState() {
        switch (isPlaying) {
            case SUBMIT:
                pausePlay();
                isStop = false;
                mTvtimeNotSubm.setText("继续听");
                break;
            case HADSUBMIT:
                pausePlay();
                isUserHadSubmitAnswerPause = false;
                mUserAnswerPlayState.setText("继续听");
                break;
            case QUESTIONITEM:
                pausePlay();
                isQuestionAudioPause = false;
                mQuestionAudioIv.setImageResource(R.drawable.interview_listen_pause);
                mQuestionAudioTv.setText("继续听");
                break;
            case ANALYSISITEM:
                pausePlay();
                isAnalysisAudioPause = false;
                mAnalysisAudioIv.setImageResource(R.drawable.interview_listen_pause);
                mAnalysisAudioTv.setText("继续听");
                break;
            case TEACHERREMARK:
                pausePlay();
                isTeacherAnswerPause = false;
                mTeacherRemarkPlayState.setText("继续听");
                break;
            case NONE:
                isStop = false;
                isUserHadSubmitAnswerPause = false;
                isQuestionAudioPause = false;
                isAnalysisAudioPause = false;
                isTeacherAnswerPause = false;
                break;
        }
    }

    /**
     * 获取百分比
     *
     * @param a 分子
     * @param b 分母
     * @return int
     */
    private static int getPercent(int a, int b) {
        if (a == 0 || b == 0) return 0;
        double c = ((double) a / b) * 100;
        return (int) c;
    }

    /*
    *   跳转到帮助页面
    * */
    private void skipToRemarkHelpActivity() {
        Intent intent = new Intent(mActivity, InterviewCommentGuideActivity.class);
        startActivity(intent);
    }

    /*
    *   修改临时录音文件的名称为确认文件名
    * */
    private void changeFileName() {
        // 先检查确认文件是否已经存在
        if (checkIsRecordFileExist()) {
            FileManager.deleteFiles(mUserAnswerFilePath); // 删除掉
            mActivity.setIsHadUnSubmitRecordedAudio(false); // 文件已经存在,数量减一
        }
        // 通知activity已经录过音,且文件被保留
        mActivity.setIsHadUnSubmitRecordedAudio(true);  // 文件数量加一
        // 修改名字:将临时文件的名字变成可提交的文件名字
        FileManager.renameFile(mTemporaryFilePath, mUserAnswerFilePath);
    }

    /*
    *   检查录音文件是否已经存在
    * */
    public boolean checkIsRecordFileExist() {
        File recordFile = new File(mUserAnswerFilePath);
        String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
        return recordFile.exists() && !duration.equals("") && Integer.parseInt(duration) > 0;
    }

    private void changeTime() {
        if (mQuestionBean.getUser_audio_duration() >= UPPERLIMITRECORDTIME) {
            mTvtimeHadSumbPlay.setText(mModel.formatDateTime(UPPERLIMITRECORDTIME));
        } else {
            mTvtimeHadSumbPlay.setText(mModel.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
        }
    }

    private void changeRecordView(int i) {
        if (i == 1) {
            mRecordingView.setVisibility(View.GONE);
            mUnRecordView.setVisibility(View.VISIBLE);
            isCanSubmit = false;
            mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
        } else if (i == 2) {
            mUnRecordView.setVisibility(View.GONE);
            mRecordingView.setVisibility(View.VISIBLE);
        } else if (i == 3) {
            mRecordingView.setVisibility(View.GONE);
            mUnsubmitView.setVisibility(View.VISIBLE);
        } else if (i == 4) {
            mUnsubmitView.setVisibility(View.GONE);
            mRecordingView.setVisibility(View.VISIBLE);
        } else if (i == 5) {
            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);
            mPurchasedLinkTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            mPurchasedLinkTv.getPaint().setAntiAlias(true);//抗锯齿
        } else {
            mRecordingView.setVisibility(View.GONE);
            mUnsubmitView.setVisibility(View.VISIBLE);
        }
    }

    /*
    *   修改点评次数
    * */
    private void changeTeacherRemarkNum() {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.opencourse_btn_bg));
        //解析
        if(mTeacherRemarkRemainderNum == null || mTeacherRemarkRemainderNum.length() <= 0){
            mTeacherRemarkRemainderNum = "0";
        }
        SpannableString analysis = new SpannableString("您有" + String.valueOf(mTeacherRemarkRemainderNum) + "次点评申请");
        int length = String.valueOf(mTeacherRemarkRemainderNum).length() + 2;
        analysis.setSpan(colorSpan, 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRemarkNumb.setText(analysis);
    }

    /**
     * 准备录音
     */
    public void prepareRecord() {
        mActivity.mMediaRecorderManagerUtil.checkRecordStatus(new MediaRecordManagerUtil.ICheckRecordStatusListener() {
            @Override
            public void onCheckRecordStatusFinished(boolean enableRecord) {
                if (enableRecord) {
                    startRecord();         // 开始录音
                }
            }
        });
    }

    private void setIsCanTouch() {
        if (isCanTouch) {
            mActivity.mViewPager.setScroll(true);    // 让viewPager不拦截
        } else {
            mActivity.mViewPager.setScroll(false);       // 让viewpager拦截
        }
    }

    /**
     * 开始录音:将录音的文件先存入缓存文件中
     **/
    public void startRecord() {
        mActivity.mMediaRecorderManagerUtil.setRecordFilePath(mTemporaryFilePath);
        if (FileManager.isFile(mTemporaryFilePath)) {
            FileManager.deleteFiles(mTemporaryFilePath);
        }
        mActivity.mMediaRecorderManagerUtil.startRecord(new MediaRecordManagerUtil.IRecordDurationCallback() {
            @Override
            public void onRecordDuration(int duration) {
                // 处理录音的时长
                showRecordingDuration(duration);
            }
        });
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
    *   显示正在录音的时长
    * */
    private void showRecordingDuration(int duration) {
        if (duration >= 0 && duration <= UPPERLIMITRECORDTIME) {
            if (duration >= LOWERLIMITRECORDTIME) {
                isCanSubmit = true;
                mIvRecordSound.setImageResource(R.drawable.interview_confrim_blue);
                if (duration == SHOWREMINDERRECORDTIME) {
                    showReminderToast();
                }
            } else {
                mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
            }
            mTvtimeRecording.setText(mModel.formatDateTime(duration));
        } else{
            stopRecord();
            mTvtimeRecording.setText(mModel.formatDateTime(UPPERLIMITRECORDTIME));
        }
    }

    /*
    *  显示已经录音的时长
    * */
    private void showRecordedDuration() {
        String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
        if (Integer.parseInt(duration) >= UPPERLIMITRECORDTIME) {
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(UPPERLIMITRECORDTIME));
        } else {
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration) + 1));
        }
    }

    /*
    *   处理用户已提交后的语音: 播放,暂停,记录断点,播放..
    * */
    private void dealUserHadSubmittedAudioPlayState() {
        if (isUserHadSubmitAnswerPause) {
            isUserHadSubmitAnswerPause = false;
            pausePlay();
            mUserAnswerPlayState.setText("继续听");
        } else {
            mStatus = HADSUBMIT;
            isUserHadSubmitAnswerPause = true;
            dealDownLoadAudio(mRecordFolder, mQuestionBean.getUser_audio());
            mUserAnswerPlayState.setText("听语音");
        }
    }

    /*
    *  处理名师点评部分:
    * */
    private void dealTeacherRemarkPart() {
        //需要判断哪一个状态
        switch (mRemarkState) {
            case NOTAPPLYFORREMARK:
                // 先得判断申请的次数
                if(mTeacherRemarkRemainderNum == null || mTeacherRemarkRemainderNum.length() <= 0) return;
                if (Integer.parseInt(mTeacherRemarkRemainderNum) > 0) {
                    // 申请弹窗
                    popupApplyForRemarkAlert();
                } else {
                    // 购买alert
                    popupReminderPurchasedAlert();
                }
                break;
            case COMMENT:
                ToastManager.showToastCenter(mActivity, "老师正在点评中，请耐心等待哦！");
                break;
            case HADREMARKED:
                dealTeacherRemarkAudioState();   // 已经点评:播放名师点评
                break;
            default:
                break;
        }
    }

    /*
    *   处理名师点评的语音部分
    * */
    private void dealTeacherRemarkAudioState() {
        // 判断已听还是未听,再点击事件中处理
        if (mQuestionBean.getComment_status() == UNLISTEN ) {
            mModel.mRequest.updateCommentStatusToListen(mQuestionBean.getId(), "hear"); // 发送:已听
        }
        dealTeacherRemarkAudioPlayState();
    }

    /*
    *   处理名师点评的语音的播放: 播放,暂停,播放...
    * */
    private void dealTeacherRemarkAudioPlayState() {
        if (isTeacherAnswerPause) {         // 暂停-->播放
            isTeacherAnswerPause = false;
            mTeacherRemarkPlayState.setText("继续听");
            pausePlay();
        } else {
            isTeacherAnswerPause = true;
            mStatus = TEACHERREMARK;
            mTeacherRemarkPlayState.setText("收听点评");
            // 判断名师点评目录中是否存在文件
            if( !checkIsExistTeacherRemarkAudio()){       // 如果不存在去下载语音
                mModel.setTimeStamp(mTeacherRemarkAudioTimeStamp);
                dealDownLoadAudio(mTeacherRemarkRecordFolder, mQuestionBean.getTeacher_audio());
            } else{
                // 判断时间戳是否一致
                isEqualsExistTeacherRemarkAudioTimeStamp();
            }
        }
    }

    /*
    *  处理题目行的暂停,播放
    * */
    public void dealQuestionAudioPlayState() {
        if (isQuestionAudioPause) {         // 暂停-->播放
            isQuestionAudioPause = false;
            mQuestionAudioTv.setText("继续听");
            pausePlay();
        } else {
            isQuestionAudioPause = true;
            mStatus = QUESTIONITEM;
            mQuestionAudioTv.setText("听语音");
            dealDownLoadAudio(mQuestionFileFolder, mQuestionBean.getQuestion_audio());
        }
    }

    /*
   *  处理解析行的暂停,播放
   * */
    public void dealAnalysisAudioPlayState() {
        if (isAnalysisAudioPause) {         // 暂停-->播放
            isAnalysisAudioPause = false;
            mAnalysisAudioTv.setText("继续听");
            pausePlay();
        } else {
            isAnalysisAudioPause = true;
            mStatus = ANALYSISITEM;
            mAnalysisAudioTv.setText("听语音");
            dealDownLoadAudio(mAnalysisFileFolder, mQuestionBean.getAnalysis_audio());
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mActivity.mMediaRecorderManagerUtil.stopReocrd();
    }

    /*
    *   停止播放语音
    * */
    public void stopPlay() {
        mActivity.mMediaRecorderManagerUtil.stopPlay();
    }

    /*
    *   播放语音: 需要四个不同的断点
    * */
    public void play(String filePath) {
        // 检验是否存在其他应用正在播放音乐: 获取音频焦点
        getAudioStreamFocus();
        // 检验是否存在别的页面正在播放的播放器
        if (mActivity.mMediaRecorderManagerUtil != null) {
            mActivity.mMediaRecorderManagerUtil.stopPlay();
            mActivity.changPlayingViewToDeafault();
        }
        if (filePath.equals("")) return;
        mActivity.mMediaRecorderManagerUtil.setPlayFilePath(filePath);
        //播放的断点
        mActivity.mMediaRecorderManagerUtil.startPlay(getOffset(), new MediaRecordManagerUtil.IPlayCompleteCallback() {
            @Override
            public void onPlayComplete() {
                ToastManager.showToast(mActivity, "播放完成");
                dealPlayedCompletedViewState();                 // 播放完成处理
            }
        }, new MediaRecordManagerUtil.IPlayFileCountdownCallback() {
            @Override
            public void onPlayCountdown(int unPlayDur) {
                dealPlayingViewState(unPlayDur);                // 播放时的处理
            }
        });
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
    *   获取音频流焦点
    * */
    private void getAudioStreamFocus() {
        mActivity.startService(new Intent(mActivity, MediaPlayingService.class)); // 开启服务
    }

    /*
    *   获取不同播放状态下的断点值
    * */
    private int getOffset() {
        int offset = 0;
        switch (mStatus) {
            case QUESTIONITEM:
                offset = mQuestionAudioOffset;
                break;
            case ANALYSISITEM:
                offset = mAnalysisAudioOffset;
                break;
            case TEACHERREMARK:
                offset = mTeacherRemarkAudioOffset;
                break;
            case SUBMIT:
                offset = mOffset;
                break;
            case HADSUBMIT:
                offset = mOffset;
                break;
        }
        return offset;
    }

    /*
    *   播放时状态的处理
    * */
    private void dealPlayingViewState(int unPlayDur) {
        mActivity.setPlayingChildViewId(getChildViewPosition());        // 获取存在播放状态的状态的view的id
        switch (mStatus) {
            case SUBMIT:                     // 未提交时
                isPlaying = SUBMIT;
                mTvtimeNotSubmPlay.setText(mModel.formatDateTime(unPlayDur));
                String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
                mUserNotSubmitAnswerProgressBar.setProgress(getPercent(unPlayDur, Integer.parseInt(duration) + 1));
                break;
            case HADSUBMIT:                 // 已提交时: 存在时差,文字快于进度条
                isPlaying = HADSUBMIT;
                mUserAnswerProgressBar.setProgress(getPercent(unPlayDur, mQuestionBean.getUser_audio_duration()));
                mTvtimeHadSumbPlay.setText(mModel.formatDateTime(unPlayDur));
                break;
            case TEACHERREMARK:             // 名师点评
                isPlaying = TEACHERREMARK;
                mTeacherRemarkPlayTimeTv.setText(mModel.formatDateTime(unPlayDur));
                mTeacherRemarkProgressBar.setProgress(getPercent(unPlayDur, mQuestionBean.getTeacher_audio_duration()));
                break;
            case QUESTIONITEM:              // 题目行语音
                isPlaying = QUESTIONITEM;
                mQuestionAudioProgressbar.setProgress(getPercent(unPlayDur, mQuestionBean.getQuestion_audio_duration()));   // 题目行的进度条
                // 中间图片的动画集合
                mediaPlayingAnimation(true);
                break;
            case ANALYSISITEM:              // 解析行语音
                isPlaying = ANALYSISITEM;
                mAnalysisAudioProgressbar.setProgress(getPercent(unPlayDur, mQuestionBean.getAnalysis_audio_duration()));   // 解析行的进度条
                // 中间图片的动画集合
                mediaPlayingAnimation(true);
                break;
            default:
                break;
        }
        mActivity.setPlayingViewState(isPlaying);           // 需要传值给activity: 播放器属于哪一个正在播放
        mActivity.setIsExitsPlayingMedia(true);
    }

    /*
    *  中间图片的动画:播放,停止
    * */
    public void mediaPlayingAnimation(boolean isPlaying) {
        if (isPlaying) {
            if (mStatus.equals(QUESTIONITEM)) {
                mQuestionAudioIv.setImageResource(R.drawable.interview_audio_playing_animation);
                AnimationDrawable questionAudioIv = (AnimationDrawable) mQuestionAudioIv.getDrawable();
                questionAudioIv.start();
            } else if (mStatus.equals(ANALYSISITEM)) {
                mAnalysisAudioIv.setImageResource(R.drawable.interview_audio_playing_animation);
                AnimationDrawable analysisAudioIv = (AnimationDrawable) mAnalysisAudioIv.getDrawable();
                analysisAudioIv.start();
            }
        } else {
            if (mStatus.equals(QUESTIONITEM)) {
                mQuestionAudioIv.setImageResource(R.drawable.interview_audio_playing_animation);
                AnimationDrawable questionAudioIv = (AnimationDrawable) mQuestionAudioIv.getDrawable();
                questionAudioIv.stop();
            } else if (mStatus.equals(ANALYSISITEM)) {
                mAnalysisAudioIv.setImageResource(R.drawable.interview_audio_playing_animation);
                AnimationDrawable analysisAudioIv = (AnimationDrawable) mAnalysisAudioIv.getDrawable();
                analysisAudioIv.stop();
            }
        }
    }

    /*
    *   暂停状态: 记录断点,记录各自暂停的状态
    * */
    private void pausePlay() {
        isPlaying = NONE;
        mActivity.setPlayingViewState(isPlaying);
        mActivity.setIsExitsPlayingMedia(false);
        mActivity.mMediaRecorderManagerUtil.playOnPause(new MediaRecordManagerUtil.IPlayFileOffsetCallback() {
            @Override
            public void onPlayOffset(int offset) {
                switch (mStatus) {
                    case SUBMIT:
                        mOffset = offset;
                        break;
                    case HADSUBMIT:
                        mOffset = offset;
                        break;
                    case QUESTIONITEM:
                        mQuestionAudioOffset = offset;
                        mediaPlayingAnimation(false);
                        mQuestionAudioIv.setImageResource(R.drawable.interview_listen_pause);
                        break;
                    case ANALYSISITEM:
                        mAnalysisAudioOffset = offset;
                        mediaPlayingAnimation(false);
                        mAnalysisAudioIv.setImageResource(R.drawable.interview_listen_pause);
                        break;
                    case TEACHERREMARK:
                        mTeacherRemarkAudioOffset = offset;
                        break;
                }
            }
        });
    }

    /*
    *   播放完成时的状态
    * */
    private void dealPlayedCompletedViewState() {
        isPlaying = NONE;
        mActivity.setPlayingViewState(isPlaying);
        mActivity.setIsExitsPlayingMedia(false);
        switch (mStatus) {
            case SUBMIT:
                String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
                mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration) + 1));
                isStop = false;
                mTvtimeNotSubm.setText("听语音");
                mUserNotSubmitAnswerProgressBar.setProgress(100);
                mOffset = 0;
                break;
            case HADSUBMIT:
                changeTime();
                isUserHadSubmitAnswerPause = false;
                mStatus = HADSUBMIT;
                mUserAnswerProgressBar.setProgress(100);
                mOffset = 0;
                break;
            case QUESTIONITEM:
                isQuestionAudioPause = false;
                mQuestionAudioProgressbar.setProgress(100);
                mQuestionAudioOffset = 0;
                mediaPlayingAnimation(false);
                mQuestionAudioIv.setImageResource(R.drawable.interview_listen_audio);
                break;
            case ANALYSISITEM:
                isAnalysisAudioPause = false;
                mAnalysisAudioProgressbar.setProgress(100);
                mAnalysisAudioOffset = 0;
                mediaPlayingAnimation(false);
                mAnalysisAudioIv.setImageResource(R.drawable.interview_listen_audio);
                break;
            case TEACHERREMARK:
                mTeacherRemarkPlayTimeTv.setText(mModel.formatDateTime(mQuestionBean.getTeacher_audio_duration()));
                mTeacherRemarkProgressBar.setProgress(100);
                mTeacherRemarkAudioOffset = 0;
                break;
        }
    }

    /*
    *  下载语音
    * */
    public void dealDownLoadAudio(String mFileFolder, String vedioUrl) {   //点击事件中传递参数
        String filePath;
        String zipFilePath;
        if(mStatus.equals(TEACHERREMARK)){
            filePath = mFileFolder + "/" + mTeacherRemarkAudioTimeStamp + ".amr";
            zipFilePath = mFileFolder + "/" + mTeacherRemarkAudioTimeStamp + ".zip";
        } else{
            filePath = mFileFolder + mQuestionBean.getId() + ".amr";
            zipFilePath = mFileFolder + mQuestionBean.getId() + ".zip";
        }
        File file = new File(filePath);
        File zipFile = new File(zipFilePath);
        if (file.exists()) {                                   // 如果文件存在直接播放
            play(filePath);
        } else if (zipFile.exists()) {
            FileManager.unzipFiles(mFileFolder, zipFilePath); // 将参数二所对应的文件解压到参数一对应的文件中
            FileManager.deleteFiles(zipFilePath);
            play(filePath);
        } else {                               // 文件不存在进行下载
            if (vedioUrl == null) return;
            if (vedioUrl.contains(".amr")) {
                downLoadAudio(vedioUrl, mFileFolder, filePath, null);

            } else if (vedioUrl.contains(".zip")) {
                downLoadAudio(vedioUrl, mFileFolder, filePath, zipFilePath);
            }
        }
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
    *  处理下载的语音:解压 : 传进来id
    * */
    private void downLoadAudio(String vedioUrl, final String mFileFolder, final String filePath, String zipFilePath) {
        String localFilePath;
        if (zipFilePath != null) {       // .zip 格式
            localFilePath = zipFilePath;
        } else {
            localFilePath = filePath;
        }
        mModel.downloadVoiceVideo(mActivity, vedioUrl, mFileFolder, localFilePath, mQuestionBean.getId(), new ICommonCallback() {        // mFileFolder时解压后存文件的目录
            @Override
            public void callback(boolean success) {
                if (success) {
                    // 获取封装好的文件
                    play(filePath);
                }
            }
        });
    }
    /*
    *   判断名师点评目录中是否存在文件
    * */
    private boolean checkIsExistTeacherRemarkAudio(){
        String teacherRemarkRecordFolderfilePath = mTeacherRemarkRecordFolder ;
        File fileDir = new File(teacherRemarkRecordFolderfilePath);
        mTeacherRemarkAudioFiles = fileDir.listFiles();
        return mTeacherRemarkAudioFiles != null && mTeacherRemarkAudioFiles.length > 0;
    }
    /*
    *   判断已经存在的名师点评的语音时间戳是否一致
    * */
    private void isEqualsExistTeacherRemarkAudioTimeStamp(){
        if(mTeacherRemarkAudioFiles == null || mTeacherRemarkAudioFiles.length <= 0) return ;

        for(File downloadFile: mTeacherRemarkAudioFiles) {
            if(downloadFile.exists() && downloadFile.isFile()){
                String absolutePath = downloadFile.getAbsolutePath();
                String substringTimeStamp = absolutePath.substring(absolutePath.length() - 18, absolutePath.length() - 4);
                // 进行判断
                if(mTeacherRemarkAudioTimeStamp.equals(substringTimeStamp)){
                    // 直接播放
                    String filePath = mTeacherRemarkRecordFolder + "/" + mTeacherRemarkAudioTimeStamp + ".amr";
                    play(filePath);
                } else{
                    // 将原来的删除掉,再重新下载
                    FileManager.deleteFiles(downloadFile.getAbsolutePath());
                    // 重新下载
                    mModel.setTimeStamp(mTeacherRemarkAudioTimeStamp);
                    dealDownLoadAudio(mTeacherRemarkRecordFolder, mQuestionBean.getTeacher_audio());
                }
            }
        }
    }

    /*
   *   弹出引导浮层
   * */
    private void popupGuideFloating() {
        if (mActivity == null) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity, R.style.NoBackGroundDialog).create();
        mAalertDialog.setCanceledOnTouchOutside(true);
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_applyfor_remark_guide_floating);

        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色

        mWindow.setGravity(Gravity.END | Gravity.BOTTOM);

        WindowManager.LayoutParams lp = mWindow.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mTeacherRemarkProgressBar.measure(width, height);
        int teacherRemarkProgressBarHeight = mTeacherRemarkProgressBar.getMeasuredHeight();     // 测量的是它的父控件的大小

        lp.x = (int) (metrics.widthPixels * 0.04);
        lp.y = teacherRemarkProgressBarHeight + 10;           // y轴偏移量
        lp.width = (int) (metrics.widthPixels * 0.75);
        lp.height = (int) (metrics.heightPixels * 0.2);
        lp.alpha = 0.8f;

        mWindow.setAttributes(lp);

        ImageView cancle = (ImageView) mWindow.findViewById(R.id.cancel_guide_floating);
        LinearLayout lookRemarkLl = (LinearLayout) mWindow.findViewById(R.id.lookover_teacher_remark);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
            }
        });
        lookRemarkLl.setOnClickListener(new View.OnClickListener() {        // 了解名师点评
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                skipToRemarkHelpActivity(); // 跳转到帮助页面
            }
        });
    }

    /*
    *    弹出申请名师点评弹窗
    * */
    private void popupApplyForRemarkAlert() {
        if (mActivity == null) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_applyfor_remark);

        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        mWindow.getDecorView().setPadding(0, 0, 0, 0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        ImageView cancleRemarkIv = (ImageView) mWindow.findViewById(R.id.cancle_applyfor_remark);
        TextView confirmRemarkTv = (TextView) mWindow.findViewById(R.id.confirm_applyfor_remark);
        TextView applyForRemarkNumbTv = (TextView) mWindow.findViewById(R.id.applyfor_remainder_numb);      // 点评还剩的次数
        String numText = mTeacherRemarkRemainderNum + "次";
        applyForRemarkNumbTv.setText(numText);

        cancleRemarkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
            }
        });

        confirmRemarkTv.setOnClickListener(new View.OnClickListener() {     // 确认申请点评的按钮
            @Override
            public void onClick(View view) {
                // 发送请求:请求点评, 需要回调
                mModel.mRequest.applyForTeacherRemark(mQuestionBean.getId(), "buy");
                mAalertDialog.dismiss();
            }
        });
    }

    /*
    *   申请后的提示弹窗
    *  */
    @Override
    public void popupAppliedForRemarkReminderAlert() {
        if (mActivity == null) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_applyfor_remark_reminder);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = mWindow.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lp.width = (int) (metrics.widthPixels * 0.9);
        lp.height = (int) (metrics.heightPixels * 0.35);

        mWindow.setAttributes(lp);

        TextView confirm = (TextView) mWindow.findViewById(R.id.confirm);
        TextView firstLineTv = (TextView) mWindow.findViewById(R.id.firstLine_tv);
        TextView secondLineTv = (TextView) mWindow.findViewById(R.id.secondLine_tv);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.opencourse_grade_num));
        SpannableString analysisFirst = new SpannableString("您将在" + "3个" + "工作日内收到名师点评。");
        SpannableString analysisSecond = new SpannableString("请在" + "记录-面试-名师点评" + "中查看。");

        analysisFirst.setSpan(colorSpan, 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysisSecond.setSpan(colorSpan, 2, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        firstLineTv.setText(analysisFirst);
        secondLineTv.setText(analysisSecond);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
            }
        });
    }

    /*
    *   提示购买的alert
    * */
    private void popupReminderPurchasedAlert() {
        if (mActivity.isFinishing()) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击

        mAalertDialog.show();
        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_reminder_purchased_remark);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色

        mWindow.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = mWindow.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        lp.width = (int) (metrics.widthPixels * 0.8);
        lp.height = (int) (metrics.heightPixels * 0.35);

        mWindow.setAttributes(lp);

        TextView cancle = (TextView) mWindow.findViewById(R.id.purchased_remark_cancle);
        TextView confirm = (TextView) mWindow.findViewById(R.id.purchased_remark_confirm);
        TextView teacherRemarkRemainderNum = (TextView) mWindow.findViewById(R.id.purchase_teacherRemark_remainder_num);
        String textRemainderNum;
        if (Integer.parseInt(mTeacherRemarkRemainderNum) <= 0) {
            textRemainderNum = "您当前共有0次点评申请,请先购买名师点评哦!";
        } else {
            textRemainderNum = "您当前共有" + mTeacherRemarkRemainderNum + "次点评申请哦!";
        }
        teacherRemarkRemainderNum.setText(textRemainderNum);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                // 购买
                final Intent intent = new Intent(getActivity(), InterviewCommentProductActivity.class);
                startActivityForResult(intent, PAY_SUCCESS);

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
            }
        });
    }

    /*
    *   广播接收者: 处理电话监听状态
    * */
    private class PhoneBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 如果是拨打电话
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                changePlayingMediaToPauseState();
            } else {
                // 如果是来电
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:   // 响铃
                        changePlayingMediaToPauseState();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:   //通话中
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:      //待机状态
                        break;
                }
            }
        }
    }

    /*
    *   获取音频焦点的广播接收者
    * */
    private class AudioStreamFocusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isGetAudioFocus = intent.getExtras().getBoolean("isGetAudioFocus", false); // 是否获取到了焦点
            if (!isGetAudioFocus) {
                changePlayingMediaToPauseState();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mPhoneBroadcastReceiver);  // 取消注册广播
        mActivity.unregisterReceiver(mAudioStreamFocusReceiver);  // 取消注册广播
        mActivity.stopService(new Intent(mActivity, MediaPlayingService.class));          // 取消注册服务

    }
}