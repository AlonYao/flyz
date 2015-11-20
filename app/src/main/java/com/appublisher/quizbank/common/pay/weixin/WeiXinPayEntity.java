package com.appublisher.quizbank.common.pay.weixin;

/**
 * 微信支付实体类（用于调起支付）
 */
public class WeiXinPayEntity {

    String appid;
    String partnerid;
    String prepayid;
    String package_value;
    String noncestr;
    String timestamp;
    String sign;

    public String getAppId() {
        return appid;
    }

    public String getPartnerId() {
        return partnerid;
    }

    public String getPrepayId() {
        return prepayid;
    }

    public String getPackageValue() {
        return package_value;
    }

    public String getNonceStr() {
        return noncestr;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }
}
