package com.appublisher.quizbank.common.vip.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipGalleryActivity;
import com.appublisher.quizbank.common.vip.model.VipHPTSQuestionModel;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * 小班：互评提升 问题Tab
 */

public class VipHPTSQuestionFragment extends Fragment {

    private static final String ARGS_DATA = "data";

    private VipHPTSResp mResp;
    private View mRoot;
    private WebView mWvQuestion;
    private WebView mWvAnswer;
    private VipHPTSQuestionModel mModel;
    private Button mBtnSubmit;
    private FlowLayout mOtherContainer;
    private TextView mTvLevel;
    private TextView mTvOtherName;
    private TextView mTvOtherDate;
    private TextView mTvFinish;
    private RoundedImageView mIvOtherAvatar;
    private LinearLayout mLlUnFinish;
    private EditText mEtMyComment;
    private RadioGroup mRgLevel;
    private RadioButton mRbLevelGood;
    private RadioButton mRbLevelMiddle;
    private RadioButton mRbLevelNegative;
    private RadioButton mRbLevelCeng;
    private String mLevel;
    private int mRecordId;

    public static VipHPTSQuestionFragment newInstance(VipHPTSResp resp) {
        Bundle args = new Bundle();
        args.putString(ARGS_DATA, GsonManager.modelToString(resp));
        VipHPTSQuestionFragment fragment = new VipHPTSQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipHPTSResp.class);
        mModel = new VipHPTSQuestionModel(getContext());
        if (mResp != null) {
            mModel.mExerciseId = mResp.getExercise_id();
            VipHPTSResp.QuestionBean questionBean = mResp.getQuestion();
            if (questionBean != null) {
                mModel.mQuestionId = questionBean.getQuestion_id();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initView(inflater, container);
        showContent();
        return mRoot;
    }

    /**
     * 显示内容
     */
    private void showContent() {
        if (mResp == null || mResp.getResponse_code() != 1) return;
        VipHPTSResp.QuestionBean questionBean = mResp.getQuestion();
        mModel.mCanSubmit = mResp.isCan_submit();
        int status = mResp.getStatus();

        if (questionBean != null) {
            // 题目
            mWvQuestion.setBackgroundColor(Color.WHITE);
            mWvQuestion.loadDataWithBaseURL(
                    null, questionBean.getQuestion(), "text/html", "UTF-8", null);
            // 参考答案
            mWvAnswer.setBackgroundColor(Color.WHITE);
            mWvAnswer.loadDataWithBaseURL(
                    null, questionBean.getAnswer(), "text/html", "UTF-8", null);
        }

        // 他的作业处理
        showOther();

        // 提交按钮
        if (mModel.mCanSubmit) {
            mBtnSubmit.setVisibility(View.VISIBLE);
        } else {
            mBtnSubmit.setVisibility(View.GONE);
        }

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel.submit(mRecordId, mEtMyComment.getText().toString(), mLevel);
            }
        });

        showMyComment(status);
    }

    /**
     * 显示我的评论
     */
    private void showMyComment(int status) {
        if (status == 1) {
            // 已完成
            mTvFinish.setVisibility(View.VISIBLE);
            mLlUnFinish.setVisibility(View.GONE);
            if (mResp == null) return;
            VipHPTSResp.UserAnswerBean userAnswerBean = mResp.getUser_answer();
            if (userAnswerBean == null) return;
            VipHPTSResp.UserAnswerBean.MyPostilBean myPostilBean = userAnswerBean.getMy_postil();
            if (myPostilBean == null) return;
            mTvFinish.setText(myPostilBean.getReview_postil());
            mTvLevel.setText(myPostilBean.getReview_level());
        } else {
            // 未完成
            mTvFinish.setVisibility(View.GONE);
            mLlUnFinish.setVisibility(View.VISIBLE);

            mRgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.vip_hpts_level_good) {
                        mLevel = mRbLevelGood.getText().toString();
                    } else if (checkedId == R.id.vip_hpts_level_middle) {
                        mLevel = mRbLevelMiddle.getText().toString();
                    } else if (checkedId == R.id.vip_hpts_level_negative) {
                        mLevel = mRbLevelNegative.getText().toString();
                    } else if (checkedId == R.id.vip_hpts_level_ceng) {
                        mLevel = mRbLevelCeng.getText().toString();
                    }
                }
            });
        }
    }

    private void initView(LayoutInflater inflater,
                          @Nullable ViewGroup container) {
        mRoot = inflater.inflate(R.layout.vip_hpts_question_fragment, container, false);
        mWvQuestion = (WebView) mRoot.findViewById(R.id.vip_hpts_question_webview);
        mOtherContainer = (FlowLayout) mRoot.findViewById(R.id.vip_hpts_other_container);
        mBtnSubmit = (Button) mRoot.findViewById(R.id.vip_hpts_submit);
        mWvAnswer = (WebView) mRoot.findViewById(R.id.vip_hpts_answer);
        mIvOtherAvatar = (RoundedImageView) mRoot.findViewById(R.id.vip_hpts_other_avatar);
        mTvOtherName = (TextView) mRoot.findViewById(R.id.vip_hpts_other_name);
        mTvOtherDate = (TextView) mRoot.findViewById(R.id.vip_hpts_other_date);
        mTvFinish = (TextView) mRoot.findViewById(R.id.vip_hpts_mycomment_finish);
        mLlUnFinish = (LinearLayout) mRoot.findViewById(R.id.vip_hpts_mycomment_unfinish);
        mTvLevel = (TextView) mRoot.findViewById(R.id.vip_hpts_level);
        mEtMyComment = (EditText) mRoot.findViewById(R.id.vip_hpts_mycomment);
        mRgLevel = (RadioGroup) mRoot.findViewById(R.id.vip_hpts_level_rg);
        mRbLevelGood = (RadioButton) mRoot.findViewById(R.id.vip_hpts_level_good);
        mRbLevelMiddle = (RadioButton) mRoot.findViewById(R.id.vip_hpts_level_middle);
        mRbLevelNegative = (RadioButton) mRoot.findViewById(R.id.vip_hpts_level_negative);
        mRbLevelCeng = (RadioButton) mRoot.findViewById(R.id.vip_hpts_level_ceng);
    }

    /**
     * 显示他的作业
     */
    private void showOther() {
        if (mResp == null) return;
        VipHPTSResp.UserAnswerBean userAnswerBean = mResp.getUser_answer();
        if (userAnswerBean == null) return;
        VipHPTSResp.UserAnswerBean.UserRecordBean userRecordBean = userAnswerBean.getUser_record();
        if (userRecordBean == null) return;

        mRecordId = userRecordBean.getRecord_id();

        VipHPTSResp.UserAnswerBean.UserRecordBean.UserInfoBean userInfoBean =
                userRecordBean.getUser_info();
        if (userInfoBean != null) {
            ImageManager.displayImage(userInfoBean.getAvatar(), mIvOtherAvatar);
            mTvOtherName.setText(userInfoBean.getNickname());
        }

        mTvOtherDate.setText(userRecordBean.getSubmit_time());

        // 他的作业图片
        final ArrayList<String> paths = userRecordBean.getImages();
        if (paths != null) {
            mOtherContainer.removeAllViews();
            int size = paths.size();
            for (int i = 0; i < size; i++) {
                final int index = i;
                ImageView imageView = mModel.getMyJobItem();
                ImageManager.displayImage(paths.get(i), imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =
                                new Intent(getContext(), VipGalleryActivity.class);
                        intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
                        intent.putExtra(VipGalleryActivity.INTENT_PATHS, paths);
                        startActivity(intent);
                    }
                });
                mOtherContainer.addView(imageView);
            }
        }
    }

}
