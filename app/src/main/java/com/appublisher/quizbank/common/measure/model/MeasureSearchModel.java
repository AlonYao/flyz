package com.appublisher.quizbank.common.measure.model;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
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
    private List<MeasureSearchResp.SearchItemBean> mList;

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
        if (!(mContext instanceof MeasureSearchActivity)) return;

        MeasureSearchResp resp = GsonManager.getModel(response, MeasureSearchResp.class);
        if (resp == null || resp.getResponse_code() != 1) {
            resetOffset();
            return;
        }

        if (mOffset == 0) {
            if (resp.getList() == null || resp.getList().size() == 0) {
                ((MeasureSearchActivity) mContext).showNone();
            } else {
                ((MeasureSearchActivity) mContext).showNotice(mCurKeywords, resp.getTotal());
            }
            mList = resp.getList();
            showContent(mList);

            // Umeng
            UmengManager.onEvent(mContext, "Searchlist");
        } else {
            if (resp.getList() == null || resp.getList().size() == 0) {
                ((MeasureSearchActivity) mContext).showNoMoreToast();
                resetOffset();
            } else {
                mList.addAll(resp.getList());
                showLoadMore(mList);
            }
        }

    }

    private void showLoadMore(List<MeasureSearchResp.SearchItemBean> list) {
        if (!(mContext instanceof MeasureSearchActivity)) return;
        ((MeasureSearchActivity) mContext).showLoadMore(list);
    }

    private void showContent(List<MeasureSearchResp.SearchItemBean> list) {
        if (!(mContext instanceof MeasureSearchActivity)) return;
        ((MeasureSearchActivity) mContext).showContent(list);
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
        if (mContext instanceof MeasureSearchActivity) {
            ((MeasureSearchActivity) mContext).hideLoading();
            ((MeasureSearchActivity) mContext).stopXListView();
        }
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
