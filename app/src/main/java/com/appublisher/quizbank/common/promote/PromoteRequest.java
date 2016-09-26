package com.appublisher.quizbank.common.promote;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

/**
 * 国考公告解读宣传
 */
public class PromoteRequest extends Request implements PromoteApi{

    public static final String PROMOTE_DATA = "promote_data";
    public static final String OBJECT = "object";

    public PromoteRequest(Context context) {
        super(context);
    }

    public PromoteRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return ParamBuilder.finalUrl(url);
    }

    /**
     * 获取数据
     */
    public void getPromoteData() {
        asyncRequest(getFinalUrl(getPromoteData), PROMOTE_DATA, OBJECT);
    }

}
