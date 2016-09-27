package com.appublisher.quizbank.common.promote;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * 国考公告解读宣传
 */
public class PromoteModel implements RequestCallback{

    private static final String YAOGUO_PROMOTE = "yaoguo_promote";
    private static final String PARAM_DATE = "date";

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

    public boolean isShow() {
        SharedPreferences sp =
                mContext.getSharedPreferences(YAOGUO_PROMOTE, Context.MODE_PRIVATE);
        String updateDate = sp.getString(PARAM_DATE, "");
        if (updateDate.length() == 0) {
            // 首次，直接显示
            return true;
        } else {
            // 当天显示过一次后不显示，第二天再显示
            Date preDate = Utils.stringToDate(updateDate);
            Date curDate = Utils.getCurDate();
            long interval = Utils.getDateInterval(preDate, curDate);
            return interval >= 1;
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

    /** get & set **/

    public PromoteRequest getRequest() {
        return mRequest;
    }

    public Context getContext() {
        return mContext;
    }
}
