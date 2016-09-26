package com.appublisher.quizbank.common.promote;

import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 国考公告解读宣传
 */
public class PromoteModel implements RequestCallback{

    private Context mContext;
    private PromoteRequest mRequest;
    private PromoteDataListener mListener;

    public interface PromoteDataListener {
        void onComplete(boolean success, PromoteResp resp);
    }

    public PromoteModel(Context context) {
        mContext = context;
        mRequest = new PromoteRequest(context, this);
    }

    public void getPromoteData(PromoteDataListener listener) {
        mListener = listener;
        mRequest.getPromoteData();
    }

    private void dealPromoteDataResp(JSONObject response) {
        PromoteResp resp = GsonManager.getModel(response, PromoteResp.class);
        if (resp == null || resp.getResponse_code() != 1) {
            // 异常处理
            mListener.onComplete(false, resp);
        } else {
            mListener.onComplete(true, resp);
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (PromoteRequest.PROMOTE_DATA.equals(apiName)) {
            dealPromoteDataResp(response);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        // 异常处理
        mListener.onComplete(false, null);
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        // 异常处理
        mListener.onComplete(false, null);
    }

    public PromoteRequest getRequest() {
        return mRequest;
    }
}
