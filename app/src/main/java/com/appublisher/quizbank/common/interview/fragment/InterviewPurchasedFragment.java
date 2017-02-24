package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.util.HashMap;


public class InterviewPurchasedFragment extends InterviewDetailBaseFragment {

    private static final String ARGS_QUESTION_BEAN = "questionBean";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_LIST_LENGTH = "listLength";
    private static final String QUESTION_TYPE = "questionType";
    private InterviewPaperDetailResp.QuestionsBean mQuestionBean;
    private int mPosition;
    private InterviewPaperDetailActivity mActivity;
    private int mListLength;
    private String mQuestionType;
    private ImageView mQuestionIm;
    private TextView mQuestionTv;
    private LinearLayout mQuestionListenLl;
    private LinearLayout mAnalysisListenLl;
    private TextView mQuestionSwitchTv;


    public static InterviewPurchasedFragment newInstance(String questionBean, int position,int listLength,String questionType) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION_BEAN, questionBean);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_LIST_LENGTH, listLength);
        args.putString(QUESTION_TYPE, questionType);    // 问题的类型
        InterviewPurchasedFragment fragment = new InterviewPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (InterviewPaperDetailActivity) getActivity();

        mQuestionBean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTION_BEAN), InterviewPaperDetailResp.QuestionsBean.class);
        // 问题的类型
        mQuestionType = getArguments().getString(QUESTION_TYPE);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mListLength = getArguments().getInt(ARGS_LIST_LENGTH);

    }

    @Override
    public String getIsUnPurchasedOrPurchasedView() {
        return "PurchasedView";
    }

    @Override
    public int getChildViewPosition() {         // 获取当前的view的id
        return mPosition;
    }

    @Override
    public void initChildView() {
        mQuestionIm = (ImageView) mFragmentView.findViewById(R.id.interview_lookquestion_im);
        mQuestionTv = (TextView) mFragmentView.findViewById(R.id.interview_lookquestion_tv);
        mQuestionSwitchTv = (TextView) mFragmentView.findViewById(R.id.question_switch_tv);

        mQuestionListenLl = (LinearLayout) mFragmentView.findViewById(R.id.interview_hadquestion_listen_ll);
        mAnalysisListenLl = (LinearLayout) mFragmentView.findViewById(R.id.interview_answer_listen_ll);

        if (mQuestionBean.getQuestion_audio() == null || mQuestionBean.getQuestion_audio_duration() == 0){
            mQuestionListenLl.setVisibility(View.GONE);
        } else {
            mQuestionListenLl.setVisibility(View.VISIBLE);
        }
        if (mQuestionBean.getAnalysis_audio() == null || mQuestionBean.getAnalysis_audio_duration() == 0){
            mAnalysisListenLl.setVisibility(View.GONE);
        } else {
            mAnalysisListenLl.setVisibility(View.VISIBLE);
        }
        showQuestionId();
    }

    private void showQuestionId() {
        // 题目行的文字处理
        mQuestionSwitchTv.setText("第 " + (mPosition + 1) + "/" + mListLength + " 题");
    }

    @Override
    public int setLayoutResourceId() {
        return R.layout.interview_question_item_recordsound_hadpayfor;
    }

    @Override
    public InterviewPaperDetailResp.QuestionsBean initChildData() {
        return mQuestionBean;
    }

    @Override
    public String initChildQuestionType() {
        return mQuestionType;
    }

    @Override
    public void initChildListener() {
        if (mQuestionBean == null || mPosition >= mListLength || mPosition < 0) return;

        mQuestionContentLl.setVisibility(View.GONE);  // 题目的展示容器默认不显示

        /**
         *  题目行的逻辑处理
         * **/
        mQuestionSwitchViewRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !mIsCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mQuestionContentLl.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mQuestionContentLl.setVisibility(View.GONE);
                    mQuestionIm.setImageResource(R.drawable.interview_answer_lookover);
                    mQuestionTv.setText("看文字");

                } else {
                    mQuestionContentLl.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                    mQuestionIm.setImageResource(R.drawable.interview_fold_up);
                    mQuestionTv.setText("不看文字");
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
        mAnalysisSwitchViewRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !mIsCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mAnalysisViewLl.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mAnalysisViewLl.setVisibility(View.GONE);
                    mAnalysisIv.setImageResource(R.drawable.interview_answer_lookover);
                    mReminderTv.setText("看文字");
                } else {
                    mAnalysisViewLl.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                    mAnalysisIv.setImageResource(R.drawable.interview_fold_up);
                    mReminderTv.setText("不看文字");
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

        /*
        *  题目行语音
        * */
        mQuestionListenLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !mIsCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mPlayingMedia.equals(QUESTION_ITEM)){
                    mIsQuestionAudioPause = true;
                } else {
                    // 判断是否存在其他的正在播放的语音
                    changePlayingMediaToPauseState();
                }
                dealQuestionAudioPlayState();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "ListenQ");
                if (isDone()) {
                    UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                }
            }
        });

        /*
        *  解析行语音
        * */
        mAnalysisListenLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !mIsCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mPlayingMedia.equals(ANALYSIS_ITEM)){
                    mIsAnalysisAudioPause = true;
                } else {
                    // 判断是否存在其他的正在播放的语音
                    changePlayingMediaToPauseState();
                }
                dealAnalysisAudioPlayState();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "ListenA");
                if (isDone()) {
                    UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                } else {
                    UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                }

            }
        });
    }

    @Override
    public String getChildFragmentRich() {
        // 题目行中文字的处理
        return (mPosition + 1) + "/" + mListLength + "  " + mQuestionBean.getQuestion();
    }
    private boolean isDone() {
        return mQuestionBean != null && mQuestionBean.getUser_audio().length() > 0;
    }

}
