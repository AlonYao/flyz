package com.appublisher.quizbank.common.pay;

import com.appublisher.quizbank.network.ApiConstants;

/**
 * 支付模块Api
 */
public interface PayApiConstants extends ApiConstants{

    //获取微信支付
    String getWXPayUrl = baseUrl + "payment/get_wxpay_params";

    //获取支付宝支付
    String getAliPayUrl = baseUrl + "payment/get_alipay_params";

}
