package com.appublisher.quizbank.common.offline.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;

public class OfflineWebViewActivity extends AppCompatActivity{

    private WebView mWebView;
    private RelativeLayout mProgressBar;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_web_view);

        // fetch data
        mUrl = getIntent().getStringExtra("url");
        String barTitle = getIntent().getStringExtra("bar_title");

        // Bar Title
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, barTitle);

        // init view
        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (RelativeLayout) findViewById(R.id.progressbar);

        // 设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showWebView(mUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 展示WebView
     * @param url url
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void showWebView(String url) {
        if (url == null || url.length() == 0) return;

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl(url);

        // 解决部分安卓机不弹出alert
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}
