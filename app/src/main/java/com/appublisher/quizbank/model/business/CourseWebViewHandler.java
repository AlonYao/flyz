package com.appublisher.quizbank.model.business;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 课程中心WebViewHandler
 */
public class CourseWebViewHandler {

    /**
     * 捕获WebView中的动作
     * @param data WebView传递过来的数据
     */
    @JavascriptInterface
    public void webToAndroid(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            String type = jsonObject.optString("type", "");
            String orderId = jsonObject.optString("orderId", "");
        } catch (JSONException e) {
            // Empty
        }
    }

    /**
     * 通知WebView
     */
    public static void androidToWeb() {
        // Empty
    }
}
