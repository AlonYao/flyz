package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureSearchActivity;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 做题模块：搜题
 */

public class MeasureSearchModel implements RequestCallback, MeasureConstants{

    private static final int COUNT = 10;

    private Context mContext;
    private MeasureRequest mRequest;
    private String mCurKeywords;
    private int mOffset;

    public MeasureSearchModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
    }

    public void search(String keywords) {
        mCurKeywords = keywords;
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
        showContent(resp.getList());
    }

    private void showContent(List<MeasureSearchResp.SearchItemBean> list) {
        if (!(mContext instanceof MeasureSearchActivity)) return;
        ((MeasureSearchActivity) mContext).showContent(list);
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
        if (mContext instanceof MeasureSearchActivity) {
            ((MeasureSearchActivity) mContext).hideLoading();
            ((MeasureSearchActivity) mContext).stopXListView();
        }
    }

    public void loadMore() {
        mOffset = mOffset + COUNT;
        mRequest.searchQuestion(mCurKeywords, mOffset, COUNT);
    }
}
