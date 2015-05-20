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
import com.appublisher.quizbank.model.FavoriteModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 收藏夹
 */
public class FavoriteFragment extends Fragment implements RequestCallback{

    public Activity mActivity;
    public LinearLayout mContainer;
    public ImageView mIvNull;

    private View mView;

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
        ProgressBarManager.showProgressBar(mView);
        new Request(mActivity, this).getNoteHierarchy("collect");

        // Umeng
        MobclickAgent.onPageStart("FavoriteFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("FavoriteFragment");
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("note_hierarchy".equals(apiName))
            FavoriteModel.dealNoteHierarchyResp(this, response);

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
