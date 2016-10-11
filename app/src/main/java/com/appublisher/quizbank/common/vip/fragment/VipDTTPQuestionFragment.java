package com.appublisher.quizbank.common.vip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.activity.VipGalleryActivity;
import com.appublisher.quizbank.common.vip.model.VipBaseModel;
import com.appublisher.quizbank.common.vip.model.VipDTTPQuestionModel;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 小班：单题突破 问题Fragment
 */

public class VipDTTPQuestionFragment extends Fragment{

    private static final String ARGS_DATA = "data";

    private VipDTTPResp mResp;
    private View mRoot;
    private WebView mWvQuestion;
    private FlowLayout mMyjobContainer;
    private VipDTTPQuestionModel mModel;
    private Button mBtnSubmit;
    private TextView mTvStatus;

    public static VipDTTPQuestionFragment newInstance(VipDTTPResp resp) {
        Bundle args = new Bundle();
        args.putString(ARGS_DATA, GsonManager.modelToString(resp));
        VipDTTPQuestionFragment fragment = new VipDTTPQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipDTTPResp.class);
        mModel = new VipDTTPQuestionModel(getContext());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == VipBaseModel.CAMERA_REQUEST_CODE) {
            // 拍照回调
            ArrayList<String> paths =
                    data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mModel.mPaths != null) {
                mModel.mPaths.addAll(paths);
            } else {
                mModel.mPaths = paths;
            }
            showMyJob();
            updateSubmitButton();

        } else if (requestCode == VipBaseModel.GALLERY_REQUEST_CODE) {
            // 图片浏览回调
            mModel.mPaths = data.getStringArrayListExtra(VipGalleryActivity.INTENT_PATHS);
            showMyJob();
            updateSubmitButton();
        }
    }

    /**
     * 显示内容
     */
    private void showContent() {
        if (mResp == null || mResp.getResponse_code() != 1) return;
        VipDTTPResp.QuestionBean questionBean = mResp.getQuestion();
        mModel.mCanSubmit = mResp.isCan_submit();
        int status = mResp.getStatus();
        String statusText = mResp.getStatus_text();

        // 题目
        if (questionBean != null) {
            mWvQuestion.setBackgroundColor(0);
            mWvQuestion.loadDataWithBaseURL(
                    null, questionBean.getQuestion(), "text/html", "UTF-8", null);
        }

        // 我的作业处理
        mModel.mPaths = getOriginImgs();
        showMyJob();

        // 状态问题
        showStatus(status, statusText);

        // 提交按钮
        if (mModel.mCanSubmit) {
            mBtnSubmit.setVisibility(View.VISIBLE);
        } else {
            mBtnSubmit.setVisibility(View.GONE);
        }

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 完成状态
        if (status == 1) {
//            showReview();
        }
    }

    /**
     * 获取用户原始做题答案
     * @return ArrayList
     */
    private ArrayList<String> getOriginImgs() {
        VipDTTPResp.UserAnswerBean userAnswerBean = mResp.getUser_answer();
        if (userAnswerBean == null) return null;
        VipDTTPResp.UserAnswerBean.OriginBean originBean = userAnswerBean.getOrigin();
        if (originBean == null) return null;
        return originBean.getImages();
    }

    private void initView(LayoutInflater inflater,
                          @Nullable ViewGroup container) {
        mRoot = inflater.inflate(R.layout.vip_dttp_question_fragment, container, false);
        mWvQuestion = (WebView) mRoot.findViewById(R.id.vip_dttp_question_webview);
        mMyjobContainer = (FlowLayout) mRoot.findViewById(R.id.vip_dttp_myjob_container);
        mBtnSubmit = (Button) mRoot.findViewById(R.id.vip_dttp_submit);
        mTvStatus = (TextView) mRoot.findViewById(R.id.vip_dttp_status);
    }

    /**
     * 显示我的作业
     */
    private void showMyJob() {
        String type;
//        mModel.mCanSubmit = true;
        if (mModel.mCanSubmit) {
            type = VipBaseActivity.FILE;
        } else {
            type = VipBaseActivity.URL;
        }

        mModel.showMyJob(
                mModel.mPaths,
                type,
                VipDTTPQuestionModel.MAX_LENGTH,
                mMyjobContainer,
                getContext(),
                new VipBaseActivity.MyJobActionListener() {
                    @Override
                    public void toCamera(int maxLength) {
                        mModel.toCamera(maxLength);
                    }
                });
    }

    /**
     * 更新提交按钮
     */
    private void updateSubmitButton() {
        int curLength = mModel.mPaths == null ? 0 : mModel.mPaths.size();
        int maxLength = VipDTTPQuestionModel.MAX_LENGTH;
        mModel.updateSubmitButton(curLength, maxLength, mBtnSubmit);
    }

//    /**
//     * 显示教师评语部分
//     */
//    private void showReview() {
//        ViewStub vsReview = (ViewStub) mRoot.findViewById(R.id.vip_msjp_review_viewstub);
//        vsReview.inflate();
//        RoundedImageView ivAvatar =
//                (RoundedImageView) mRoot.findViewById(R.id.vip_msjp_review_avatar);
//        TextView tvName = (TextView) mRoot.findViewById(R.id.vip_msjp_review_teacher_name);
//        TextView tvDate = (TextView) mRoot.findViewById(R.id.vip_msjp_review_date);
//        TextView tvScore = (TextView) mRoot.findViewById(R.id.vip_msjp_review_score);
//        TextView tvRemark = (TextView) mRoot.findViewById(R.id.vip_msjp_review_remark);
//        FlowLayout reviewImgContainer =
//                (FlowLayout) mRoot.findViewById(R.id.vip_msjp_review_img_container);
//        WebView reviewAnswer = (WebView) mRoot.findViewById(R.id.vip_msjp_review_answer);
//
//        if (mResp == null) return;
//        VipDTTPResp.UserAnswerBean userAnswerBean = mResp.getUser_answer();
//        if (userAnswerBean == null) return;
//
//        VipDTTPResp.UserAnswerBean.ReviewBean reviewBean = userAnswerBean.getReview();
//        if (reviewBean == null) return;
//
//        // 老师评论
//        VipMSJPResp.UserAnswerBean.ReviewBean.LectorBean lectorBean = reviewBean.getLector();
//        if (lectorBean != null) {
//            ImageManager.displayImage(lectorBean.getAvatar(), ivAvatar);
//            tvName.setText(lectorBean.getName());
//        }
//        tvDate.setText(reviewBean.getReview_time());
//        tvScore.setText(String.valueOf(reviewBean.getScore()));
//
//        // 老师评语
//        tvRemark.setText(reviewBean.getReview_postil());
//
//        // 老师批改
//        reviewImgContainer.removeAllViews();
//        final ArrayList<String> imgs = reviewBean.getImages();
//        int size = imgs.size();
//        for (int i = 0; i < size; i++) {
//            final int index = i;
//            ImageView imageView = mModel.getMyJobItem();
//            ImageManager.displayImage(imgs.get(i), imageView);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent =
//                            new Intent(getContext(), VipGalleryActivity.class);
//                    intent.putExtra(VipGalleryActivity.INTENT_INDEX, index);
//                    intent.putExtra(VipGalleryActivity.INTENT_PATHS, imgs);
//                    startActivity(intent);
//                }
//            });
//            reviewImgContainer.addView(imageView);
//        }
//
//        // 参考答案
//        VipMSJPResp.QuestionBean questionBean = mResp.getQuestion();
//        if (questionBean != null) {
//            reviewAnswer.setBackgroundColor(0);
//            reviewAnswer.loadDataWithBaseURL(
//                    null, questionBean.getAnswer(), "text/html", "UTF-8", null);
//        }
//    }

    /**
     * 显示状态文字
     * @param status 状态
     * @param text 文字
     */
    @SuppressWarnings("deprecation")
    public void showStatus(int status, String text) {
        if (status == 0) {
            mTvStatus.setVisibility(View.GONE);
        } else {
            mTvStatus.setVisibility(View.VISIBLE);
            // 文字
            if (status == 3) {
                mTvStatus.setText("等待批改");
            } else {
                mTvStatus.setText(text);
            }
            // 颜色
            if (status == 1) {
                mTvStatus.setTextColor(getResources().getColor(R.color.vip_green));
            } else {
                mTvStatus.setTextColor(getResources().getColor(R.color.vip_red));
            }
        }
    }

}
