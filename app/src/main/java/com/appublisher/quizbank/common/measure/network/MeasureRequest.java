package com.appublisher.quizbank.common.measure.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;

/**
 * 做题模块
 */

public class MeasureRequest extends Request{

    public MeasureRequest(Context context) {
        super(context);
    }

    public MeasureRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

}
