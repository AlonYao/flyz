package com.appublisher.quizbank.common.interview.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.view.WindowManager;
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
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.model.InterviewUnPurchasedModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;
import com.appublisher.quizbank.common.interview.network.InterviewModel;
import com.appublisher.quizbank.common.utils.MediaRecorderManager;
import com.appublisher.quizbank.common.utils.TimeUtils;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huaxiao on 2016/12/16.
 * 用来展示未付费页面的Fragment,本类中只处理控件view,数据展示和控件点击交给InterviewUnPurchasdModel
 */

public class InterviewUnPurchasedFragment extends InterviewDetailBaseFragment {

    private View mUnPurchasedView;
    public ViewPager mViewPager;
    private View merterialView;
    private View analysisSwitchView;
    private LinearLayout questionContent;
    private TextView analysisSwitchTv;
    private ImageView analysisIm;
    private TextView reminderTv;
    private TextView analysisTv;
    private TextView noteTv;
    private TextView sourceTv;
    private TextView keywordsTv;
    private static final String ARGS_QUESTIONBEAN = "questionbean";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_LISTLENGTH = "listLength";
    private static final String QUESTIONTYPE = "questiontype";
    private InterviewPaperDetailResp.QuestionsBean mQuestionbean;
    private int mPosition;
    private int mListLength;
    public View mUnRecordView;
    private View mRecordingView;
    private View mUnsubmitView;
    private View mRecordedView;
    private LinearLayout mUnrecordsound_ll;
    private RelativeLayout mRecordsounding_cancle;
    private RelativeLayout mRecordsounding_confirm;
    private LinearLayout mRecordsoundingll;
    private TextView mTvtimeRecording;
    private LinearLayout mRecordNotSubmit_ll_play;
    private RelativeLayout mRecordNotSubmit_rl;
    private RelativeLayout mRecordNotSubmit_rl_submit;
    private TextView mTvtimeNotSubmPlay;
    private LinearLayout mAnswer_listen_ll;
    public TextView mTvtimeHadSumbPlay;
    private ImageView mIvRecordSound;
    private boolean isBlue;    // 记录是否满足录音时间后,确认按钮是否变蓝
    private String fileFolder;
    private int question_id;
    private String userAnswerFilePath;
    private Timer mTimer;
    private int timeRecording;
    private Handler handler;
    private final int TIME_CANCEL = 1;
    private final int RECORD_TIME = 2;
    private final int RECORD_SUBMIT = 3;
    private final int PLAYING = 4;           // 正在播放
    private final int PLAYINGSUBMIT = 5;           // 已提交后的正在播放
    private int timePlaying;
    private TextView mTvtimeNotSubm;
    private InterviewPaperDetailActivity mActivity;
    private boolean isStop;           //是否停止播放
    private InterviewUnPurchasedModel mUnPurchasedModel;
    private String RECORDABLE = "recordable";      //可录音
    private String CONFIRMABLE = "confirmable";   //可确认
    private String SUBMIT = "submit";              //可提交
    private String HADSUBMIT = "hadSubmit";      // 已提交
    private String status;
    private int user_audio_durationTime;
    private String questionType;
    private LinearLayout analysisView;
    private MediaRecorderManager mediaRecorderManager;


    public static Fragment newInstance(String questionbean, int position, int listLength, InterviewPaperDetailActivity mctivity,String questionType) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONBEAN, questionbean);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_LISTLENGTH, listLength);
        args.putString(QUESTIONTYPE, questionType);    // 问题的类型

        InterviewUnPurchasedFragment fragment = new InterviewUnPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (InterviewPaperDetailActivity) getActivity();

        mQuestionbean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        questionType = getArguments().getString(QUESTIONTYPE);  // 问题的类型

        mListLength = getArguments().getInt(ARGS_LISTLENGTH);

        mPosition = getArguments().getInt(ARGS_POSITION);          // 问题的索引

        user_audio_durationTime = mQuestionbean.getUser_audio_duration();    // 用户的录音时长

        // 创建自己的model
        mUnPurchasedModel = new InterviewUnPurchasedModel(mActivity);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        isStop = false;
        mActivity.setCanBack(0);            // 默认设置返回键可以点击

        // 未付费页面的容器
        mUnPurchasedView = inflater.inflate(R.layout.interview_question_item_recordsound_notpayfor, container, false);
        initView();
        initFile();
        initListener();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RECORD_SUBMIT:
                        if (timeRecording >= 60 && timeRecording <= 360) {
                            status = CONFIRMABLE;
                            mIvRecordSound.setImageResource(R.drawable.interview_confrim_blue);
                        }
                    case RECORD_TIME:
                        if (timeRecording >= 0 && timeRecording <= 360) {
                            if(timeRecording == 330){
                                ToastManager.showToast(mActivity,"还有30秒");
                            }
                            mTvtimeRecording.setText(TimeUtils.formatDateTime(timeRecording));

                        }else if(timeRecording > 360){
                            handler.sendEmptyMessage(TIME_CANCEL);
                            mediaRecorderManager.stop();
                        }
                        break;
                    case PLAYING:
                        if (timePlaying >= 0) {
                            mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(timePlaying));
                        } else {
                            mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(timeRecording));
                            handler.sendEmptyMessage(TIME_CANCEL);
                            isStop = false;
                            mTvtimeNotSubm.setText("听一下");
                        }
                        break;
                    case PLAYINGSUBMIT:
                        if (timePlaying >= 0) {
                            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(timePlaying));
                        }else{
                            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
                            isStop = false;
                            status = HADSUBMIT;
                            handler.sendEmptyMessage(TIME_CANCEL);
                        }
                        break;
                    case TIME_CANCEL:
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                        break;
                }
            }
        };
        return mUnPurchasedView;
    }

    private void checkedIsAnswer() {

        if(mQuestionbean.getUser_audio() !=null &&  mQuestionbean.getUser_audio().length() > 0){

            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
            status = HADSUBMIT;

        }else{
            status = RECORDABLE;
            mUnRecordView.setVisibility(View.VISIBLE);
            analysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }

    private void initFile() {
        String userId = LoginModel.getUserId();
        fileFolder = FileManager.getRootFilePath(mActivity) + "/interview/"  + userId + "/record/";;

        question_id = mQuestionbean.getId();            // 具体哪一个问题

        FileManager.mkDir(fileFolder);
        // 录音存储的文件路径
        userAnswerFilePath = fileFolder + question_id + ".amr";
    }

    private void initView() {

        merterialView = mUnPurchasedView.findViewById(R.id.meterial_rl);

        analysisSwitchView = mUnPurchasedView.findViewById(R.id.analysis_switch_rl);

        questionContent = (LinearLayout) mUnPurchasedView.findViewById(R.id.question_content);
        analysisView = (LinearLayout) mUnPurchasedView.findViewById(R.id.unpurchased_analysis_ll);

        analysisIm = (ImageView) mUnPurchasedView.findViewById(R.id.analysis_im);
        reminderTv = (TextView) mUnPurchasedView.findViewById(R.id.open_analysis);
        analysisTv = (TextView) mUnPurchasedView.findViewById(R.id.analysis_tv);
        noteTv = (TextView) mUnPurchasedView.findViewById(R.id.note_tv);
        sourceTv = (TextView) mUnPurchasedView.findViewById(R.id.source_tv);
        keywordsTv = (TextView) mUnPurchasedView.findViewById(R.id.keywords_tv);

        initRecordSoundView();  //初始化录音界面的布局和控件
        checkedIsAnswer();     // 需要一个返回到提交字段
    }

    /**
     * 录音界面布局额控件,并设置监听
     */
    private void initRecordSoundView() {

        mUnRecordView = mUnPurchasedView.findViewById(R.id.interview_popup_unrecordsound);
        mRecordingView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounding);
        mUnsubmitView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounding_unsubmit);
        mRecordedView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounded);

        // 初始化各自的控件
        mUnrecordsound_ll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_unrecordsound_ll);

        mRecordsounding_cancle = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_cancle);
        mRecordsounding_confirm = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_rl_confirm);
        mRecordsoundingll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_ll);
        mTvtimeRecording = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_sounding_time);
        mIvRecordSound = (ImageView) mUnPurchasedView.findViewById(R.id.imagview_confirm);

        mRecordNotSubmit_rl = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsound_rl_rerecording);
        mRecordNotSubmit_ll_play = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_ll_play);
        mRecordNotSubmit_rl_submit = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_rl_submit);
        mTvtimeNotSubm = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_play);
        mTvtimeNotSubmPlay = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_sounding_play_time);

        mAnswer_listen_ll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_hadanswer_listen_ll);
        mTvtimeHadSumbPlay = (TextView) mUnPurchasedView.findViewById(R.id.tv_recorded_sound_play_time);

    }

    private void initListener() {

        if (mQuestionbean != null && mPosition < mListLength && mListLength > 0) {

            //材料
            if (mQuestionbean.getMaterial() != null && !"".equals(mQuestionbean.getMaterial())) {
                merterialView.setVisibility(View.VISIBLE);
                merterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(mActivity, InterviewMaterialDetailActivity.class);
                        intent.putExtra("material", mQuestionbean.getMaterial());
                        mActivity.startActivity(intent);

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Material");
                        UmengManager.onEvent(mActivity, "InterviewProblem", map);
                    }
                });
            } else {
                merterialView.setVisibility(View.GONE);
            }

            analysisView.setVisibility(View.GONE);  // 解析答案的容器默认不显示

            /**
             *  展开解析时需要监听是否已经答题: 用一个常量字符记录(在基类中处理录音页面的逻辑时)
             * **/
            analysisSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {     // 解析行的逻辑处理:
                    if (isDone() || isBuyAll() || isBuySingle()) {               // 已经答题
                        if (analysisView.getVisibility() == View.VISIBLE) {
                            analysisView.setVisibility(View.GONE);
                            analysisIm.setImageResource(R.drawable.interview_answer_lookover);
                            if ("notice".equals(mQuestionbean.getStatus())) {
                                reminderTv.setText("查看");
                            } else {
                                reminderTv.setText("查看");
                            }
                        } else {
                            // 如果答完题状态
                            analysisView.setVisibility(View.VISIBLE);
                            analysisIm.setImageResource(R.drawable.interview_answer_packup);
                            if ("notice".equals(mQuestionbean.getStatus())) {
                                reminderTv.setText("收起");
                            } else {
                                reminderTv.setText("收起");
                            }
                        }
                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Answer");
                        UmengManager.onEvent(mActivity, "InterviewProblem", map);
                    } else {                      // 未答题
                        // 弹窗处理:三个item
                        mActivity.mUnPurchasedModel.showNoAnswerDialog();
                    }

                }
            });

            //下面的是展示问题的文字的处理
            String rich = (mPosition + 1) + "/" + mListLength + "  " + mQuestionbean.getQuestion();
            addRichTextToContainer(mActivity, questionContent, rich, true);

            showAnswer(); // 展示答案解析
        }

        // 监听录音控件
        mUnrecordsound_ll.setOnClickListener(OnClickListener);
        mRecordsounding_cancle.setOnClickListener(OnClickListener);
        mRecordsounding_confirm.setOnClickListener(OnClickListener);
        mRecordsoundingll.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl.setOnClickListener(OnClickListener);
        mRecordNotSubmit_ll_play.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl_submit.setOnClickListener(OnClickListener);
        mAnswer_listen_ll.setOnClickListener(OnClickListener);
    }

    private boolean isDone() {
        return mQuestionbean != null && mQuestionbean.getUser_audio().length() > 0;
    }

    private boolean isBuySingle() {
        return mQuestionbean != null && mQuestionbean.isPurchased_audio();
    }

    private boolean isBuyAll() {
        if (mActivity == null) return false;
        InterviewPaperDetailResp.AllAudioBean bean = mActivity.getAllAudioBean();
        return bean != null && bean.is_purchased();
    }

    //展示答案
    private void showAnswer() {

        // 解析行的文字处理

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.themecolor));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        //解析
        SpannableString analysis =   new SpannableString("【解析】" + mQuestionbean.getAnalysis());
        analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysisTv.setLineSpacing(0, 1.4f);
        analysisTv.setText(analysis);

        //知识点
        SpannableString note = new SpannableString("【知识点】" + mQuestionbean.getNotes());
        note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        noteTv.setLineSpacing(0, 1.4f);
        noteTv.setText(note);

        //来源
        SpannableString source = new SpannableString("【来源】" + mQuestionbean.getFrom());
        source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sourceTv.setLineSpacing(0, 1.4f);
        sourceTv.setText(source);

        //关键词
        SpannableString keywords = new SpannableString("【关键词】" + mQuestionbean.getKeywords());
        keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywordsTv.setLineSpacing(0, 1.4f);
        keywordsTv.setText(keywords);

    }
    /**
     *   底部录音页面中各个控件的点击事件
     * */
    public View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.interview_unrecordsound_ll) {    //如果点击了录音功能

                // 需要判断录音器是否已经存在,如果存在销毁,停止
                if(mActivity.recorderManager != null){
                    mActivity.recorderManager.stop();

                    handler.sendEmptyMessage(TIME_CANCEL);
                    mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
                    prepareRecord(); // 先准备录音

                }else{
                    if(status == RECORDABLE) {
                        prepareRecord();    //先准备录音
                    }
                }
            } else if (id == R.id.interview_recordsounding_cancle) {   // 点击取消功能

                mActivity.viewPager.setScroll(true);    // 让viewPager不拦截
                mActivity.setCanBack(0);                  // 可以按返回键
                status = RECORDABLE;
                mTvtimeRecording.setText("0" + "\"");
                stopRecord();
                mRecordingView.setVisibility(View.GONE);
                mUnRecordView.setVisibility(View.VISIBLE);
                analysisSwitchView.setClickable(true);
                merterialView.setClickable(true);
                isBlue = false;
                mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);

            } else if (id == R.id.interview_recordsounding_rl_confirm) {   //点击确认功能

                if (isBlue == true && timeRecording > 60) {
                    mActivity.viewPager.setScroll(false);    // 未提交页面也不可以滑动
                    isStop = false;
                    mActivity.setCanBack(2);                // 返回键设置不可返回
                    mediaRecorderManager.stop();
                    mRecordingView.setVisibility(View.GONE);
                    mUnsubmitView.setVisibility(View.VISIBLE);
                    analysisSwitchView.setClickable(false);
                    merterialView.setClickable(false);
                    if(timeRecording >= 360){
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(360));
                    }else{
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(timeRecording));
                    }
                    stopRecord();
                } else {
                    ToastManager.showToast(mActivity, "录音时间要超过60秒");
                }

            } else if (id == R.id.interview_recordsounding_ll) {             // 点击录音整体
                ToastManager.showToast(getActivity(), "正在录音,录音时间要超过60秒");

            } else if (id == R.id.interview_recordsound_rl_rerecording) {      //点击重录

                if (mediaRecorderManager != null) {
                    mediaRecorderManager.stop();
                    status = RECORDABLE;
                    mTvtimeRecording.setText("0" + "\"");
                    mTvtimeNotSubm.setText("听一下");
                    isBlue = false;
                    mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
                    handler.sendEmptyMessage(TIME_CANCEL);
                    prepareRecord();
                    mUnsubmitView.setVisibility(View.GONE);
                    mRecordingView.setVisibility(View.VISIBLE);
                    mActivity.setCanBack(1);
                    analysisSwitchView.setClickable(false);         // 录音过程中不可点击
                    merterialView.setClickable(false);
                    mActivity.viewPager.setScroll(false);

                }
            } else if (id == R.id.interview_recordsounding_ll_play) {       //点击未提交播放按钮
                if(isStop){
                    isStop = false;
                    mediaRecorderManager.stop();
                    handler.sendEmptyMessage(TIME_CANCEL);
                    mTvtimeNotSubm.setText("听一下");

                    if(timeRecording >= 360){
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(360));
                    }else{
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(timeRecording));
                    }

                }else{
                    isStop = true;
                    status = SUBMIT;               // 变成可提交状态
                    play(userAnswerFilePath,status);
                }

            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮

                mediaRecorderManager.stop();  // 关闭播放器和录音器
                mUnPurchasedModel.showSubmitAnswerAlert(mActivity, userAnswerFilePath, mQuestionbean,FileManager.getVideoDuration(userAnswerFilePath),questionType);

            } else if (id == R.id.interview_hadanswer_listen_ll) {           // 已提交播放按钮

                status = HADSUBMIT;
                if(mActivity.recorderManager != null){
                }

                if(isStop){
                    isStop = false;
                    mActivity.recorderManager.stop();
                    handler.sendEmptyMessage(TIME_CANCEL);
                    mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
                }else{
                    isStop = true;
                    dealAnswer();   // 处理自己提交的录音
                }
            }
        }
    };
    /*
    *   处理已提交录音的播放数据
    * */
    public void dealAnswer(){

        final String filePath = fileFolder + question_id + ".amr";
        final String zipFilePath = fileFolder + question_id + ".zip";
        final File file = new File(filePath);
        status = HADSUBMIT;
        if (file.exists()) {
            ToastManager.showToast(mActivity,"从本地获取的数据语音");
            playHadsubmit(filePath,status);
        } else{
            String url = mQuestionbean.getUser_audio();
            if (url == null) return;

            if (url.contains(".amr")) {
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionbean.getUser_audio(), fileFolder, filePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean success) {
                        if(success){
                            playHadsubmit(filePath,status);
                        }
                    }
                });
            } else if (url.contains(".zip")){
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionbean.getUser_audio(), fileFolder, zipFilePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean success) {
                        if(success){
                            playHadsubmit(filePath,status);
                        }
                    }
                });
            }
        }
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 准备录音
     */
    public void prepareRecord() {

        mUnRecordView.setVisibility(View.GONE);
        mRecordingView.setVisibility(View.VISIBLE);
        analysisSwitchView.setClickable(false);         // 录音过程中不可点击
        merterialView.setClickable(false);
        mActivity.viewPager.setScroll(false);   // 让viewpager拦截
        mActivity.setCanBack(1);     // 是否可以按返回键

        mediaRecorderManager = new MediaRecorderManager(mActivity);  // 在刚开始录音后:进行创建mediarecordmanager

        // 录音逻辑:
        new MediaRecorderManager(mActivity, new MediaRecorderManager.CheckRecordStatusListener() {
            @Override
            public void onCheckRecordStatusFinished(boolean enableRecord) {
                if (enableRecord) {
                    // 开始录音
                    startRecord();
                }
            }
        }).checkRecordStatus();
    }

    /**
     * 开始录音
     **/
    public void startRecord() {

        mediaRecorderManager.mFileName = userAnswerFilePath;
        if (FileManager.isFile(userAnswerFilePath)) {
            FileManager.deleteFiles(userAnswerFilePath);
        }
        mediaRecorderManager.onRecord(true);
        startTimer();
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        handler.sendEmptyMessage(TIME_CANCEL);
        mediaRecorderManager.stop();
    }

    /**
     * 开始时间
     */
    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        timeRecording = 0;   //记录录音时间
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeRecording++;
                if (timeRecording >= 60 && timeRecording <= 360) {
                    handler.sendEmptyMessage(RECORD_SUBMIT);
                    isBlue = true;
                }
                handler.sendEmptyMessage(RECORD_TIME);
            }
        }, 0, 1000);
    }

    /**
     * 播放:已提交时的播放;提交后的播放
     **/
    public void playHadsubmit(String userAnswerFilePath, String statusState) {

        String status = statusState;
        mActivity.recorderManager.mPlayFileName = userAnswerFilePath;
        mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
            @Override
            public void playOver(boolean isPlay) {

            }
        });
        if(status.equals(SUBMIT)) {
            mTvtimeNotSubm.setText("停止播放");
        }else if(status.equals(HADSUBMIT)){
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
        }

        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        playTimer(status);
    }

    public void play(String userAnswerFilePath, String statusState) {

        String status = statusState;
        mediaRecorderManager.mPlayFileName = userAnswerFilePath;
        mediaRecorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
            @Override
            public void playOver(boolean isPlay) {

            }
        });
        if(status.equals(SUBMIT)) {
            mTvtimeNotSubm.setText("停止播放");
        }else if(status.equals(HADSUBMIT)){
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
        }
        playTimer(status);
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 播放的时间处理:递减
     **/
    private void playTimer(final String statusState) {
        final String status = statusState;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (status.equals(SUBMIT)){    // 未提交的状态:但是可提交
            timePlaying = timeRecording;
        }else if(status.equals(HADSUBMIT)){
            timePlaying = user_audio_durationTime;
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                    timePlaying--;
                    if(status.equals(SUBMIT)) {
                        handler.sendEmptyMessage(PLAYING);
                    }else if (status.equals(HADSUBMIT)){
                        handler.sendEmptyMessage(PLAYINGSUBMIT);
                    }
            }
        }, 0, 1000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
         handler.sendEmptyMessage(TIME_CANCEL);
    }

    /**
     * 动态添加富文本
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public  void addRichTextToContainer(final Activity activity,
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
                textView.setTextColor(activity.getResources().getColor(R.color.common_text));
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
}
