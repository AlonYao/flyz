package com.appublisher.quizbank.common.pay;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.common.pay.ali.AliPay;
import com.appublisher.quizbank.common.pay.weixin.WeiXinPay;
import com.appublisher.quizbank.common.pay.weixin.WeiXinPayEntity;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 课程中心WebViewHandler
 */
public class PayWebViewHandler implements RequestCallback{

    private PayRequest mPayRequest;
    private Context mContext;

    public PayWebViewHandler(Context context) {
        this.mPayRequest = new PayRequest(context, this);
        this.mContext = context;
    }

    /**
     * 捕获WebView中的动作
     *
     * @param data WebView传递过来的数据
     */
    @JavascriptInterface
    public void webToAndroid(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String type = jsonObject.optString("payType", "");
            String orderId = jsonObject.optString("orderID", "");
            PayConstants.mOrderID = orderId;
            //aliPay,wxPay
            if (type.equals("wxPay")) {
                mPayRequest.getWeiXinPayEntity(orderId);
            } else if (type.equals("aliPay")) {
                mPayRequest.getAliPayUrl(orderId);
            }
        } catch (JSONException e) {
            // Empty
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) return;

        if ("wxPay".equals(apiName)) {
            WeiXinPayEntity weiXinPayEntity =
                    GsonManager.getObejctFromJSON(response.toString(), WeiXinPayEntity.class);
            WeiXinPay.pay(mContext, weiXinPayEntity);
        }

        if ("aliPay".equals(apiName)) {
            String response_code = response.optString("response_code");
            if (response_code.equals("1")) {
                String param_str = response.optString("param_str");
                AliPay.pay(param_str, (Activity) mContext);
            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
