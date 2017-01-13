package com.appublisher.quizbank.common.interview.fragment;

import android.annotation.SuppressLint;
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
import android.support.v4.content.ContextCompat;
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
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;
import com.appublisher.quizbank.common.interview.network.InterviewModel;
import com.appublisher.quizbank.common.interview.view.IIterviewDetailBaseFragmentView;
import com.appublisher.quizbank.common.utils.MediaRecorderManager;
import com.appublisher.quizbank.common.utils.TimeUtils;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huaxiao on 2016/12/16.
 * // 在基类fragment中获取录音界面的四个布局,然后创建各自的model对象,在各自的model中处理点击事件
 */

public abstract class  InterviewDetailBaseFragment extends Fragment implements IIterviewDetailBaseFragmentView {

    public String RECORDABLE = "recordable";                    //可录音
    public static final String CONFIRMABLE = "confirmable";   //可确认
    public static final String SUBMIT = "submit";              //可提交
    public static final String HADSUBMIT = "hadSubmit";      // 已提交
    private static final int TIME_CANCEL = 1;
    private static final int RECORD_TIME = 2;
    private static final int RECORD_SUBMIT = 3;
    private static final int PLAYING = 4;                   // 正在播放
    private static final int PLAYINGSUBMIT = 5;           // 已提交后的正在播放
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
    public RelativeLayout mRecordNotSubmit_rl;
    public LinearLayout mRecordNotSubmit_ll_play;
    public RelativeLayout mRecordNotSubmit_rl_submit;
    public TextView mTvtimeNotSubm;
    public TextView mTvtimeNotSubmPlay;
    public LinearLayout mAnswer_listen_ll;
    public TextView mTvtimeHadSumbPlay;
    public boolean isStop;           //是否停止播放
    public String mStatus;
    public View mFragmentView;
    private Timer mTimer;
    public int mTimeRecording;
    private boolean isCanSubmit;
    private int mTimePlaying;
    private String mRecordFolder;
    private String mUserAnswerFilePath;
    private InterviewPaperDetailResp.QuestionsBean mQuestionBean;
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
    private InterviewHandler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (InterviewPaperDetailActivity) getActivity();
        mModel = new InterviewDetailModel(mActivity);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        isStop = false;
        mActivity.setCanBack(0);            // 默认设置返回键可以点击
        mStatus = RECORDABLE;

        mFragmentView = inflater.inflate(setLayoutResouceId(), container, false); // 生成布局
        mQuestionBean = initChildData();
        mQuestionType = initChildQuestionType();

        mHandler = new InterviewHandler(this);

        initRecordView();             // 初始化录音页面控件
        checkIsAnswer();
        initChildView();
        initRecordView();             // 初始化录音页面控件
        initRerodFile();             // 初始化录音文件
        initChildListener();
        initRecordListener();
        showQuestion();
        showAnswer();
        return mFragmentView;
    }

    private void checkIsAnswer() {
        if(mQuestionBean.getUser_audio() !=null &&  mQuestionBean.getUser_audio().length() > 0){
            changeRecordView(5);
            if (mQuestionBean.getUser_audio_duration() >= 360){
                mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(360));
            }else{
                mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
            }
            mStatus = HADSUBMIT;

        }else{
            mStatus = RECORDABLE;
            mUnRecordView.setVisibility(View.VISIBLE);
            mAnalysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }

    private void initRerodFile() {
        String userId = LoginModel.getUserId();
        mRecordFolder = FileManager.getRootFilePath(mActivity) + "/interview/"  + userId + "/user_answer/";            // 自己录音的路径
        FileManager.mkDir(mRecordFolder);

        // 录音存储的文件路径
        mUserAnswerFilePath = mRecordFolder + mQuestionBean.getId() + ".amr";
    }

    public static class InterviewHandler extends Handler{

        private final WeakReference<InterviewDetailBaseFragment> mFragment;   // 将fragment添加到弱引用中
        InterviewHandler(InterviewDetailBaseFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }
        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {

            InterviewDetailBaseFragment fragment = mFragment.get(); // 需要的时候获取

            if (fragment != null) {
                switch (msg.what) {
                    case RECORD_SUBMIT:
                        if (fragment.mTimeRecording >= 5 && fragment.mTimeRecording <= 360) {
                            fragment.mStatus = CONFIRMABLE;
                            fragment.mIvRecordSound.setImageResource(R.drawable.interview_confrim_blue);
                        }
                    case RECORD_TIME:
                        if (fragment.mTimeRecording >= 0 && fragment.mTimeRecording <= 360) {
                            if(fragment.mTimeRecording == 330){
                                fragment.showReminderToast();
                            }
                            fragment.mTvtimeRecording.setText(TimeUtils.formatDateTime(fragment.mTimeRecording));
                        }else if(fragment.mTimeRecording > 360){
                            fragment.mHandler.sendEmptyMessage(TIME_CANCEL);
                            fragment.mActivity.mMediaRecorderManager.stop();
                        }
                        break;
                    case PLAYING:
                        if (fragment.mTimePlaying >= 0) {
                            fragment.mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(fragment.mTimePlaying));
                        } else {
                            fragment.mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(fragment.mTimeRecording));
                            fragment.mHandler.sendEmptyMessage(TIME_CANCEL);
                            fragment.isStop = false;
                            fragment.mTvtimeNotSubm.setText("听一下");
                        }
                        break;
                    case PLAYINGSUBMIT:
                        if (fragment.mTimePlaying >= 0) {
                            fragment.mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(fragment.mTimePlaying));
                        }else{
                            fragment.changeTime();
                            fragment.isStop = false;
                            fragment.mStatus = HADSUBMIT;
                            fragment.mHandler.sendEmptyMessage(TIME_CANCEL);
                        }
                        break;
                    case TIME_CANCEL:
                        if (fragment.mTimer != null) {
                            fragment.mTimer.cancel();
                            fragment.mTimer = null;
                        }
                    break;
                }
            }
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

        mRecordsounding_cancle = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_cancle);
        mRecordsounding_confirm = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_rl_confirm);
        mRecordsoundingll = (LinearLayout) mFragmentView.findViewById(R.id.interview_recordsounding_ll);
        mTvtimeRecording = (TextView) mFragmentView.findViewById(R.id.tv_record_sounding_time);
        mIvRecordSound = (ImageView) mFragmentView.findViewById(R.id.imagview_confirm);

        mRecordNotSubmit_rl = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsound_rl_rerecording);
        mRecordNotSubmit_ll_play = (LinearLayout) mFragmentView.findViewById(R.id.interview_recordsounding_ll_play);
        mRecordNotSubmit_rl_submit = (RelativeLayout) mFragmentView.findViewById(R.id.interview_recordsounding_rl_submit);
        mTvtimeNotSubm = (TextView) mFragmentView.findViewById(R.id.tv_record_play);
        mTvtimeNotSubmPlay = (TextView) mFragmentView.findViewById(R.id.tv_record_sounding_play_time);

        mAnswer_listen_ll = (LinearLayout) mFragmentView.findViewById(R.id.interview_hadanswer_listen_ll);
        mTvtimeHadSumbPlay = (TextView) mFragmentView.findViewById(R.id.tv_recorded_sound_play_time);


        if (mQuestionBean != null ) {
            //材料
            if (mQuestionBean.getMaterial() != null && !"".equals(mQuestionBean.getMaterial())) {
                mMerterialView.setVisibility(View.VISIBLE);
                mMerterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
    public void initRecordListener(){   // 录音控件的监听事件

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

    public void showReminderToast() {
        ToastManager.showToast(mActivity,"还有30秒");
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
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.themecolor));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        //解析
        SpannableString analysis =   new SpannableString("【解析】" + mQuestionBean.getAnalysis());
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
                textView.setTextColor(ContextCompat.getColor(mActivity,R.color.common_text));
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
     *   底部录音页面中各个控件的点击事件
     * */
    public View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.interview_unrecordsound_ll) {    //如果点击了录音功能

                if(mStatus.equals(RECORDABLE)){
                    // 需要判断录音器是否已经存在,如果存在销毁,停止
                    if(mActivity.mMediaRecorderManager != null){
                        stopRecord();
                    }
                    isCanTouch(false);
                    mActivity.setCanBack(1);                  // 不可以按返回键
                    changeRecordView(2);
                    prepareRecord(); // 先准备录音

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Record");
                    UmengManager.onEvent(mActivity, "InterviewRecord", map);
                }

            } else if (id == R.id.interview_recordsounding_cancle) {   // 点击取消功能

                stopRecord();
                isCanTouch(true);
                mActivity.setCanBack(0);                    // 不可以按返回键
                mStatus = RECORDABLE;
                String zero = "0\"";
                mTvtimeRecording.setText(zero);
                changeRecordView(1);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_confirm) {   //点击确认功能

                if (isCanSubmit && mTimeRecording > 5) {
                    stopRecord();
                    isCanTouch(false);
                    mActivity.setCanBack(2);                // 返回键设置不可返回,点击有弹窗
                    isStop = false;
                    changeRecordView(3);

                    if(mTimeRecording >= 360){
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(360));
                    }else{
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(mTimeRecording));
                    }
                } else {
                    ToastManager.showToast(mActivity, "录音时间要超过60秒");
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Conform");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_ll) {             // 点击录音整体
                ToastManager.showToast(getActivity(), "正在录音,录音时间要超过60秒");

            } else if (id == R.id.interview_recordsound_rl_rerecording) {      //点击重录

                if (mActivity.mMediaRecorderManager != null) {
                    stopRecord();
                    mStatus = RECORDABLE;
                    String zero = "0\"";
                    mTvtimeRecording.setText(zero);
                    mTvtimeNotSubm.setText("听一下");
                    isCanSubmit = false;
                    mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
                    isCanTouch(false);
                    mActivity.setCanBack(1);
                    changeRecordView(4);
                    prepareRecord();
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Remake");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_ll_play) {       //点击未提交播放按钮
                if(isStop){
                    isStop = false;
                    stopRecord();
                    mTvtimeNotSubm.setText("听一下");

                    if(mTimeRecording >= 360){
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(360));
                    }else{
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(mTimeRecording));
                    }
                }else{
                    isStop = true;
                    mStatus = SUBMIT;               // 变成可提交状态
                    mTvtimeNotSubm.setText("停止播放");
                    play(mUserAnswerFilePath);
                    playTimer(mStatus);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Playaudio");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮
                Logger.e("点击提交按钮");
                stopRecord();
                mModel.showSubmitAnswerProgressBar(mUserAnswerFilePath, mQuestionBean, FileManager.getVideoDuration(mUserAnswerFilePath), mQuestionType);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Submit");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_hadanswer_listen_ll) {           // 已提交播放按钮

                if(isStop){
                    isStop = false;
                    stopRecord();
                    changeTime();

                }else{
                    mStatus = HADSUBMIT;
                    isStop = true;
                    dealDownLoadAudio(mRecordFolder, mQuestionBean.getUser_audio());
                    changeTime();
                    playTimer(mStatus);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Answer");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            }
        }
    };

    private void changeTime() {
        if (mQuestionBean.getUser_audio_duration() >= 360){
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(360));
        }else{
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
        }
    }
    private void changeRecordView(int i) {
        if(i == 1){
            mRecordingView.setVisibility(View.GONE);
            mUnRecordView.setVisibility(View.VISIBLE);
            isCanSubmit = false;
            mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);

        }else if (i == 2){
            mUnRecordView.setVisibility(View.GONE);
            mRecordingView.setVisibility(View.VISIBLE);
        }else if (i == 3){
            mRecordingView.setVisibility(View.GONE);
            mUnsubmitView.setVisibility(View.VISIBLE);
        }else if (i == 4){
            mUnsubmitView.setVisibility(View.GONE);
            mRecordingView.setVisibility(View.VISIBLE);
        }else{
            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);
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
                  startRecord();         // 开始录音
                }
            }
        }).checkRecordStatus();
    }

    private void isCanTouch(boolean isCanTouch){
        if(isCanTouch){
            mAnalysisSwitchView.setClickable(true);         // 录音过程中可点击
            mMerterialView.setClickable(true);
            mActivity.mViewPager.setScroll(true);    // 让viewPager不拦截
            releaseFragmentTouch();

        }else{
            mAnalysisSwitchView.setClickable(false);         // 录音过程中不可点击
            mMerterialView.setClickable(false);
            mActivity.mViewPager.setScroll(false);       // 让viewpager拦截
            banFragmentTouch();
        }
    }

    /**
     * 开始录音
     **/
    public void startRecord() {
        mActivity.mMediaRecorderManager.mFileName = mUserAnswerFilePath;
        if (FileManager.isFile(mUserAnswerFilePath)) {
            FileManager.deleteFiles(mUserAnswerFilePath);
        }
        mActivity.mMediaRecorderManager.onRecord(true);
        startTimer();
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    /**
     * 开始时间
     */
    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        //记录录音时间
        mTimeRecording = 0;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeRecording++;
                if (mTimeRecording >= 5 && mTimeRecording <= 360) {
                    isCanSubmit =  true;
                    mHandler.sendEmptyMessage(RECORD_SUBMIT);
                }
               mHandler.sendEmptyMessage(RECORD_TIME);
            }
        }, 0, 1000);
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mHandler.sendEmptyMessage(TIME_CANCEL);
        mActivity.mMediaRecorderManager.stop();
    }

    /*
  *   播放语音
  * */
    public void play(String userAnswerFilePath) {
        mActivity.mMediaRecorderManager.mPlayFileName = userAnswerFilePath;
        mActivity.mMediaRecorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
            @Override
            public void playOver(boolean isPlay) {

            }
        });
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    /**
     * 播放的时间处理:递减
     **/
    private void playTimer(final String status) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (status.equals(SUBMIT)){    // 未提交的状态:但是可提交
            mTimePlaying = mTimeRecording;
        }else if(status.equals(HADSUBMIT)){
            if (mQuestionBean.getUser_audio_duration() >= 360){
                mTimePlaying = 360;
            }else{
                mTimePlaying = mQuestionBean.getUser_audio_duration() + 1;
            }
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimePlaying--;
                if(status.equals(SUBMIT)) {
                    mHandler.sendEmptyMessage(PLAYING);
                }else if (status.equals(HADSUBMIT)){
                    mHandler.sendEmptyMessage(PLAYINGSUBMIT);
                }
            }
        }, 0, 1000);
    }
    /*
    *  处理下载的问题行的语音
    * */
    public void dealDownLoadAudio(String mFileFolder, String vedioUrl){   //点击事件中传递参数
        final String filePath = mFileFolder + mQuestionBean.getId() + ".amr";
        String zipFilePath = mFileFolder + mQuestionBean.getId() + ".zip";

        File file = new File(filePath);
        File zipFile = new File(zipFilePath);
        if (file.exists() ) {                                   // 如果文件存在直接播放
            play(filePath);
        } else if(zipFile.exists()){
            FileManager.unzipFiles(mFileFolder, zipFilePath); // 将参数二所对应的文件解压到参数一对应的文件中
            FileManager.deleteFiles(zipFilePath);
            play(filePath);
        } else {                               // 文件不存在进行下载
            if(vedioUrl == null) return;
            if (vedioUrl.contains(".amr")) {
                downLoadAudio(vedioUrl, mFileFolder, filePath, null);

            } else if(vedioUrl.contains(".zip")){
                downLoadAudio(vedioUrl, mFileFolder, filePath, zipFilePath);
            }
        }
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    /*
    *  下载语音
    * */
    private void downLoadAudio(String vedioUrl, String mFileFolder, final String filePath, String zipFilePath) {
        String localFilePath ;
        if(zipFilePath != null ){       // .zip 格式
            localFilePath = zipFilePath;
        }else{
            localFilePath = filePath;
        }
        InterviewModel.downloadVoiceVideo(mActivity, vedioUrl, mFileFolder, localFilePath, new ICommonCallback() {
            @Override
            public void callback(boolean success) {
                if(success){
                    play(filePath);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.sendEmptyMessage(TIME_CANCEL);
    }
}


