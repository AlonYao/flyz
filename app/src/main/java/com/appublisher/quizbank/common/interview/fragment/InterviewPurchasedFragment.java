package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.io.File;
import java.util.HashMap;


public class InterviewPurchasedFragment extends InterviewDetailBaseFragment {

    private static final String ARGS_QUESTIONBEAN = "questionBean";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_LISTLENGTH = "listLength";
    private static final String QUESTIONTYPE = "questionType";
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
        args.putString(ARGS_QUESTIONBEAN, questionBean);
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

        mQuestionBean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        // 问题的类型
        mQuestionType = getArguments().getString(QUESTIONTYPE);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mListLength = getArguments().getInt(ARGS_LISTLENGTH);

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
        }else{
            mQuestionListenLl.setVisibility(View.VISIBLE);
        }
        if (mQuestionBean.getAnalysis_audio() == null || mQuestionBean.getAnalysis_audio_duration() == 0){
            mAnalysisListenLl.setVisibility(View.GONE);
        }else{
            mAnalysisListenLl.setVisibility(View.VISIBLE);
        }
        showQuestionId();
    }

    private void showQuestionId() {
        // 题目行的文字处理
        mQuestionSwitchTv.setText("第 " + (mPosition + 1) + "/" + mListLength + " 题");
    }

    @Override
    public int setLayoutResouceId() {
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

        mQuestionContent.setVisibility(View.GONE);  // 题目的展示容器默认不显示

        /**
         *  题目行的逻辑处理
         * **/
        mQuestionSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !isCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mQuestionContent.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mQuestionContent.setVisibility(View.GONE);
                    mQuestionIm.setImageResource(R.drawable.interview_answer_lookover);
                    mQuestionTv.setText("看文字");

                } else {
                    mQuestionContent.setVisibility(View.VISIBLE);           // 折叠-->展开状态
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
        mAnalysisSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !isCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (mAnalysisView.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mAnalysisView.setVisibility(View.GONE);
                    mAnalysisIm.setImageResource(R.drawable.interview_answer_lookover);
                    mReminderTv.setText("看文字");
                } else {
                    mAnalysisView.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                    mAnalysisIm.setImageResource(R.drawable.interview_fold_up);
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
                if( !isCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if (isPlaying.equals(QUESTIONITEM)){
                    isQuestionAudioPause = true;
                }else{
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
                if( !isCanTouch){
                    ToastManager.showToast(mActivity, "请专心录音哦");
                    return;
                }
                if(isPlaying.equals(ANALYSISITEM)){
                    isAnalysisAudioPause = true;
                }else{
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        String filePath = mQuestionFileFolder + mQuestionBean.getId() + ".amr";
        String analysisFilePath = mAnalysisFileFolder + mQuestionBean.getId() + ".amr";
        File file = new File(filePath);
        File analysisfile = new File(analysisFilePath);
        if (file.exists() ) {                                   // 如果文件存在直接播放
            FileManager.deleteFiles(filePath);
        }
        if (analysisfile.exists() ) {                                   // 如果文件存在直接播放
            FileManager.deleteFiles(analysisFilePath);
        }
    }
}
