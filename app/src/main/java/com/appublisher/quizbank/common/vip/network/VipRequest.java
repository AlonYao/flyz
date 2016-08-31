package com.appublisher.quizbank.common.vip.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.network.ParamBuilder;

import java.util.Map;

/**
 * 小班模块
 */
public class VipRequest extends Request implements VipApi {

    public VipRequest(Context context) {
        super(context);
    }

    public VipRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return ParamBuilder.finalUrl(url);
    }

    /**
     * 提交作业
     */
    public void submit(Map<String, String> params) {
        postRequest(getFinalUrl(submit), params, "submit", "object");
    }

}
