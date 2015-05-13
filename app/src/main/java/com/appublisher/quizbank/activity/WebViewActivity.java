package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.OpenCourseModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * WebView
 */
public class WebViewActivity extends ActionBarActivity implements RequestCallback{

    private RelativeLayout mProgressBar;
    private WebView mWebView;
    private static Request mRequest;
    private static String mOpencourseId;

    public Handler mHandler;

    public static final int TIME_ON = 10;
    public static final int TIME_OUT = 11;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final MeasureActivity activity = (MeasureActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIME_ON:
                        mRequest.getOpenCourseConsult(mOpencourseId);
                        break;

                    case TIME_OUT:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (RelativeLayout) findViewById(R.id.progressbar);
        mHandler = new MsgHandler(this);

        // 成员变量初始化
        mRequest = new Request(this, this);

        // 获取数据
        String url = getIntent().getStringExtra("url");
        String from = getIntent().getStringExtra("from");
        mOpencourseId = getIntent().getStringExtra("content");

        if ("opencourse_started".equals(from)) {
            ProgressDialogManager.showProgressDialog(this, true);
            mRequest.getOpenCourseUrl(mOpencourseId);
        } else {
            showWebView(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("WebViewActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("WebViewActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("open_course_url".equals(apiName))
            OpenCourseModel.dealOpenCourseUrlResp(this, response);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    /**
     * 展示WebView
     * @param url url
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void showWebView(String url) {
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
