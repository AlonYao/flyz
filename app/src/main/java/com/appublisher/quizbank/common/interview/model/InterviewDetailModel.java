package com.appublisher.quizbank.common.interview.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewDetailModel implements RequestCallback {

    private Context mContext;
    public InterviewRequest mRequest;

    public InterviewDetailModel(Context context) {
        mContext = context;
        mRequest = new InterviewRequest(context, this);
    }


    @Override
    public void requestCompleted(JSONObject response, String apiName) {

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).hideLoading();
    }
}
