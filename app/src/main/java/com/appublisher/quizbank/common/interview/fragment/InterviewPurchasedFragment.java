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
import com.appublisher.quizbank.common.interview.model.InterviewPurchasedModel;
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
 */

public class InterviewPurchasedFragment extends InterviewDetailBaseFragment {

    private View mPurchasedView;
    private View merterialView;
    private View questionSwitchView;
    private LinearLayout questionListenLl;
    private ImageView questionIm;
    private TextView questionTv;
    private LinearLayout questionContent;
    private View analysisSwitchView;
    private LinearLayout analysisListenLl;
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
    private InterviewPaperDetailResp.QuestionsBean mQuestionsBean;
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
    private TextView mTvtimeHadSumbPlay;
    private ImageView mIvRecordSound;
    private String RECORDABLE = "recordable";      //可录音
    private String CONFIRMABLE = "confirmable";   //可确认
    private String SUBMIT = "submit";              //可提交
    private String HADSUBMIT = "hadSubmit";      // 已提交
    private String status;
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
    private String user_audioUrl;
    private boolean isBlue;    // 记录是否满足录音时间后,确认按钮是否变蓝
    private MediaRecorderManager mediaRecorderManager;
    private int user_audio_durationTime;
    private String questionType;
    private String questionFileFolder;
    private String analysisFileFolder;
    private LinearLayout analysisView;
    private InterviewPurchasedModel mPurchasedModel;


    public static InterviewPurchasedFragment newInstance(String questionbean, int position,int listLength,String questionType) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONBEAN, questionbean);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_LISTLENGTH, listLength);
        args.putString(QUESTIONTYPE, questionType);    // 问题的类型
        InterviewPurchasedFragment fragment = new InterviewPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (InterviewPaperDetailActivity) getActivity();
        status = RECORDABLE;
        mQuestionsBean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        // 问题的类型
        questionType = getArguments().getString(QUESTIONTYPE);

        mPurchasedModel = new InterviewPurchasedModel(mActivity);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mListLength = getArguments().getInt(ARGS_LISTLENGTH);

        user_audioUrl = mQuestionsBean.getUser_audio();    // 录音提交后返回到地址
        user_audio_durationTime = mQuestionsBean.getUser_audio_duration();   // 提交录音返回的时长
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPurchasedView = inflater.inflate(R.layout.interview_question_item_recordsound_hadpayfor, container, false);
        mediaRecorderManager = new MediaRecorderManager(mActivity);

        initView();
        checkedIsAnswer();     // 需要一个返回到提交字段
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

        return mPurchasedView;
    }
    private void checkedIsAnswer() {

        // 根据解析时长来显示有无答案和解析
        // 回答的问题直接展开
        if(user_audioUrl !=null &&  user_audioUrl.length() > 0){
            status = HADSUBMIT;
            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);
            mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));
        }else{
            // 如果未答题:显示未录音页面
            status = RECORDABLE;
            mUnRecordView.setVisibility(View.VISIBLE);
            analysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }
    private void initFile() {
        String userId = LoginModel.getUserId();

        fileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + userId + "/user_answer/";
        questionFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + userId + "/question_audio/";
        analysisFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + userId + "/analysis_audio/";

        question_id = mQuestionsBean.getId();            // 具体哪一个问题
        FileManager.mkDir(fileFolder);
        FileManager.mkDir(questionFileFolder);
        FileManager.mkDir(analysisFileFolder);

        // 录音存储的文件路径
        userAnswerFilePath = fileFolder + question_id + ".amr";
    }

    private void initView() {

        merterialView = mPurchasedView.findViewById(R.id.meterial_rl);
        questionSwitchView = mPurchasedView.findViewById(R.id.analysis_quesition_rl);
        questionListenLl = (LinearLayout) mPurchasedView.findViewById(R.id.interview_hadquestion_listen_ll);

        questionIm = (ImageView) mPurchasedView.findViewById(R.id.interview_lookquestion_im);
        questionTv = (TextView) mPurchasedView.findViewById(R.id.interview_lookquestion_tv);
        questionContent = (LinearLayout) mPurchasedView.findViewById(R.id.question_content);
        analysisSwitchView = mPurchasedView.findViewById(R.id.analysis_switch_rl);
        analysisListenLl = (LinearLayout) mPurchasedView.findViewById(R.id.interview_answer_listen_ll);

        analysisIm = (ImageView) mPurchasedView.findViewById(R.id.analysis_im);
        reminderTv = (TextView) mPurchasedView.findViewById(R.id.open_analysis);
        //解析答案的容器
        analysisView = (LinearLayout) mPurchasedView.findViewById(R.id.analysis_ll);
        analysisTv = (TextView) mPurchasedView.findViewById(R.id.analysis_tv);
        noteTv = (TextView) mPurchasedView.findViewById(R.id.note_tv);
        sourceTv = (TextView) mPurchasedView.findViewById(R.id.source_tv);
        keywordsTv = (TextView) mPurchasedView.findViewById(R.id.keywords_tv);

        if(mQuestionsBean.getQuestion_audio() ==null || mQuestionsBean.getQuestion_audio().length() ==0){
            questionListenLl.setVisibility(View.GONE);
        }else{
            questionListenLl.setVisibility(View.VISIBLE);
        }
        if(mQuestionsBean.getAnalysis_audio() ==null || mQuestionsBean.getAnalysis_audio().length() ==0){
            analysisListenLl.setVisibility(View.GONE);
        }else{
            analysisListenLl.setVisibility(View.VISIBLE);
        }
        initRecordSoundView();
    }

    private void initRecordSoundView() {

        mUnRecordView = mPurchasedView.findViewById(R.id.interview_popup_unrecordsound);
        mRecordingView = mPurchasedView.findViewById(R.id.interview_popup_recordsounding);
        mUnsubmitView = mPurchasedView.findViewById(R.id.interview_popup_recordsounding_unsubmit);
        mRecordedView = mPurchasedView.findViewById(R.id.interview_popup_recordsounded);

        // 初始化各自的控件
        mUnrecordsound_ll = (LinearLayout) mPurchasedView.findViewById(R.id.interview_unrecordsound_ll);

        mRecordsounding_cancle = (RelativeLayout) mPurchasedView.findViewById(R.id.interview_recordsounding_cancle);
        mRecordsounding_confirm = (RelativeLayout) mPurchasedView.findViewById(R.id.interview_recordsounding_rl_confirm);
        mRecordsoundingll = (LinearLayout) mPurchasedView.findViewById(R.id.interview_recordsounding_ll);
        mTvtimeRecording = (TextView) mPurchasedView.findViewById(R.id.tv_record_sounding_time);
        mIvRecordSound = (ImageView) mPurchasedView.findViewById(R.id.imagview_confirm);

        mRecordNotSubmit_rl = (RelativeLayout) mPurchasedView.findViewById(R.id.interview_recordsound_rl_rerecording);
        mRecordNotSubmit_ll_play = (LinearLayout) mPurchasedView.findViewById(R.id.interview_recordsounding_ll_play);
        mRecordNotSubmit_rl_submit = (RelativeLayout) mPurchasedView.findViewById(R.id.interview_recordsounding_rl_submit);

        mTvtimeNotSubm = (TextView) mPurchasedView.findViewById(R.id.tv_record_play);
        mTvtimeNotSubmPlay = (TextView) mPurchasedView.findViewById(R.id.tv_record_sounding_play_time);


        mAnswer_listen_ll = (LinearLayout) mPurchasedView.findViewById(R.id.interview_hadanswer_listen_ll);
        mTvtimeHadSumbPlay = (TextView) mPurchasedView.findViewById(R.id.tv_recorded_sound_play_time);

    }
    private void initListener() {

        // 监听录音控件
        mUnrecordsound_ll.setOnClickListener(OnClickListener);
        mRecordsounding_cancle.setOnClickListener(OnClickListener);
        mRecordsounding_confirm.setOnClickListener(OnClickListener);
        mRecordsoundingll.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl.setOnClickListener(OnClickListener);
        mRecordNotSubmit_ll_play.setOnClickListener(OnClickListener);
        mRecordNotSubmit_rl_submit.setOnClickListener(OnClickListener);
        mAnswer_listen_ll.setOnClickListener(OnClickListener);
        questionListenLl.setOnClickListener(OnClickListener);
        analysisListenLl.setOnClickListener(OnClickListener);

        if (mQuestionsBean != null && mPosition < mListLength && mListLength >0 ) {

            //材料
            if (mQuestionsBean.getMaterial() != null && !"".equals(mQuestionsBean.getMaterial())) {
                merterialView.setVisibility(View.VISIBLE);
                merterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(mActivity, InterviewMaterialDetailActivity.class);
                        intent.putExtra("material", mQuestionsBean.getMaterial());
                        mActivity.startActivity(intent);

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Material");
                        UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                    }
                });
            } else {
                merterialView.setVisibility(View.GONE);
            }

            questionContent.setVisibility(View.GONE);  // 题目的展示容器默认不显示
            analysisView.setVisibility(View.GONE);  // 解析答案的容器默认不显示

            /**
             *  本类为已付费页面的类,不用再和服务器交互:题目行的逻辑处理:逻辑:点击事件:展开与折叠;听语音播放但不可暂停
             * **/
            questionSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (questionContent.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                        questionContent.setVisibility(View.GONE);
                        questionIm.setImageResource(R.drawable.interview_answer_lookover);
                        questionTv.setText("看文字");

                    } else {
                        questionContent.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                        questionIm.setImageResource(R.drawable.interview_answer_packup);
                        questionTv.setText("不看文字");

                    }

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "ReadQ");
                    if (isDone()) {
                        UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                    } else {
                        UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                    }
                }
            });

            /**
             *  本类为已付费页面的类,不用再和服务器交互: 解析行的逻辑处理: 逻辑:点击事件:展开与折叠;听语音播放但不可暂停
             * **/
            analysisSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (analysisView.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                        analysisView.setVisibility(View.GONE);
                        analysisIm.setImageResource(R.drawable.interview_answer_lookover);
                        if ("notice".equals(mQuestionsBean.getStatus())) {
                            reminderTv.setText("看文字");
                        } else {
                            reminderTv.setText("看文字");
                        }
                    } else {
                        analysisView.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                        analysisIm.setImageResource(R.drawable.interview_answer_packup);
                        if ("notice".equals(mQuestionsBean.getStatus())) {
                            reminderTv.setText("不看文字");
                        } else {
                            reminderTv.setText("不看文字");
                        }
                    }

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "ReadA");
                    if (isDone()) {
                        UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                    } else {
                        UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                    }
                }
            });

            // 题目行中文字的处理
            String rich = (mPosition + 1) + "/" + mListLength + "  " + mQuestionsBean.getQuestion();
            addRichTextToContainer((Activity) mActivity, questionContent, rich, true);

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.themecolor));
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 15));
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

            //解析
            SpannableString analysis = new SpannableString("【解析】"+ mQuestionsBean.getAnalysis() );
            analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysisTv.setLineSpacing(0, 1.4f);
            analysisTv.setText(analysis);

            //知识点
            SpannableString note = new SpannableString("【知识点】" + mQuestionsBean.getNotes());
            note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteTv.setLineSpacing(0, 1.4f);
            noteTv.setText(note);

            //来源
            SpannableString source = new SpannableString("【来源】" + mQuestionsBean.getFrom());
            source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sourceTv.setLineSpacing(0, 1.4f);
            sourceTv.setText(source);

            //关键词
            SpannableString keywords = new SpannableString("【关键词】" + mQuestionsBean.getKeywords());
            keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywordsTv.setLineSpacing(0, 1.4f);
            keywordsTv.setText(keywords);
        }
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
                textView.setLineSpacing(0, 1.4f);
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

                // 需要判断录音器是否已经存在,如果存在销毁,停止
                if(mActivity.recorderManager != null){
                    mActivity.recorderManager.stop();
                    //先准备录音
                    prepareRecord();
                }else{
                    if(status == RECORDABLE) {
                        //先准备录音
                        prepareRecord();
                    }
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Record");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_cancle) {   // 点击取消功能

                mActivity.viewPager.setScroll(true);
                mActivity.setCanBack(0);
                status = RECORDABLE;
                mTvtimeRecording.setText("0" + "\"");
                stopRecord();
                mRecordingView.setVisibility(View.GONE);
                mUnRecordView.setVisibility(View.VISIBLE);
                isBlue = false;
                mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
                // 禁止题目和解析语音播放
                merterialView.setClickable(true);
                questionSwitchView.setClickable(true);
                questionListenLl.setClickable(true);
                analysisSwitchView.setClickable(true);
                analysisListenLl.setClickable(true);
                analysisSwitchView.setClickable(true);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_confirm) {   //点击确认功能

                if (isBlue == true && timeRecording > 60) {
                    mActivity.viewPager.setScroll(false);
                    isStop = false;
                    mActivity.setCanBack(2);
                    mediaRecorderManager.stop();
                    mRecordingView.setVisibility(View.GONE);
                    mUnsubmitView.setVisibility(View.VISIBLE);
                    // 禁止题目和解析语音播放
                    merterialView.setClickable(false);
                    questionSwitchView.setClickable(false);
                    questionListenLl.setClickable(false);
                    analysisSwitchView.setClickable(false);
                    analysisListenLl.setClickable(false);
                    analysisSwitchView.setClickable(false);
                    if(timeRecording >= 360){
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(360));
                    }else{
                        mTvtimeNotSubmPlay.setText(TimeUtils.formatDateTime(timeRecording));
                    }
                    stopRecord();
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
                    mActivity.setCanBack(1);     // 是否可以按返回键
                    mActivity.viewPager.setScroll(false);   // 让viewpager拦截
                    // 禁止题目和解析语音播放
                    merterialView.setClickable(false);
                    questionSwitchView.setClickable(false);
                    questionListenLl.setClickable(false);
                    analysisSwitchView.setClickable(false);
                    analysisListenLl.setClickable(false);
                    analysisSwitchView.setClickable(false);

                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Remake");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

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

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Playaudio");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮
                mediaRecorderManager.stop();  // 关闭播放器和录音器
                mPurchasedModel.showSubmitAnswerAlert(mActivity, userAnswerFilePath, mQuestionsBean, FileManager.getVideoDuration(userAnswerFilePath),questionType);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Submit");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_hadanswer_listen_ll) {           // 已提交播放按钮

                // 需要从网络获取
                status = HADSUBMIT;
                if(isStop){
                    isStop = false;
                    mActivity.recorderManager.stop();
                    handler.sendEmptyMessage(TIME_CANCEL);
                    mTvtimeHadSumbPlay.setText(TimeUtils.formatDateTime(user_audio_durationTime));

                }else{
                    isStop = true;
                    mediaRecorderManager.stop();
                    dealAnswer();    // 处理自己提交的录音
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Answer");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            }else if(id ==R.id.interview_hadquestion_listen_ll){
                dealQuestionVedio();     // 处理题目行语音数据

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "ListenQ");
                if (isDone()) {
                    UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                }

            }else if(id == R.id.interview_answer_listen_ll ){
                dealAnalysisVedio();    // 处理解析行语音数据

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "ListenA");
                if (isDone()) {
                    UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                }
            }
        }
    };

    private boolean isDone() {
        return mQuestionsBean != null && mQuestionsBean.getUser_audio().length() > 0;
    }

    /*
    *   处理已提交录音的播放数据
    * */
    public void dealAnswer(){
        final String filePath = fileFolder + question_id + ".amr";
        final String zipFilePath = fileFolder + question_id + ".zip";

        final File file = new File(filePath);
        status = HADSUBMIT;
        if(file.exists()){
            playHadsubmit(filePath,status);

        }else{
            String url = mQuestionsBean.getUser_audio();
            if (url == null) return;

            if (url.contains(".amr")) {
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getUser_audio(), fileFolder, filePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean success) {
                        if(success){
                            playHadsubmit(filePath,status);
                        }
                    }
                });
            } else if (url.contains(".zip")){
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getUser_audio(), fileFolder, zipFilePath, new ICommonCallback() {
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

    // 处理下载的问题行的语音
    public void dealQuestionVedio(){

        final String filePath = questionFileFolder + question_id + ".amr";
        String zipFilePath = questionFileFolder + question_id + ".zip";
        File file = new File(filePath);
        if (file.exists()) {
            mActivity.recorderManager.mPlayFileName = filePath;
            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                @Override
                public void playOver(boolean isPlay) {
                }
            });
        } else {
            String url = mQuestionsBean.getQuestion_audio();
            if(url == null) return;
            if (url.contains(".amr")) {
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getQuestion_audio(), questionFileFolder, filePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean success) {
                        if(success){
                            mActivity.recorderManager.mPlayFileName = filePath;
                            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                                @Override
                                public void playOver(boolean isPlay) {
                                }
                            });
                        }
                    }
                });
            } else if(url.contains(".zip")){
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getQuestion_audio(), questionFileFolder, zipFilePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean isSuccess) {
                        if (isSuccess) {
                            mActivity.recorderManager.mPlayFileName = filePath;
                            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                                @Override
                                public void playOver(boolean isPlay) {
                                }
                            });
                        }
                    }
                });
            }

        }
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    // 处理下载的解析行的语音
    public void dealAnalysisVedio() {

        final String filePath = analysisFileFolder + question_id + ".amr";
        final String zipFilePath = analysisFileFolder + question_id + ".zip";
        File file = new File(filePath);
        if (file.exists()) {
            mActivity.recorderManager.mPlayFileName = filePath;
            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                @Override
                public void playOver(boolean isPlay) {

                }
            });
        } else {
            String url = mQuestionsBean.getAnalysis_audio();
            if(url == null) return;
            if (url.contains(".amr")) {
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getAnalysis_audio(), analysisFileFolder, filePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean success) {
                        if(success){
                            mActivity.recorderManager.mPlayFileName = filePath;
                            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                                @Override
                                public void playOver(boolean isPlay) {
                                }
                            });
                        }
                    }
                });
            } else if(url.contains(".zip")){
                InterviewModel.downloadVoiceVideo(mActivity, mQuestionsBean.getAnalysis_audio(), analysisFileFolder, zipFilePath, new ICommonCallback() {
                    @Override
                    public void callback(boolean isSuccess) {
                        if (isSuccess) {
                            mActivity.recorderManager.mPlayFileName = filePath;
                            mActivity.recorderManager.onPlay(true, new MediaRecorderManager.PlayOverMethod() {
                                @Override
                                public void playOver(boolean isPlay) {
                                }
                            });
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
        mActivity.viewPager.setScroll(false);
        mActivity.setCanBack(1);     // 是否可以按返回键
        // 禁止题目和解析语音播放
        merterialView.setClickable(false);
        questionSwitchView.setClickable(false);
        questionListenLl.setClickable(false);
        analysisSwitchView.setClickable(false);
        analysisListenLl.setClickable(false);
        analysisSwitchView.setClickable(false);

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
     * 播放:未提交时的播放;提交后的播放
     **/
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

}