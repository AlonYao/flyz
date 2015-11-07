package com.appublisher.quizbank.common.offline.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.utils.FileManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;
import com.appublisher.quizbank.utils.http.HttpManager;
import com.appublisher.quizbank.utils.http.IHttpListener;
import com.coolerfall.download.DownloadListener;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.duobeiyun.DuobeiYunClient;

import java.io.File;
import java.io.IOException;

public class OfflineWebViewActivity extends AppCompatActivity implements IHttpListener{

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

        // 获取最新的播放器版本
        new HttpManager(this).execute(DuobeiYunClient.fetchLatetVersionUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    /**
     * 下载播放器
     */
    private void downloadPlayer(String version) {
        if (version == null || version.length() == 0) return;

        String playerUrl = DuobeiYunClient.getPlayerResourceUrl(version);
        final String dirPath =
                Environment.getExternalStorageDirectory().toString()
                        + "/duobeiyun/play/";
        String fileName = playerUrl.substring(playerUrl.lastIndexOf("/") + 1, playerUrl.length());

        ProgressDialogManager.showProgressDialog(this);
        ToastManager.showToast(this, "更新必要文件中……");

        final DownloadManager manager = new DownloadManager();
        DownloadRequest request = new DownloadRequest()
                .setUrl(playerUrl)
                .setDestFilePath(dirPath + fileName)
                .setRetryTime(100)
                .setDownloadListener(new DownloadListener() {
                    @Override
                    public void onStart(int downloaduodId, long totalBytes) {
                        // 空间不足提示
                        if (totalBytes > Utils.getAvailableSDCardSize()) {
                            manager.cancelAll();
                            ToastManager.showToast(
                                    OfflineWebViewActivity.this, "手机可用存储空间不足");
                            ProgressDialogManager.closeProgressDialog();
                            showWebView(mUrl);
                        }
                    }

                    @Override
                    public void onRetry(int downloadId) {
                        // Empty
                    }

                    @Override
                    public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
                        // Empty
                    }

                    @Override
                    public void onSuccess(int downloadId, String filePath) {
                        // 解压缩播放器地址
                        try {
                            FileManager.unzip(new File(filePath), new File(dirPath));
                            FileManager.deleteFiles(filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ToastManager.showToast(OfflineWebViewActivity.this, "更新完成");
                        ProgressDialogManager.closeProgressDialog();
                        showWebView(mUrl);
                    }

                    @Override
                    public void onFailure(int downloadId, int statusCode, String errMsg) {
                        ToastManager.showToast(OfflineWebViewActivity.this, "更新失败");
                        ProgressDialogManager.closeProgressDialog();
                        showWebView(mUrl);
                    }
                });

        manager.add(request);
    }

    @Override
    public void onResponse(String response) {
        if (response == null) return;

        String curVersion = "0.0";

        try {
            curVersion = DuobeiYunClient.fetchCurrentVersionUrl();
        } catch (Exception e) {
            // Empty
        }

        try {
            int cur = Integer.parseInt(curVersion.substring(0, curVersion.indexOf(".")));
            int latest = Integer.parseInt(response.substring(0, response.indexOf(".")));

            if (latest > cur) {
                downloadPlayer(response);
            } else {
                showWebView(mUrl);
            }
        } catch (Exception e) {
            if (!"0.0".equals(curVersion)) {
                // 如果出现异常，只能是获取版本的接口出现问题，那么如果本地有旧版播放器，则直接播放。
                showWebView(mUrl);
            }
        }
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
