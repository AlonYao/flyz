package com.appublisher.quizbank.common.pay.weixin;

import android.content.Context;

import com.appublisher.quizbank.R;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 */
public class WeiXinPay {

    /**
     * 支付
     * @param context 上下文
     * @param entity 微信支付实体类
     */
    public static void pay(Context context, WeiXinPayEntity entity) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, null);
        iwxapi.registerApp(context.getString(R.string.weixin_appid));

        PayReq payReq = new PayReq();
        payReq.appId = entity.getAppId();
        payReq.partnerId = entity.getPartnerId();
        payReq.prepayId= entity.getPrepayId();
        payReq.packageValue = entity.getPackageValue();
        payReq.nonceStr= entity.getNonceStr();
        payReq.timeStamp= entity.getTimeStamp();
        payReq.sign= entity.getSign();

        iwxapi.sendReq(payReq);
    }
}
