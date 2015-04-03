package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 常见问题
 */
public class QaActivity extends ActionBarActivity implements RequestCallback{

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        // ToolBar
        CommonModel.setToolBar(this);

        // view初始化
        mWebView = (WebView) findViewById(R.id.qa_webview);
        mWebView.setBackgroundColor(0);

        ProgressDialogManager.showProgressDialog(this);
        new Request(this, this).getQa();
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
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        if (response == null || response.length() == 0) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if (apiName.equals("qa")) {
            JSONObject qa = response.optJSONObject(0);
            if (qa != null) {
                String content = qa.optString("content");

                String ext = "<style type='text/css'>html, body {width:100%;height: 100%;margin: " +
                        "0px;padding: 0px;color:#262B2D}</style>";

                mWebView.loadDataWithBaseURL(null, ext + content, "text/html", "UTF-8", null);
            }
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
