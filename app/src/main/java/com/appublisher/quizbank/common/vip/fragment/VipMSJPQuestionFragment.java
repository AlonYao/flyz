package com.appublisher.quizbank.common.vip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

import org.apmem.tools.layouts.FlowLayout;

/**
 * 小班：名师精批 问题Tab
 */
public class VipMSJPQuestionFragment extends Fragment {

    private static final String ARGS_DATA = "data";

    private VipMSJPResp mResp;
    private View mRoot;
    private WebView mWvQuestion;
    private VipMSJPActivity mActivity;
    private FlowLayout mMyjobContainer;

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
        mActivity = (VipMSJPActivity) getActivity();
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

    private void showContent() {
        if (mResp == null || mResp.getResponse_code() != 1) return;
        VipMSJPResp.QuestionBean questionBean = mResp.getQuestion();
        boolean canSubmit = mResp.isCan_submit();
        if (questionBean != null) {
            // 题目
            mWvQuestion.setBackgroundColor(0);
            mWvQuestion.loadDataWithBaseURL(
                    null, questionBean.getQuestion(), "text/html", "UTF-8", null);
        }

        // 我的作业处理
        if (canSubmit) {
//            mActivity.showMyJob();
        } else {

        }
    }

    private void initView(LayoutInflater inflater,
                          @Nullable ViewGroup container) {
        mRoot = inflater.inflate(R.layout.vip_msjp_question_fragment, container, false);
        mWvQuestion = (WebView) mRoot.findViewById(R.id.vip_msjp_question_webview);
        mMyjobContainer = (FlowLayout) mRoot.findViewById(R.id.vip_msjp_myjob_container);
    }

    /**
     * get & set
     */
    public FlowLayout getMyjobContainer() {
        return mMyjobContainer;
    }
}
