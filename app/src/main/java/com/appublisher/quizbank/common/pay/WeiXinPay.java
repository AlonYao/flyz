package com.appublisher.quizbank.common.pay;

import android.content.Context;

import com.appublisher.quizbank.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 */
public class WeiXinPay {

    public static void pay(Context context) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, null);
        iwxapi.registerApp(context.getString(R.string.weixin_appid));
    }
}
