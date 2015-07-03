package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.QaResp;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

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

        ProgressDialogManager.showProgressDialog(this, true);
        new Request(this, this).getQa();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("QaActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("QaActivity");
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

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        QaResp qaResp = gson.fromJson(response.toString(), QaResp.class);

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
