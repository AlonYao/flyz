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
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.KnowledgeTreeModel;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 收藏夹
 */
public class FavoriteFragment extends Fragment implements RequestCallback {

    public Activity mActivity;
    public LinearLayout mContainer;
    public ImageView mIvNull;

    private View mView;
    private long mUMTimeStamp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_favorite, container, false);
        mContainer = (LinearLayout) mView.findViewById(R.id.collect_container);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        if (!isHidden()) getData();

        // Umeng
        MobclickAgent.onPageStart("FavoriteFragment");
        mUMTimeStamp = System.currentTimeMillis();

        // TalkingData
        TCAgent.onPageStart(mActivity, "FavoriteFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("FavoriteFragment");
        int dur = (int) ((System.currentTimeMillis() - mUMTimeStamp) / 1000);
        HashMap<String, String> map = new HashMap<>();
        UmengManager.onEventValue(getContext(), "Collect", map, dur);

        // TalkingData
        TCAgent.onPageEnd(mActivity, "FavoriteFragment");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 获取数据
        if (!hidden) getData();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("note_hierarchy".equals(apiName))
            new KnowledgeTreeModel(
                    mActivity,
                    mContainer,
                    KnowledgeTreeModel.TYPE_COLLECT,
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

    /**
     * 获取数据
     */
    private void getData() {
        ProgressBarManager.showProgressBar(mView);
        new QRequest(mActivity, this).getNoteHierarchy(KnowledgeTreeModel.TYPE_COLLECT);
    }
}
