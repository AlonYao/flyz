package com.appublisher.quizbank.common.measure;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.volley.RequestCallback;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 做题模块：管理类
 */

public class MeasureModel implements RequestCallback{

    private Context mContext;

    MeasureModel(Context context) {
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
