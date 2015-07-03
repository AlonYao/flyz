package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

/**
 * 通知详情
 */
public class NoticeDetailActivity extends ActionBarActivity {

    private RelativeLayout mProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        TextView textView = (TextView) findViewById(R.id.notice_text);
        WebView webView = (WebView) findViewById(R.id.notice_url);
        mProgressBar = (RelativeLayout) findViewById(R.id.progressbar);

        String type = getIntent().getStringExtra("type");
        String content = getIntent().getStringExtra("content");

        if ("text".equals(type)) {
            textView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

            textView.setText(content);
        } else if ("url".equals(type)) {
            textView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.VISIBLE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(content);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("NoticeDetailActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("NoticeDetailActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
