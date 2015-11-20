package com.appublisher.quizbank.model.business;

import android.webkit.JavascriptInterface;

import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 课程中心WebViewHandler
 */
public class CourseWebViewHandler {


    /**
     * 捕获WebView中的动作
     *
     * @param data WebView传递过来的数据
     */
    @JavascriptInterface
    public void webToAndroid(String data) {
        try {
            Logger.i("webData" + data);
            JSONObject jsonObject = new JSONObject(data);
            String type = jsonObject.optString("payType", "");
            String orderId = jsonObject.optString("orderID", "");
            WebViewActivity.orderID = orderId;
            //aliPay,wxPay
            if (type.equals("wxPay")) {
                WebViewActivity.mRequest.getWeiXinPayEntity(orderId);
            } else if (type.equals("aliPay")) {
                WebViewActivity.mRequest.getAliPayUrl(orderId);
            }
        } catch (JSONException e) {
            // Empty
        }
    }

    /**
     * 通知WebView
     */
    public static void androidSendDataToWeb() {
        JSONObject object = new JSONObject();
        try {
            object.put("orderID", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = object.toString();
        WebViewActivity.mWebView.loadUrl("javascript:androidToWeb(" + str + ")");
        Logger.i(object.toString());
    }

    @JavascriptInterface
    public String getMyJSONData() {
        JSONObject object = new JSONObject();
        try {
            object.put("orderID", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str = object.toString();
        return str;
    }
}
