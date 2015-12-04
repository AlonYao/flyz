package com.appublisher.quizbank.common.pay;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

/**
 * 支付模块请求
 */
public class PayRequest extends Request implements PayApiConstants {

    public PayRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    /**
     * 获取微信支付回调
     *
     * @param order_id 公开课id
     */
    public void getWeiXinPayEntity(String order_id) {
        asyncRequest(ParamBuilder.finalUrl(getWXPayUrl) + "&order_id=" + order_id,
                "wxPay", "object");
    }

    /**
     * 获取支付宝支付回调
     *
     * @param order_id 公开课id
     */
    public void getAliPayUrl(String order_id) {
        asyncRequest(ParamBuilder.finalUrl(getAliPayUrl) + "&order_id=" + order_id,
                "aliPay", "object");
    }

}
