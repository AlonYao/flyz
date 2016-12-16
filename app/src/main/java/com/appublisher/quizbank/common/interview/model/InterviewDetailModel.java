package com.appublisher.quizbank.common.interview.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.volley.RequestCallback;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewDetailModel implements RequestCallback {

    private Context mContext;

    public InterviewDetailModel(Context context) {
        mContext = context;
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
