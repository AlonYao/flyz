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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.model.InterviewUnPurchasedModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;
import com.appublisher.quizbank.common.interview.network.InterviewModel;
import com.appublisher.quizbank.common.utils.MediaRecorderManager;
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
    private View analysisView;
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
    private InterviewPaperDetailResp.QuestionsBean mQuestionbean;
    private int mPosition;
    private int mListLength;
    private RelativeLayout mRecordParentView;
    public View mUnRecordView;
    private View mRecordingView;
    private View mUnsubmitView;
    private View mRecordedView;
    private LinearLayout mUnrecordsound_ll;
    private RelativeLayout mRecordNotSubmit_ll;
    private RelativeLayout mRecordsounding_cancle;
    private RelativeLayout mRecordsounding_confirm;
    private LinearLayout mRecordsoundingll;
    private TextView mTvtimeRecording;
    private LinearLayout mRecordNotSubmit_ll_play;
    private RelativeLayout mRecordNotSubmit_rl;
    private RelativeLayout mRecordNotSubmit_rl_submit;
    private TextView mTvtimeNotSubmPlay;
    private LinearLayout mAnswer_listen_ll;
    private TextView mTvtimeHadSumbPlay;
    private ImageView mIvRecordSound;
    private boolean isBlue;    // 记录是否满足录音时间后,确认按钮是否变蓝
    private boolean isAnswer;   // 常量是否已经提交
    private MediaRecorderManager mediaRecorderManager;
    private String fileFolder;
    private int question_id;
    private String userAnswerFilePath;
    private Timer mTimer;
    private int timeRecording;
    private Handler handler;
    private final int TIME_CANCEL = 1;
    private final int RECORD_TIME = 2;
    private final int RECORD_SUBMIT = 3;
    private final int COMPLETEPLAY = 4;     // 完成播放
    private final int PLAYING = 5;           // 正在播放
    private final int PLAYINGSUBMIT = 6;           // 已提交后的正在播放
    private int timePlaying;
    private TextView mTvtimeNotSubm;
    private InterviewPaperDetailActivity mActivity;
    private ScrollView mScrollview;
    private RelativeLayout mBottomContainer;
    private int timePlayed;             // 记录录音播放时长
    private boolean isStop;           //是否停止播放
    private InterviewUnPurchasedModel mUnPurchasedModel;
    private int analysis_audio_duration;
    private String user_audioUrl;
    private String RECORDABLE = "recordable";      //可录音
    private String CONFIRMABLE = "confirmable";   //可确认
    private String SUBMIT = "submit";              //可提交
    private String HADSUBMIT = "hadSubmit";      // 已提交
    private String status;
    private int hadsbmitTime;


//    private enum recordStatus {   //枚举记录录音的状态
//
//        RECORDABLE,
//
//        CONFIRMABLE,
//
//        SUBMIT
//        //
//
//    }

    public static Fragment newInstance(String questionbean, int position, int listLength, InterviewPaperDetailActivity mctivity) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONBEAN, questionbean);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_LISTLENGTH, listLength);
        InterviewUnPurchasedFragment fragment = new InterviewUnPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (InterviewPaperDetailActivity) getActivity();

      //  status = recordStatus.RECORDABLE;
        status = RECORDABLE;
        // InterviewPaperDetailResp.QuestionsBean 对象
        mQuestionbean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        mListLength = getArguments().getInt(ARGS_LISTLENGTH);

        mPosition = getArguments().getInt(ARGS_POSITION);          // 问题的索引

        user_audioUrl = mQuestionbean.getUser_audio();    // 录音提交后返回到地址

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
        //   status = recordStatus.RECORDABLE;

        initView();
        checkedIsAnswer();     // 需要一个返回到提交字段
        initFile();
        initListener();

        mediaRecorderManager = new MediaRecorderManager(mActivity);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RECORD_SUBMIT:
                        if (timeRecording >= 5) {
                       //     status = recordStatus.CONFIRMABLE;
                            status = CONFIRMABLE;
                            mIvRecordSound.setImageResource(R.drawable.interview_confrim_blue);
                        }
                    case RECORD_TIME:
                        mTvtimeRecording.setText(timeRecording + "\"");
                        break;
                    case PLAYING:
                        if (timePlaying > 0) {
                            mTvtimeNotSubmPlay.setText(timePlaying + "\"");
                        } else {
                            mTvtimeNotSubmPlay.setText("完成播放,点击重播");
                            mTvtimeNotSubm.setText("听一下");
                        }
                        break;
                    case PLAYINGSUBMIT:
                        if (timePlaying > 0) {
                            mTvtimeHadSumbPlay .setText(timePlaying + "\"");
                        }else{
                            mTvtimeHadSumbPlay .setText(hadsbmitTime);
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

        // 根据解析时长来显示有无答案和解析
        // 回答的问题直接展开
      //  analysis_audio_duration = 1;
        if(user_audioUrl !=null &&  user_audioUrl.length() > 0){
            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);

            // 修改toolbar
            if(mUnPurchasedModel ==null){
                new InterviewUnPurchasedModel(mActivity).changeToolbarMenu(mActivity,true);
            }
           mUnPurchasedModel.changeToolbarMenu(mActivity,true);

        }else{
             // 如果未答题:显示未录音页面
            mUnRecordView.setVisibility(View.VISIBLE);
            analysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }

    private void initFile() {
        fileFolder = FileManager.getRootFilePath(mActivity) + "/daily_interview/user/";

        question_id = mPosition;            // 具体哪一个问题

        FileManager.mkDir(fileFolder);
        // 录音存储的文件路径
        userAnswerFilePath = fileFolder + question_id + ".amr";
    }


    private void initView() {

        mScrollview = (ScrollView) mUnPurchasedView.findViewById(R.id.scrollView);   //scrollview整体
        mBottomContainer = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_popupwindow_recordsound_container);   //底部录音整体容器



        merterialView = mUnPurchasedView.findViewById(R.id.meterial_rl);       // 材料行:逻辑显示与否根据数据集合判断

        analysisSwitchView = mUnPurchasedView.findViewById(R.id.analysis_switch_rl);       // 解析行:逻辑:点击事件:展开与折叠 & 是否答题的逻辑

        questionContent = (LinearLayout) mUnPurchasedView.findViewById(R.id.question_content);      // 展示问题的容器
        analysisView = mUnPurchasedView.findViewById(R.id.analysis_ll);                 //解析答案的容器
        analysisSwitchTv = (TextView) mUnPurchasedView.findViewById(R.id.analysis_switch_tv);        //解析行的左面的文字
        analysisIm = (ImageView) mUnPurchasedView.findViewById(R.id.analysis_im);             // 解析行右面的ImageView:逻辑:展开:换图片 & 折叠换图片
        reminderTv = (TextView) mUnPurchasedView.findViewById(R.id.open_analysis);          // 解析行右面ImageView下面的文字
        analysisTv = (TextView) mUnPurchasedView.findViewById(R.id.analysis_tv);           // 答案中的标签:解析
        noteTv = (TextView) mUnPurchasedView.findViewById(R.id.note_tv);               // 答案中的标签:知识点
        sourceTv = (TextView) mUnPurchasedView.findViewById(R.id.source_tv);          // 答案中的标签:来源
        keywordsTv = (TextView) mUnPurchasedView.findViewById(R.id.keywords_tv);       //答案中的标签:关键词

        initRecordSoundView();  //初始化录音界面的布局和控件

    }

    /**
     * 录音界面布局额控件,并设置监听
     */
    private void initRecordSoundView() {
        //录音页面的父容器
        mRecordParentView = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_popupwindow_recordsound_container);
        mUnRecordView = mUnPurchasedView.findViewById(R.id.interview_popup_unrecordsound);       //未录音页面
        mRecordingView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounding);    // 正在录音页面
        mUnsubmitView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounding_unsubmit);   //已录音,未提交页面
        mRecordedView = mUnPurchasedView.findViewById(R.id.interview_popup_recordsounded);       //已提交页面

        // 初始化各自的控件
        mUnrecordsound_ll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_unrecordsound_ll);  //未录音中间图片文字整体

        mRecordsounding_cancle = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_cancle);   // 正在录音:取消整体
        mRecordsounding_confirm = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_rl_confirm);    // 正在录音:确认整体
        mRecordsoundingll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_ll);       // 正在录音:中间的整体
        mTvtimeRecording = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_sounding_time);     //正在录音监听时间
        mIvRecordSound = (ImageView) mUnPurchasedView.findViewById(R.id.imagview_confirm);              //正在录音:确认图片

        mRecordNotSubmit_rl = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsound_rl_rerecording);     // 未提交录音:重录整体
        mRecordNotSubmit_ll_play = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_ll_play);     // 未提交录音: 播放整体
        mRecordNotSubmit_rl_submit = (RelativeLayout) mUnPurchasedView.findViewById(R.id.interview_recordsounding_rl_submit);  //未提交录音:提交整体
        mTvtimeNotSubm = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_play);                              // 未提交录音:播放的文字
        mTvtimeNotSubmPlay = (TextView) mUnPurchasedView.findViewById(R.id.tv_record_sounding_play_time);       // 未提交录音:播放时间


        mAnswer_listen_ll = (LinearLayout) mUnPurchasedView.findViewById(R.id.interview_hadanswer_listen_ll);   // 已提交录音:播放整体
        mTvtimeHadSumbPlay = (TextView) mUnPurchasedView.findViewById(R.id.tv_recorded_sound_play_time);    // 已提交录音:播放时间

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

            if ("notice".equals(mQuestionbean.getStatus())) {   //判断解析行左面的文字是"提示"还是"解析"
                analysisSwitchTv.setText("展开提示");
            } else {
                analysisSwitchTv.setText("展开解析");
            }
            /**
             *  展开解析时需要监听是否已经答题: 用一个常量字符记录(在基类中处理录音页面的逻辑时)
             * **/
            analysisSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {     // 解析行的逻辑处理: 逻辑:点击事件:展开与折叠 & 是否答题的逻辑

                    if(user_audioUrl !=null &&  user_audioUrl.length() > 0){               // 已经答题
                        if (analysisView.getVisibility() == View.VISIBLE) {    // 展开-->折叠状态
                            analysisView.setVisibility(View.GONE);
                            analysisIm.setImageResource(R.drawable.interview_answer_lookover);
                            if ("notice".equals(mQuestionbean.getStatus())) {
                                analysisSwitchTv.setText("展开提示");
                                reminderTv.setText("查看");
                                analysisTv.setVisibility(View.GONE);
                            } else {
                                analysisSwitchTv.setText("展开解析");
                                reminderTv.setText("查看");
                                analysisTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //   if(mQuestionbean.isAnswer || mQuestionbean.isPurchased){    // 具体到每道题:是否答题或者是否购买
                            // 如果答完题状态
                            analysisView.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                            analysisIm.setImageResource(R.drawable.interview_answer_packup);
                            if ("notice".equals(mQuestionbean.getStatus())) {
                                analysisSwitchTv.setText("收起提示");
                                reminderTv.setText("收起");
                                analysisTv.setVisibility(View.GONE);
                            } else {
                                analysisSwitchTv.setText("收起解析");
                                reminderTv.setText("收起");
                                analysisTv.setVisibility(View.VISIBLE);
                            }
                        }
                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Answer");
                        UmengManager.onEvent(mActivity, "InterviewProblem", map);
                    }else{                      // 未答题
                        // 弹窗处理:三个item
                        InterviewUnPurchasedModel mUnPurchasedModel = new InterviewUnPurchasedModel(mActivity);
                        String payUrl = "从集合中获取到的支付链接";
                        mUnPurchasedModel.showNoAnswerDialog(mActivity,payUrl);
                    }

                }
            });

            //下面的是展示问题的文字的处理
            String rich = (mPosition + 1) + "/" + mListLength + "  " + mQuestionbean.getQuestion();
            addRichTextToContainer((Activity) mActivity, questionContent, rich, true);

            showAnswer(); // 展示答案解析

        }

        // 监听录音控件
        mUnrecordsound_ll.setOnClickListener(OnClickListener);   // 未录音中间图片整体
        mRecordsounding_cancle.setOnClickListener(OnClickListener);  // 录音中取消整体
        mRecordsounding_confirm.setOnClickListener(OnClickListener); //录音中确认整体
        mRecordsoundingll.setOnClickListener(OnClickListener);        //录音中播放整体
        mRecordNotSubmit_rl.setOnClickListener(OnClickListener);    // 已录音未提交:重录整体
        mRecordNotSubmit_ll_play.setOnClickListener(OnClickListener);    // 已录音未提交:播放整体
        mRecordNotSubmit_rl_submit.setOnClickListener(OnClickListener);    // 已录音未提交:提交整体
        mAnswer_listen_ll.setOnClickListener(OnClickListener);       // 已提交录音页面:播放整体
    }

    //展示答案
    private void showAnswer() {
        //下面是展示答案的文字的处理
        SpannableString analysis = new SpannableString("【解析】" + mQuestionbean.getAnalysis());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.themecolor));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        //解析
        analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysisTv.setText(analysis);

        //知识点
        SpannableString note = new SpannableString("【知识点】" + mQuestionbean.getNotes());
        note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        noteTv.setText(note);

        //来源
        SpannableString source = new SpannableString("【来源】" + mQuestionbean.getFrom());
        source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sourceTv.setText(source);

        //关键词
        SpannableString keywords = new SpannableString("【关键词】" + mQuestionbean.getKeywords());
        keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

                if (status == RECORDABLE) {    //状态记录是否可以录音
                    //先准备录音
                    prepareRecord();
                    mUnRecordView.setVisibility(View.GONE);
                    mRecordingView.setVisibility(View.VISIBLE);
                    analysisSwitchView.setClickable(false);         // 录音过程中不可点击
                    merterialView.setClickable(false);
                    mActivity.viewPager.setScroll(false);   // 让viewpager拦截
                    mActivity.setCanBack(1);     // 是否可以按返回键
                }

            } else if (id == R.id.interview_recordsounding_cancle) {   // 点击取消功能

                mActivity.viewPager.setScroll(true);    // 让viewPager不拦截
                mActivity.setCanBack(0);              // 可以按返回键
                status = RECORDABLE;
                mTvtimeRecording.setText("0" + "\"");
                stopRecord();
                mRecordingView.setVisibility(View.GONE);
                mUnRecordView.setVisibility(View.VISIBLE);
                analysisSwitchView.setClickable(true);         // 未录音过程中可点击
                merterialView.setClickable(true);         // 未录音过程中可点击
                isBlue = false;
                mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);


            } else if (id == R.id.interview_recordsounding_rl_confirm) {   //点击确认功能

                //   if(一个常量记录图片变成了蓝色){
                if (isBlue == true && timeRecording > 10) {
                    // 进入:已录音未提交页面

                    mActivity.viewPager.setScroll(false);    // 未提交页面也不可以滑动

                    mActivity.setCanBack(2);                // 返回键设置不可返回
                    mediaRecorderManager.stop();
                    mRecordingView.setVisibility(View.GONE);
                    mUnsubmitView.setVisibility(View.VISIBLE);       // 进入重录页面
                    analysisSwitchView.setClickable(false);         // 录音过程中不可点击
                    merterialView.setClickable(false);
                    mTvtimeNotSubmPlay.setText(timeRecording + "\"");
                    stopRecord();
                } else {
                    ToastManager.showToast(mActivity, "录音时间要超过60秒");
                }
            } else if (id == R.id.interview_recordsounding_ll) {             // 点击录音整体
                ToastManager.showToast(getActivity(), "正在录音,录音时间要超过60秒");
                // 逻辑:计时
            } else if (id == R.id.interview_recordsound_rl_rerecording) {      //点击重录
                mActivity.setCanBack(0);
                mActivity.viewPager.setScroll(true);
                mTvtimeRecording.setText("0" + "\"");
                resetRecord();

            } else if (id == R.id.interview_recordsounding_ll_play) {       //点击播放按钮
                if(isStop){
                    isStop = false;
                    mediaRecorderManager.stop();
                    handler.sendEmptyMessage(TIME_CANCEL);
                    mTvtimeNotSubm.setText("听一下");
                    mTvtimeNotSubmPlay.setText(timeRecording+"\"");

                }else{
                    isStop = true;
                    // 播放的逻辑及时间递减,播放完后:吐司提示
                    status = SUBMIT;               // 变成可提交状态
                    play(userAnswerFilePath);
                }

            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮
                // 提交的逻辑:向服务器发送数据并回调
                // toolbar改变
                // 显示dailog
                mediaRecorderManager.stop();  // 关闭播放器和录音器
                mUnPurchasedModel.showSubmitAnswerAlert(mActivity, userAnswerFilePath, mQuestionbean,FileManager.getVideoDuration(userAnswerFilePath));
              //  InterviewUnPurchasedModel.showSubmitAnswerAlert(mActivity, userAnswerFilePath, mQuestionbean);    // 录音地址, 录音的集合,回调接口

            } else if (id == R.id.interview_hadanswer_listen_ll) {           // 已提交页面:播放按钮
                // 需要从网络获取
                dealAnswer();

            }
        }
    };
    /*
    *   处理已提交录音的播放数据  dealAnswer(hotAnswerFolder, interviewM.getHottest_answer().getFile_url());
    * */
    public void dealAnswer(){

        final String filePath = fileFolder + question_id + ".amr";
        final File file = new File(filePath);
        String url = user_audioUrl;
        if (file.exists()) {
            ToastManager.showToast(mActivity,"从本地获取的数据语音");
            String videoDurationTime = FileManager.getVideoDuration(filePath);
            hadsbmitTime = Integer.parseInt(videoDurationTime);
           // playTimer(); // 播放时间:递减
            play(filePath);
        } else{
            InterviewModel.downloadVideo(mActivity,url,filePath, new ICommonCallback() {
                @Override
                public void callback(boolean success) {
                    if(success){
                       ToastManager.showToast(mActivity,"从网络下载了语音");
                        String videoDurationTime = FileManager.getVideoDuration(filePath);
//                        hadsbmitTime = Integer.parseInt(videoDurationTime);
                     //   playTimer(); // 播放时间:递减
                        play(filePath);
                    }
                }
            });
        }

    }

    /**
     * 准备录音
     */
    public void prepareRecord() {
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
        //
        if (FileManager.isFile(userAnswerFilePath)) {
            FileManager.deleteFiles(userAnswerFilePath);
        }
        mediaRecorderManager.onRecord(true);
        startTimer();

    }

    /**
     * 停止录音
     */

    public void stopRecord() {
        handler.sendEmptyMessage(TIME_CANCEL);
        mediaRecorderManager.stop();
    }

    /**
     * 重新录制
     */
    public void resetRecord() {

        //需要报上一次的清空,然后重新开启
        if (mediaRecorderManager != null) {
            mediaRecorderManager.stop();
            status = RECORDABLE;
            mTvtimeNotSubm.setText("听一下");
            isBlue = false;
            mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
            handler.sendEmptyMessage(TIME_CANCEL);
            // 已录音未提交页面跳转到未录音状态
            mUnsubmitView.setVisibility(View.GONE);
            mUnRecordView.setVisibility(View.VISIBLE);
            analysisSwitchView.setClickable(true);         // 未录音过程中可点击
            merterialView.setClickable(true);

        }
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
                if (timeRecording >= 10) {
                    handler.sendEmptyMessage(RECORD_SUBMIT);
                    isBlue = true;
                }
                handler.sendEmptyMessage(RECORD_TIME);
            }
        }, 0, 1000);
    }

    /**
     * 播放:未提交时的播放;提交后的播放
     **/
    public void play(String userAnswerFilePath) {

        mediaRecorderManager.mPlayFileName = userAnswerFilePath;
        mediaRecorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
            @Override
            public void playOver(boolean isPlay) {

            }
        });
        mTvtimeNotSubm.setText("停止播放");
            //  时间展示:递减
            playTimer();
    }

    /**
     * 播放的时间处理:递减
     **/
    private void playTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (status == SUBMIT) {    // 未提交的状态:但是可提交
            timePlaying = timeRecording;
        }else if(status == HADSUBMIT){
            timePlaying = hadsbmitTime;
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                    timePlaying--;
                    if(status == SUBMIT) {
                        handler.sendEmptyMessage(PLAYING);
                    }else if (status == HADSUBMIT){
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
    public static void addRichTextToContainer(final Activity activity,
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
