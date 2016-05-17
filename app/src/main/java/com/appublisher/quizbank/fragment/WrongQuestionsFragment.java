package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.KnowledgeTreeModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 错题本
 */
public class WrongQuestionsFragment extends Fragment implements RequestCallback {

    private Activity mActivity;
    private LinearLayout mContainer;
    private ImageView mIvNull;
    private View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wrongquestions, container, false);

        // View 初始化
        mContainer = (LinearLayout) mView.findViewById(R.id.wrongq_container);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        if (!isHidden()) getData();

        // Umeng
        MobclickAgent.onPageStart("WrongQuestionsFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "WrongQuestionsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("WrongQuestionsFragment");

        // TalkingData
        TCAgent.onPageEnd(mActivity, "WrongQuestionsFragment");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 获取数据
        if (!hidden) getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        ProgressBarManager.showProgressBar(mView);
        new Request(mActivity, this).getNoteHierarchy(KnowledgeTreeModel.TYPE_ERROR);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("note_hierarchy".equals(apiName))
            new KnowledgeTreeModel(
                    mActivity,
                    mContainer,
                    KnowledgeTreeModel.TYPE_ERROR,
                    new KnowledgeTreeModel.ICheckHierarchyResp() {
                @Override
                public void isCorrectData(boolean isCorrect) {
                    if (isCorrect) {
                        mIvNull.setVisibility(View.GONE);
                    } else {
                        mIvNull.setVisibility(View.VISIBLE);
                    }
                }
            }).dealHierarchyResp(response);

        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressBarManager.hideProgressBar();
    }
}
