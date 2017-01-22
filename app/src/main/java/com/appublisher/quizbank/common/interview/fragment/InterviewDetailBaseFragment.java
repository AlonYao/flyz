package com.appublisher.quizbank.common.interview.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.view.Window;
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
import com.appublisher.lib_basic.customui.RoundProgressBarWidthNumber;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.model.InterviewDetailModel;
import com.appublisher.quizbank.common.interview.model.InterviewModel;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;
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
import java.util.Timer;

/**
 * Created by huaxiao on 2016/12/16.
 * // 在基类fragment中获取录音界面的四个布局,然后创建各自的model对象,在各自的model中处理点击事件
 */

public abstract class  InterviewDetailBaseFragment extends Fragment implements IIterviewDetailBaseFragmentView, InterviewDetailBaseFragmentCallBak {

    public String RECORDABLE = "recordable";                    //可录音
    public static final String CONFIRMABLE = "confirmable";   //可确认
    public static final String SUBMIT = "submit";              //可提交
    public static final String HADSUBMIT = "hadSubmit";      // 已提交
    private static final String UNHEAR = "unhear";           // 没有申请名师点评
    private static final String COMMENT = "comment";           // 等待点评中
    private static final String HEAR = "hear";                  // 已返回点评
    private static final int RECORD_TIME = 1;
    private static final int RECORD_SUBMIT = 2;
    private static final int PLAYING = 3;                   // 正在播放
    private static final int PLAYINGSUBMIT = 4;           // 已提交后的正在播放
    private static final int TIME_CANCEL = 0;
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
    public RelativeLayout mAnswer_listen_ll;
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
    private String mTemporaryFilePath;
    private RoundProgressBarWidthNumber mUserAnswerProgressBar;
    private RoundProgressBarWidthNumber mTeacherRemarkProgressBar;
    private RelativeLayout mTeacherRemarkRl;
    private TextView mRemarkNumb;
    private ImageView mQuestionHelpIv;
    private TextView mPurchasedLinkTv;
    private LinearLayout mNotRemarkLl;
    private TextView mUserAnswerTv;
    private TextView mTeacherRemarkPlayTime;
    private TextView mTeacherRemarkPlayState;
    private TextView mUserAnswerPlayState;
    private LinearLayout mExistRemarkLl;
    private TextView mWaitRemarkingTv;
    private String mRemarkState;
    private InterviewTeacherRemarkGuideFragment mInterviewTeacherRemarkGuideFragment;
    private int mOffset;
    private int mTeacherRemarkRemainderNum;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (InterviewPaperDetailActivity) getActivity();
//        mModel = new InterviewDetailModel(mActivity);
        mModel = new InterviewDetailModel(mActivity, this);

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

        mOffset = 0;
        initRecordView();             // 初始化录音页面控件
        checkIsAnswer();
        initChildView();
        initRecordView();             // 初始化录音页面控件
        initRecordFile();             // 初始化录音文件
        initChildListener();
        initRecordListener();
        showQuestion();
        showAnswer();
        return mFragmentView;
    }

    private void checkIsAnswer() {
        if(mQuestionBean.getUser_audio() !=null &&  mQuestionBean.getUser_audio().length() > 0){
            getTeacherRemarkRemainder();          // 获取名师点评剩余的次数
            changeRecordView(5);
            checkTeacherRemarkState();      // 检查点评的状态
            checkIsFirstSubmit();           // 检查是否第一次提交题
            if (mQuestionBean.getUser_audio_duration() >= 360){
                mTvtimeHadSumbPlay.setText(mModel.formatDateTime(360));
            }else{
                mTvtimeHadSumbPlay.setText(mModel.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
            }
            mStatus = HADSUBMIT;

        }else{
            mStatus = RECORDABLE;
            mUnRecordView.setVisibility(View.VISIBLE);
            mAnalysisView.setVisibility(View.GONE);       //如果未答题:解析行折叠
        }
    }
    // 获取名师点评剩余的次数
    private void getTeacherRemarkRemainder(){
        mModel.mRequest.getTeacherRemarkRemainder(2);           // 通过model来获取
    }

    // 回调接口,获取名师点评的剩余的次数
    @Override
    public void refreshTeacherRemarkRemainder(int num) {
        mTeacherRemarkRemainderNum = num;
//        mTeacherRemarkRemainderNum = 0;
        Logger.e("mTeacherRemarkRemainderNum==="+mTeacherRemarkRemainderNum);
        // 修改点评次数
        changeTeacherRemarkNum();
    }

    // 申请点评后,点评次数减一,刷新点评状态
    @Override
    public void refreshTeacherRemarkState() {
        --mTeacherRemarkRemainderNum;
        changeTeacherRemarkNum();
        //
        mNotRemarkLl.setVisibility(View.GONE);
        mWaitRemarkingTv.setVisibility(View.VISIBLE);
        mExistRemarkLl.setVisibility(View.GONE);
    }

    /*
            *   检查已经答题后:名师点评的状态
            * */
    private void checkTeacherRemarkState() {
//        mRemarkState = mQuestionBean.getListen_review();
        mRemarkState = UNHEAR;
        if ( mRemarkState.equals(UNHEAR) ){             // 没有申请名师点评
            mNotRemarkLl.setVisibility(View.VISIBLE);
            mWaitRemarkingTv.setVisibility(View.GONE);
            mExistRemarkLl.setVisibility(View.GONE);
        }else if( mRemarkState.equals(COMMENT)){        // 点评中
            mNotRemarkLl.setVisibility(View.GONE);
            mWaitRemarkingTv.setVisibility(View.VISIBLE);
            mExistRemarkLl.setVisibility(View.GONE);
        }else{                                          // 已回复点评
            mNotRemarkLl.setVisibility(View.GONE);
            mWaitRemarkingTv.setVisibility(View.GONE);
            mExistRemarkLl.setVisibility(View.VISIBLE);
        }
    }
    /*
    *   检查是否第一次提交题
    * */
    private void checkIsFirstSubmit(){

        SharedPreferences sp = mActivity.getSharedPreferences("interview_submit", Context.MODE_PRIVATE);
        boolean isFirstSubmit = sp.getBoolean("isFirstSubmit", true);
        if (isFirstSubmit){
            // 弹出引导浮层
            popupGuideFloating();
            SharedPreferences shp = mActivity.getSharedPreferences("interview_submit", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = shp.edit();
            edit.putBoolean("isFirstSubmit", false);
            edit.apply();
        }
    }
    /*
    *   弹出引导浮层
    * */
    private void popupGuideFloating(){
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

        lp.x = (int)(metrics.widthPixels * 0.04);
        lp.y = (int)(metrics.heightPixels * 0.2);
        lp.width = (int)(metrics.widthPixels * 0.75);
        lp.height = (int)(metrics.heightPixels * 0.2);
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
                skipToRemarkHelpFragment(); // 跳转到帮助页面
            }
        });

    }

    // 弹出申请名师点评弹窗
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
        applyForRemarkNumbTv.setText(String.valueOf(mTeacherRemarkRemainderNum));

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
                // TODO: 2017/1/22  mModel.申请点评
                mModel.mRequest.applyForTeacherRemark(mQuestionBean.getId(), "buy");
                mAalertDialog.dismiss();
            }
        });
    }

    /*
    *   申请后的提示弹窗: 在modle中处理后通过iview接口类进行弹窗
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
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        TextView confirm = (TextView) mWindow.findViewById(R.id.confirm);

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
    private void popupReminderPurchasedAlert(){
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

        lp.width = (int)(metrics.widthPixels * 0.8);
        lp.height = (int)(metrics.heightPixels * 0.35);

        mWindow.setAttributes(lp);


        TextView cancle = (TextView) mWindow.findViewById(R.id.purchased_remark_cancle);
        TextView confirm = (TextView) mWindow.findViewById(R.id.purchased_remark_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                // TODO: 2017/1/22 购买
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
            }
        });


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

        mAnswer_listen_ll = (RelativeLayout) mFragmentView.findViewById(R.id.interview_hadanswer_listen_rl);
        mTvtimeHadSumbPlay = (TextView) mFragmentView.findViewById(R.id.tv_recorded_sound_play_time);

        mTeacherRemarkRl = (RelativeLayout) mFragmentView.findViewById(R.id.teacher_remark_rl);         // 点评进度条整体
        mUserAnswerProgressBar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.user_answer_progressbar_left);           // 用户答案进度条
        mTeacherRemarkProgressBar = (RoundProgressBarWidthNumber) mFragmentView.findViewById(R.id.teacher_remark_progressbar_right);    // 老师点评进度条
        mRemarkNumb = (TextView) mFragmentView.findViewById(R.id.teacher_remark_number);            // 点评次数
        mQuestionHelpIv = (ImageView) mFragmentView.findViewById(R.id.question_help_iv);            // 问号图标
        mPurchasedLinkTv = (TextView) mFragmentView.findViewById(R.id.purchased_remark_tv);         // 购买链接
        mWaitRemarkingTv = (TextView) mFragmentView.findViewById(R.id.teacher_remark_waiting_tv);       // 点评中
        mUserAnswerPlayState = (TextView) mFragmentView.findViewById(R.id.user_answer_state_tv);           // 用户答案播放的状态
        mTeacherRemarkPlayTime = (TextView) mFragmentView.findViewById(R.id.teacher_remark_time);   // 具有老师点评时的时间
        mTeacherRemarkPlayState = (TextView) mFragmentView.findViewById(R.id.teacher_remark_state_tv);  // 具有老师点评时播放的状态
        mNotRemarkLl = (LinearLayout) mFragmentView.findViewById(R.id.teacher_notremark_ll);      // 没有申请点评
        mExistRemarkLl = (LinearLayout) mFragmentView.findViewById(R.id.teacher_remark_offered_ll);     // 存在老师点评整体


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

    private void initRecordFile() {
        String userId = LoginModel.getUserId();
        mRecordFolder = FileManager.getRootFilePath(mActivity) + "/interview/"  + userId + "/user_answer/";            // 自己录音的路径
        FileManager.mkDir(mRecordFolder);

        mUserAnswerFilePath = mRecordFolder + mQuestionBean.getId() + ".amr";        // 录音存储的文件路径
        mTemporaryFilePath = mRecordFolder + mQuestionBean.getId() + "temp.amr";        // 临时文件的存储路径
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

        // 名师点评部分
        mTeacherRemarkRl.setOnClickListener(OnClickListener);   // 名师点评
        mQuestionHelpIv.setOnClickListener(OnClickListener);    // 问号
        mPurchasedLinkTv.setOnClickListener(OnClickListener);   // 购买链接
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
                    if(mActivity.mMediaRecorderManagerUtil != null){
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

            } else if (id == R.id.interview_recordsounding_cancle) {   // 点击取消功能: 判断有效文件是否存在
                stopRecord();
                isCanTouch(true);
                // 检查可提交文件是否存在,如果存在,回到未提交页面,否则回到未录音页面
                if ( checkIsRecordFileExist() && isCanSubmit){     // 录音文件存在,进入可提交页面
                    mActivity.setCanBack(2);
                    // 修改录音文字
                    String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
                    mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration)+1));
                    changeRecordView(6);
                }else{
                    mActivity.setCanBack(0);                    // 不可以按返回键
                    mStatus = RECORDABLE;
                    String zero = "0\"";
                    mTvtimeRecording.setText(zero);
                    changeRecordView(1);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Cancel");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_confirm) {   //点击确认功能
                if (isCanSubmit) {
                    stopRecord();
                    isCanTouch(true);
                    mActivity.setCanBack(2);                // 返回键设置不可返回,点击有弹窗
                    isStop = false;
                    // 此时录音文件已经存在,判断确认文件是否存在,时长是否为零
                    changeFileName();
                    changeRecordView(3);
                    showRecordedDuration();             // 显示录音的时长
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
                if (mActivity.mMediaRecorderManagerUtil != null) {
                    stopPlay();         // 停止播放语音
                    mStatus = RECORDABLE;
                    String zero = "0\"";
                    mTvtimeRecording.setText(zero);
                    mTvtimeNotSubm.setText("听一下");
                    isCanSubmit = true;
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
                    stopPlay();             // 停止播放语音
                    mTvtimeNotSubm.setText("听一下");
                }else{
                    isStop = true;
                    mStatus = SUBMIT;               // 变成可提交状态
                    mTvtimeNotSubm.setText("停止播放");
                    play(mUserAnswerFilePath);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Playaudio");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);

            } else if (id == R.id.interview_recordsounding_rl_submit) {      // 点击提交按钮
                stopPlay();
                mModel.showSubmitAnswerProgressBar(mUserAnswerFilePath, mQuestionBean, FileManager.getVideoDuration(mUserAnswerFilePath), mQuestionType);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Submit");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if (id == R.id.interview_hadanswer_listen_rl) {           // 已提交播放按钮
                dealUserHadSubmittedAudio();         // 处理已提交录音: 播放,暂停,播放,记录断点

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Answer");
                UmengManager.onEvent(mActivity, "InterviewRecord", map);
            } else if(id == R.id.teacher_remark_rl){        // 名师点评部分

                dealTeacherRemarkPart();        // 处理名师点评部分
            } else if(id == R.id.question_help_iv){         // 问号
                // 跳转到帮助页面
                skipToRemarkHelpFragment();

            } else if(id == R.id.purchased_remark_tv){      // 购买链接
                // TODO: 2017/1/20 点击购买:请求,modle中处理,接口来回调刷新
                // 跳转到购买页面
                popupReminderPurchasedAlert();
            }
        }
    };
    /*
    *   处理用户已提交后的语音: 播放,暂停,记录断点,播放..
    * */
    private void dealUserHadSubmittedAudio() {
        if(isStop){
            isStop = false;
            pausePlay();
        }else{
            mStatus = HADSUBMIT;
            isStop = true;
            dealDownLoadAudio(mRecordFolder, mQuestionBean.getUser_audio());
        }
    }

    /*
    *   跳转到帮助页面
    * */
    private void skipToRemarkHelpFragment() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null).replace(R.id.interview_fragment_container, new InterviewTeacherRemarkGuideFragment()).commit();
        // 修改toolbar
        mActivity.setIsTeacherRemarkView(true);
        mActivity.invalidateOptionsMenu();
    }

    /*
    *   修改临时录音文件的名称为确认文件名
    * */
    private void changeFileName() {
        // 先检查确认文件是否已经存在
        if ( checkIsRecordFileExist() ){
            FileManager.deleteFiles(mUserAnswerFilePath); // 删除掉
        }
        // 修改名字:将临时文件的名字变成可提交的文件名字
        FileManager.renameFile(mTemporaryFilePath,mUserAnswerFilePath);
    }
    /*
    *   检查录音文件是否已经存在
    * */
    private boolean checkIsRecordFileExist() {
        File recordFile = new File(mUserAnswerFilePath);
        String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
        return recordFile.exists() && !duration.equals("") && Integer.parseInt(duration) >0;
    }

    private void changeTime() {
        if (mQuestionBean.getUser_audio_duration() >= 360){
            mTvtimeHadSumbPlay.setText(mModel.formatDateTime(360));
        }else{
            mTvtimeHadSumbPlay.setText(mModel.formatDateTime(mQuestionBean.getUser_audio_duration() + 1));
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
        }else if(i == 5){
            mUnRecordView.setVisibility(View.GONE);
            mRecordedView.setVisibility(View.VISIBLE);
            mPurchasedLinkTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            mPurchasedLinkTv .getPaint().setAntiAlias(true);//抗锯齿
        }else{
            mRecordingView.setVisibility(View.GONE);
            mUnsubmitView.setVisibility(View.VISIBLE);
        }
    }
    /*
    *   修改点评次数
    * */
    private void changeTeacherRemarkNum(){
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity,R.color.opencourse_btn_bg));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(mActivity, 16));
        StyleSpan styleSpan = new StyleSpan(Typeface.NORMAL);

        //解析
        SpannableString analysis =   new SpannableString("您有"+ String.valueOf(mTeacherRemarkRemainderNum) + "次点评申请");
        int length = String.valueOf(mTeacherRemarkRemainderNum).length() + 2;

        analysis.setSpan(colorSpan, 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(sizeSpan, 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        analysis.setSpan(styleSpan, 2, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        if(duration >=0 && duration <=20){
            if(duration >=5 ){
                isCanSubmit = true;
                mIvRecordSound.setImageResource(R.drawable.interview_confrim_blue);
                if (duration == 15){
                    showReminderToast();
                }
                if (duration >20){
                    mActivity.mMediaRecorderManagerUtil.stopReocrd();
                }
            }else{
                mIvRecordSound.setImageResource(R.drawable.interview_confirm_gray);
            }
            mTvtimeRecording.setText(mModel.formatDateTime(duration));
        }
    }
    /*
    *  显示已经录音的时长
    * */
    private void showRecordedDuration(){
        String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
        if(Integer.parseInt(duration) >= 360){
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(360));
        }else{
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration)+1));
        }
    }

    /*
    *  处理名师点评部分:
    * */
    private void dealTeacherRemarkPart(){
         //需要判断哪一个状态
        switch (mRemarkState){
            case UNHEAR:
                // 先得判断申请的次数
                if(mTeacherRemarkRemainderNum >0){
                    // 申请弹窗
                    popupApplyForRemarkAlert();
                }else{
                    // 购买alert
                    popupReminderPurchasedAlert();
                }

                break;
            case COMMENT:
                ToastManager.showToast(mActivity, "点评中");
                break;
            case HEAR:
                // TODO: 2017/1/20 播放语音
                break;
            default:
                break;
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
  *   播放语音: 播放时需要 实时返回时间
  * */
    public void play(String userAnswerFilePath) {
        mActivity.mMediaRecorderManagerUtil.setPlayFilePath(userAnswerFilePath);
        //播放的断点
        mActivity.mMediaRecorderManagerUtil.startPlay(mOffset, new MediaRecordManagerUtil.IPlayCompleteCallback() {
            @Override
            public void onPlayComplete() {
                ToastManager.showToast(mActivity, "播放完成");

                dealPlayedCompletedViewState();     // 分别处理未提交录音时和已提交录音时的状态处理
            }
        }, new MediaRecordManagerUtil.IPlayFileCountdownCallback() {
            @Override
            public void onPlayCountdown(int unPlayDur) {

                dealPlayingViewState(unPlayDur);    // 分别处理未提交录音时和已提交录音时的 倒计时的状态的处理
            }
        });
        // 设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
   *   暂停播放: 需要记录两个状态: 断点位置,播放时的时间
   * */
    private void pausePlay() {
        mActivity.mMediaRecorderManagerUtil.playOnPause(new MediaRecordManagerUtil.IPlayFileOffsetCallback() {
            @Override
            public void onPlayOffset(int offset) {
                mOffset = offset;                         // 记录断点
            }
        });
    }
    /*
    *   处理未提交语音时和已提交语音时 播放完成是的状态
    * */
    private void dealPlayedCompletedViewState(){
        if(mStatus.equals(SUBMIT)){     // 未提交时
            String duration = FileManager.getVideoDuration(mUserAnswerFilePath);
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(Integer.parseInt(duration)+1));
            isStop = false;
            mTvtimeNotSubm.setText("听一下");
        }else if(mStatus.equals(HADSUBMIT)){        // 已提交时
            changeTime();
            isStop = false;
            mStatus = HADSUBMIT;
        }
    }
    /*
    *   分别处理未提交录音时和已提交录音时的播放时倒计时的状态的处理
    * */
    private void dealPlayingViewState(int unPlayDur){
        if(mStatus.equals(SUBMIT)){     // 未提交时
            mTvtimeNotSubmPlay.setText(mModel.formatDateTime(unPlayDur));
        }else if(mStatus.equals(HADSUBMIT)){        // 已提交时
            mTvtimeHadSumbPlay.setText(mModel.formatDateTime(unPlayDur));
        }
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
            Logger.e("文件已经存在");
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
    }
}