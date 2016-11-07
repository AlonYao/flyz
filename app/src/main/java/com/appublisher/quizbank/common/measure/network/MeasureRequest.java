package com.appublisher.quizbank.common.measure.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;

/**
 * 做题模块
 */

public class MeasureRequest extends Request implements MeasureApi{

    public static final String AUTO_TRAINING = "auto_training";

    public MeasureRequest(Context context) {
        super(context);
    }

    public MeasureRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return LoginParamBuilder.finalUrl(url);
    }

    /**
     * 快速智能练习
     */
    public void getAutoTraining() {
        asyncRequest(getFinalUrl(getAutoTraining), "auto_training", "object");
    }

}
