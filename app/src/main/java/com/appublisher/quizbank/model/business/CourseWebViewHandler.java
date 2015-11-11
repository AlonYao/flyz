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

//    private WebView mWebView;

//    public CourseWebViewHandler() {
//        mWebView = webView;
//    }

    @JavascriptInterface
    public void haha(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Logger.e(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e(data);

    }

    public static void sendDataToWebView() {
        String a = "aaa";
        WebViewActivity.mWebView.loadUrl("javascript:xxx('aaa')");

        Logger.e("11111111111111");
    }
}
