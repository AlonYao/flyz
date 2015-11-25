package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.pay.ali.AliPayResult;
import com.appublisher.quizbank.common.pay.weixin.WeiXinPay;
import com.appublisher.quizbank.common.pay.weixin.WeiXinPayEntity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.CourseWebViewHandler;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.Logger;
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
    public static String orderID;
    private long mUmengTimestamp;
    private String mUmengEntry;
    public String mUmengQQ;
    public Handler mHandler;
    public LinearLayout mLlOpenCourseConsult;
    public TextView mTvOpenCourseConsult;
    public Timer mTimer;
    public boolean mHasShowOpenCourseConsult;
    public boolean mIsFromQQ;
    private static final int SDK_PAY_FLAG = 1;
    public static final int TIME_ON = 10;
    public static boolean isPaySuccess = false;

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
                    case SDK_PAY_FLAG: {
                        AliPayResult aliPayResult = new AliPayResult((String) msg.obj);
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                        String resultInfo = aliPayResult.getResult();
                        String resultStatus = aliPayResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            Toast.makeText(activity, "支付成功",
                                    Toast.LENGTH_LONG).show();
                            isPaySuccess = true;
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                            // 最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(activity, "支付结果确认中",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(activity, "支付失败",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        break;
                    }
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
        String barTitle = getIntent().getStringExtra("bar_title");

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
        //判断是否是购买成功
        if (isPaySuccess) {
            String url = "http://dev.m.zhiboke.net/index.html#/live/ordersuccess?order_num=" + orderID;
            mWebView.loadUrl(url);
            isPaySuccess = false;
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("open_course_url".equals(apiName))
            OpenCourseModel.dealOpenCourseUrlResp(this, response);

        if ("open_course_consult".equals(apiName))
            OpenCourseModel.dealOpenCourseConsultResp(this, response);
        if ("wxPay".equals(apiName)) {
            Logger.i("wxPay=" + response.toString());
            WeiXinPayEntity weiXinPayEntity = GsonManager.getObejctFromJSON(response.toString(), WeiXinPayEntity.class);
//            WeiXinPay weiXinPay = new WeiXinPay(WebViewActivity.this, weiXinPayEntity);
            WeiXinPay.pay(WebViewActivity.this, weiXinPayEntity);
//            PayReq payReq = new PayReq();
//            payReq.appId = weiXinPayEntity.getAppId();
//            payReq.partnerId = weiXinPayEntity.getPartnerId();
//            payReq.prepayId = weiXinPayEntity.getPrepayId();
//            payReq.packageValue = weiXinPayEntity.getPackageValue();
//            payReq.nonceStr = weiXinPayEntity.getNonceStr();
//            payReq.timeStamp = weiXinPayEntity.getTimeStamp();
//            payReq.sign = weiXinPayEntity.getSign();
//            Logger.i(weiXinPayEntity.toString());
//            iwxapi.sendReq(payReq);
        }
        if ("aliPay".equals(apiName)) {
            Logger.i("aliPay=" + response.toString());
            String response_code = response.optString("response_code");
            if (response_code.equals("1")) {
                String param_str = response.optString("param_str");
                if (param_str != null && param_str != "") {
                    aliPay(param_str);
                }
            }
        }
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
    public static void showWebView(String url) {
//        url = "http://192.168.1.115/mobile_live_web/demo.html";
//        url = "http://192.168.1.115/mobile_live_web/yaoguoNotice/dailyplan.html";
        if (url == null) return;
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(new CourseWebViewHandler(), "handler");
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

    public void aliPay(final String payInfo) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(WebViewActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
