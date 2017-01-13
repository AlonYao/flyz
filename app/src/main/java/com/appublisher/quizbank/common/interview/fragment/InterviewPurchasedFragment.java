package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

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
    private String mQuestionFileFolder;
    private String mAnalysisFileFolder;

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
        initFile();
    }

    private void initFile() {
        mQuestionFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + LoginModel.getUserId() + "/question_audio/";
        mAnalysisFileFolder = FileManager.getRootFilePath(mActivity) + "/interview/" + LoginModel.getUserId() + "/analysis_audio/";
        FileManager.mkDir(mQuestionFileFolder);
        FileManager.mkDir(mAnalysisFileFolder);
    }

    @Override
    public void initChildView() {
        mQuestionIm = (ImageView) mFragmentView.findViewById(R.id.interview_lookquestion_im);
        mQuestionTv = (TextView) mFragmentView.findViewById(R.id.interview_lookquestion_tv);

        mQuestionListenLl = (LinearLayout) mFragmentView.findViewById(R.id.interview_hadquestion_listen_ll);
        mAnalysisListenLl = (LinearLayout) mFragmentView.findViewById(R.id.interview_answer_listen_ll);

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
         *  本类为已付费页面的类,不用再和服务器交互:题目行的逻辑处理:逻辑:点击事件:展开与折叠;听语音播放但不可暂停
         * **/
        mQuestionSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionContent.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mQuestionContent.setVisibility(View.GONE);
                    mQuestionIm.setImageResource(R.drawable.interview_answer_lookover);
                    mQuestionTv.setText("看文字");

                } else {
                    mQuestionContent.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                    mQuestionIm.setImageResource(R.drawable.interview_answer_packup);
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
                if (mAnalysisView.getVisibility() == View.VISIBLE) {    // 打开-->折叠状态
                    mAnalysisView.setVisibility(View.GONE);
                    mAnalysisIm.setImageResource(R.drawable.interview_answer_lookover);
                    mReminderTv.setText("看文字");
                } else {
                    mAnalysisView.setVisibility(View.VISIBLE);           // 折叠-->展开状态
                    mAnalysisIm.setImageResource(R.drawable.interview_answer_packup);
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

                dealDownLoadAudio(mQuestionFileFolder, mQuestionBean.getQuestion_audio());

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

                dealDownLoadAudio(mAnalysisFileFolder, mQuestionBean.getAnalysis_audio());

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
    public void releaseFragmentTouch() {
        mQuestionListenLl.setClickable(true);
        mAnalysisListenLl.setClickable(true);
        mQuestionSwitchView.setClickable(true);
    }
    @Override
    public void banFragmentTouch() {
        mQuestionListenLl.setClickable(false);
        mAnalysisListenLl.setClickable(false);
        mQuestionSwitchView.setClickable(false);
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
