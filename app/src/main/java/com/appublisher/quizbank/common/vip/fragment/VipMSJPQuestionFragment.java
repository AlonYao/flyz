package com.appublisher.quizbank.common.vip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.activity.VipGalleryActivity;
import com.appublisher.quizbank.common.vip.model.VipBaseModel;
import com.appublisher.quizbank.common.vip.model.VipMSJPQuestionModel;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 小班：名师精批 问题Tab
 */
public class VipMSJPQuestionFragment extends Fragment {

    private static final String ARGS_DATA = "data";

    private VipMSJPResp mResp;
    private View mRoot;
    private WebView mWvQuestion;
    private FlowLayout mMyjobContainer;
    private VipMSJPQuestionModel mModel;

    public static VipMSJPQuestionFragment newInstance(VipMSJPResp resp) {
        Bundle args = new Bundle();
        args.putString(ARGS_DATA, GsonManager.modelToString(resp));
        VipMSJPQuestionFragment fragment = new VipMSJPQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResp = GsonManager.getModel(getArguments().getString(ARGS_DATA), VipMSJPResp.class);
        mModel = new VipMSJPQuestionModel(getContext());
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

        } else if (requestCode == VipBaseModel.GALLERY_REQUEST_CODE) {
            // 图片浏览回调
            mModel.mPaths = data.getStringArrayListExtra(VipGalleryActivity.INTENT_PATHS);
            showMyJob();
        }
    }

    private void showContent() {
        if (mResp == null || mResp.getResponse_code() != 1) return;
        VipMSJPResp.QuestionBean questionBean = mResp.getQuestion();
        mModel.mCanSubmit = mResp.isCan_submit();
        if (questionBean != null) {
            // 题目
            mWvQuestion.setBackgroundColor(0);
            mWvQuestion.loadDataWithBaseURL(
                    null, questionBean.getQuestion(), "text/html", "UTF-8", null);
        }

        // 我的作业处理
        showMyJob();
    }

    private void initView(LayoutInflater inflater,
                          @Nullable ViewGroup container) {
        mRoot = inflater.inflate(R.layout.vip_msjp_question_fragment, container, false);
        mWvQuestion = (WebView) mRoot.findViewById(R.id.vip_msjp_question_webview);
        mMyjobContainer = (FlowLayout) mRoot.findViewById(R.id.vip_msjp_myjob_container);
    }

    /**
     * 显示我的作业
     */
    private void showMyJob() {
        String type;
        if (!mModel.mCanSubmit) {
            type = VipBaseActivity.FILE;
        } else {
            type = VipBaseActivity.URL;
        }

        mModel.showMyJob(
                mModel.mPaths,
                type,
                VipMSJPQuestionModel.MAX_LENGTH,
                mMyjobContainer,
                getContext(),
                new VipBaseActivity.MyJobActionListener() {
                    @Override
                    public void toCamera(int maxLength) {
                        mModel.toCamera(maxLength);
                    }
                });
    }

}
