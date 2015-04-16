package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 错题本
 */
public class WrongQuestionsFragment extends Fragment implements RequestCallback{

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrongquestions, container, false);

        // 成员变量初始化
        Request request = new Request(mActivity, this);

        // 获取数据
        ProgressBarManager.showProgressBar(view);
        request.getNoteHierarchy("error");

        return view;
    }

    /**
     * 处理知识点层级（错题）回调
     * @param response 回调对象
     */
    private void dealNoteHierarchyResp(JSONObject response) {
        if (response == null) return;
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("note_hierarchy".equals(apiName)) dealNoteHierarchyResp(response);

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
