package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.pay.PayConstants;
import com.appublisher.quizbank.common.pay.PayWebViewHandler;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.model.entity.umeng.UMShareContentEntity;
import com.appublisher.quizbank.model.entity.umeng.UMShareUrlEntity;
import com.appublisher.quizbank.model.entity.umeng.UmengShareEntity;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;

/**
 * WebView
 */
public class WebViewActivity extends ActionBarActivity implements RequestCallback {

    private static RelativeLayout mProgressBar;
    public static WebView mWebView;
    private String mFrom;
    private String mUrl;
    private HomeWatcher mHomeWatcher;
    public static Request mRequest;
    private static String mOpencourseId;
    private long mUmengTimestamp;
    private String mUmengEntry;
    public String mUmengQQ;
    public Handler mHandler;
    public LinearLayout mLlOpenCourseConsult;
    public TextView mTvOpenCourseConsult;
    public Timer mTimer;
    public boolean mHasShowOpenCourseConsult;
    public boolean mIsFromQQ;
    public static final int TIME_ON = 10;
    private String barTitle;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final WebViewActivity activity = (WebViewActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIME_ON:
                        mRequest.getOpenCourseConsult(mOpencourseId);
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
        mLlOpenCourseConsult = (LinearLayout) findViewById(R.id.opencourse_consult_ll);
        mTvOpenCourseConsult = (TextView) findViewById(R.id.opencourse_consult_tv);

        // 成员变量初始化
        mHandler = new MsgHandler(this);
        mRequest = new Request(this, this);
        mHasShowOpenCourseConsult = false;
        mUmengQQ = "0";
        mHomeWatcher = new HomeWatcher(this);
        mIsFromQQ = false;

        // 获取数据
        mUrl = getIntent().getStringExtra("url");
        mFrom = getIntent().getStringExtra("from");
        mOpencourseId = getIntent().getStringExtra("content");
        mUmengTimestamp = getIntent().getLongExtra("umeng_timestamp", 0);
        if (mUmengTimestamp == 0) mUmengTimestamp = System.currentTimeMillis();
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        barTitle = getIntent().getStringExtra("bar_title");

        // 设置Bar Name
        CommonModel.setBarTitle(this, barTitle == null ? "" : barTitle);

        // 设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load Url
        if (!mIsFromQQ) {
            if ("opencourse_started".equals(mFrom)) {
                ProgressDialogManager.showProgressDialog(this, true);
                mRequest.getOpenCourseUrl(mOpencourseId);
            } else if ("opencourse_pre".equals(mFrom)) {
                showWebView(mUrl
                        + "&user_id=" + LoginModel.getUserId()
                        + "&user_token=" + LoginModel.getUserToken()
                        + "&timestamp=" + System.currentTimeMillis());
            } else if ("course".equals(mFrom) && PayConstants.mIsPaySuccess) {
                String url = "http://dev.m.zhiboke.net/index.html#/live/ordersuccess?order_num="
                        + PayConstants.mOrderID;
                mWebView.loadUrl(url);
                PayConstants.mIsPaySuccess = false;
            } else {
                showWebView(mUrl);
            }
        }

        // 重置状态
        mIsFromQQ = false;

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                if (mWebView != null) mWebView.loadUrl("");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

        // Umeng
        MobclickAgent.onPageStart("WebViewActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

        // Umeng
        MobclickAgent.onPageEnd("WebViewActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        if ("opencourse_started".equals(mFrom)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("Entry", mUmengEntry);
            map.put("EnterLive", "1");
            map.put("QQ", mUmengQQ);
            long dur = System.currentTimeMillis() - mUmengTimestamp;
            UmengManager.sendComputeEvent(this, "OnAir", map, (int) (dur / 1000));
        }

        // 关闭定时器
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mWebView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null) mWebView.loadUrl("");
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("刷新").setIcon(
                R.drawable.webview_refresh), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if ("opencourse_started".equals(mFrom)) {
            MenuItemCompat.setShowAsAction(menu.add("咨询"),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        if ("course".equals(mFrom)) {
            MenuItemCompat.setShowAsAction(menu.add("分享").setIcon(R.drawable.quiz_share),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWebView != null) mWebView.loadUrl("");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            finish();
        } else if ("刷新".equals(item.getTitle())) {
            mWebView.reload();
        } else if ("咨询".equals(item.getTitle())) {
            // Umeng
            mUmengQQ = "1";

            OpenCourseModel.setMarketQQ(this);
        } else if ("分享".equals(item.getTitle())) {
            /** 构造友盟分享实体 **/
            UmengShareEntity umengShareEntity = new UmengShareEntity();
            umengShareEntity.setActivity(this);
            umengShareEntity.setContent("听说上过" + barTitle + ",一口气上岸不费劲儿～");
            umengShareEntity.setFrom("course_detail");

            // 友盟分享文字处理
            UMShareContentEntity umShareContentEntity = new UMShareContentEntity();
            umShareContentEntity.setType("course_detail");
            umShareContentEntity.setExamName(barTitle);

            // 友盟分享跳转链接处理
            UMShareUrlEntity urlEntity = new UMShareUrlEntity();
            urlEntity.setType("course_detail");
            int course_id = getIntent().getIntExtra("course_id", -1);
            urlEntity.setCourse_id(course_id);
            umengShareEntity.setUrl(UmengManager.getUrl(urlEntity));
            UmengManager.openShare(umengShareEntity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("open_course_url".equals(apiName))
            OpenCourseModel.dealOpenCourseUrlResp(this, response);

        if ("open_course_consult".equals(apiName))
            OpenCourseModel.dealOpenCourseConsultResp(this, response);

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
     *
     * @param url url
     */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void showWebView(String url) {
        if (url == null) return;

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl(url);

        if ("course".equals(mFrom)) {
            mWebView.addJavascriptInterface(new PayWebViewHandler(this), "handler");
        }

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
