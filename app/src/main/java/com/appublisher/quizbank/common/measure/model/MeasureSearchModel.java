package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.common.measure.view.IMeasureSearchView;

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
    private List<MeasureSearchResp.SearchItemBean> mList;
    private IMeasureSearchView mView;

    public MeasureSearchModel(Context context, IMeasureSearchView view) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
        mView = view;
    }

    public void search(String keywords) {
        mCurKeywords = keywords;
        mOffset = 0;
        mRequest.searchQuestion(keywords, mOffset, COUNT);
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
        if (resp == null || resp.getResponse_code() != 1) {
            resetOffset();
            return;
        }

        if (mOffset == 0) {
            if (resp.getList() == null || resp.getList().size() == 0) {
                mView.showNone();
            } else {
                mView.showNotice(mCurKeywords, resp.getTotal());
            }
            mList = resp.getList();
            mView.showContent(mList);

            // Umeng
            UmengManager.onEvent(mContext, "Searchlist");
        } else {
            if (resp.getList() == null || resp.getList().size() == 0) {
                mView.showNoMoreToast();
                resetOffset();
            } else {
                mList.addAll(resp.getList());
                mView.showLoadMore(mList);
            }
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
        resetOffset();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
        resetOffset();
    }

    private void hideLoading() {
        mView.hideLoading();
        mView.stopXListView();
    }

    public void loadMore() {
        mOffset = mOffset + COUNT;
        mRequest.searchQuestion(mCurKeywords, mOffset, COUNT);
    }

    public String getCurKeywords() {
        return mCurKeywords;
    }

    private void resetOffset() {
        if (mOffset <= 0) {
            mOffset = 0;
        } else {
            mOffset = mOffset - COUNT;
        }
    }
}
