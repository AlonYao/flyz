package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.QaResp;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 常见问题
 */
public class QaActivity extends BaseActivity implements RequestCallback {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        // ToolBar
       setToolBar(this);

        // view初始化
        mWebView = (WebView) findViewById(R.id.qa_webview);
        mWebView.setBackgroundColor(0);

        ProgressDialogManager.showProgressDialog(this, true);
        new QRequest(this, this).getQa();
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
        if (response == null) return;

        QaResp qaResp = GsonManager.getModel(response.toString(), QaResp.class);

        if (qaResp == null || qaResp.getResponse_code() != 1) return;

        String content = qaResp.getContent();

        String ext = "<style type='text/css'>html, body {width:100%;height: 100%;margin: " +
                "0px;padding: 0px;color:#262B2D}</style>";

        mWebView.loadDataWithBaseURL(null, ext + content, "text/html", "UTF-8", null);

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
}
