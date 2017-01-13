package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.util.HashMap;

/**
 * Created by huaxiao on 2016/12/16.
 * 用来展示未付费页面的Fragment,本类中只处理控件view,数据展示和控件点击交给InterviewUnPurchasdModel
 */
public class InterviewUnPurchasedFragment extends InterviewDetailBaseFragment {

    private static final String ARGS_QUESTIONBEAN = "questionBean";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_LISTLENGTH = "listLength";
    private static final String QUESTIONTYPE = "questionType";
    public ViewPager mViewPager;
    private InterviewPaperDetailResp.QuestionsBean mQuestionBean;
    private int mPosition;
    private int mListLength;
    private InterviewPaperDetailActivity mActivity;
    private String mQuestionType;

    public static Fragment newInstance(String questionBean, int position, int listLength, String questionType) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONBEAN, questionBean);
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

        mQuestionBean = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTIONBEAN), InterviewPaperDetailResp.QuestionsBean.class);
        mQuestionType = getArguments().getString(QUESTIONTYPE);  // 问题的类型

        mListLength = getArguments().getInt(ARGS_LISTLENGTH);

        mPosition = getArguments().getInt(ARGS_POSITION);          // 问题的索引

    }

    @Override
    public String getChildFragmentRich() {           // 传给basefragment问题数据
        return (mPosition + 1) + "/" + mListLength + "  " + mQuestionBean.getQuestion();
    }

    @Override
    public void banFragmentTouch() {
    }

    @Override
    public void releaseFragmentTouch() {
    }

    @Override
    public int setLayoutResouceId() {               // 传给basefragment布局id
        return R.layout.interview_question_item_recordsound_notpayfor;
    }

    @Override
    public void initChildView() {
    }

    @Override
    public InterviewPaperDetailResp.QuestionsBean initChildData() {         // 传给basefragment数据集合bean
        return mQuestionBean;
    }

    @Override
    public String initChildQuestionType() {                 // 传给basefragment问题的类型
        return mQuestionType;
    }

    @Override
    public void initChildListener() {
        if (mQuestionBean == null || mPosition >= mListLength || mPosition < 0) return;
        /**
         *  展开解析时需要监听是否已经答题: 用一个常量字符记录(在基类中处理录音页面的逻辑时)
         * **/
        mAnalysisSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {     // 解析行的逻辑处理:
                if (isDone() || isBuyAll() || isBuySingle()) {               // 已经答题
                    if (mAnalysisView.getVisibility() == View.VISIBLE) {
                        mAnalysisView.setVisibility(View.GONE);
                        mAnalysisIm.setImageResource(R.drawable.interview_answer_lookover);
                        mReminderTv.setText("查看");
                    } else {
                        // 如果答完题状态
                        mAnalysisView.setVisibility(View.VISIBLE);
                        mAnalysisIm.setImageResource(R.drawable.interview_answer_packup);
                        mReminderTv.setText("收起");
                    }
                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "ReadA");
                    if (isDone()) {
                        UmengManager.onEvent(mActivity, "InterviewAnalysis", map);
                    } else {
                        UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                    }

                } else {                      // 未答题
                    // 弹窗处理:三个item

                    mActivity.mModel.showNoAnswerDialog();
                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Answer");
                    UmengManager.onEvent(mActivity, "InterviewQuestion", map);
                }
            }
        });
    }

    private boolean isDone() {
        return mQuestionBean != null && mQuestionBean.getUser_audio().length() > 0;
    }

    private boolean isBuySingle() {
        return mQuestionBean != null && mQuestionBean.isPurchased_audio();
    }

    private boolean isBuyAll() {
        if (mActivity == null) return false;
        InterviewPaperDetailResp.AllAudioBean bean = mActivity.getAllAudioBean();
        return bean != null && bean.is_purchased();
    }

}
