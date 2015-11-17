package com.appublisher.quizbank.common.pay.weixin;

/**
 * 微信支付实体类（用于调起支付）
 */
public class WeiXinPayEntity {

    String appId;
    String partnerId;
    String prepayId;
    String packageValue;
    String nonceStr;
    String timeStamp;
    String sign;

    public String getAppId() {
        return appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSign() {
        return sign;
    }
}
