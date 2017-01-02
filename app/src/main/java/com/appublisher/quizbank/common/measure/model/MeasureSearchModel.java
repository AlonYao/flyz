package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 做题模块：搜题
 */

public class MeasureSearchModel implements RequestCallback, MeasureConstants{

    private static final int COUNT = 10;

    private Context mContext;
    private MeasureRequest mRequest;

    public MeasureSearchModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
    }

    public void search(String keywords) {
        mRequest.searchQuestion(keywords, 0, COUNT);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (SEARCH_QUESTION.equals(apiName)) {
            dealSearchQuestionResp(response);
        }

        hideLoading();
    }

    private void dealSearchQuestionResp(JSONObject response) {
        MeasureSearchResp resp = GsonManager.getModel(response, MeasureSearchResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        Logger.e(String.valueOf(resp.getTotal()));
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    private void hideLoading() {
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

}
