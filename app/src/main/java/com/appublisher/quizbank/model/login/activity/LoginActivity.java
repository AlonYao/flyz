package com.appublisher.quizbank.model.login.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.ExamChangeActivity;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * 登录注册Activity
 */
public class LoginActivity extends ActionBarActivity implements RequestCallback{

    public static final int LOGIN_SUCCESS = 1;
    private HomeWatcher mHomeWatcher;
    public Handler mHandler;
    public Request mRequest;
    public UMSocialService mController;
    public String mSocialLoginType;
    public String mUsername;
    public String mPwdEncrypt;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case LOGIN_SUCCESS:
                        if (!LoginModel.hasExamInfo()) {
                            Intent intent = new Intent(activity, ExamChangeActivity.class);
                            intent.putExtra("from", "login");
                            activity.startActivity(intent);
                        } else {
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);
                        }

                        activity.finish();
                        ToastManager.showToast(activity, "登录成功");

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
        setContentView(R.layout.login_activity_login);

        CommonModel.setToolBar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // 禁止键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // view初始化
        Button btnLogin = (Button) findViewById(R.id.login_btn);
        ImageButton weixinBtn = (ImageButton) findViewById(R.id.login_weixin);
        ImageButton weiboBtn = (ImageButton) findViewById(R.id.login_weibo);
        TextView tvForgetPwd = (TextView) findViewById(R.id.login_forgetpwd);
        final EditText etUsername = (EditText) findViewById(R.id.login_username);
        final EditText etPassword = (EditText) findViewById(R.id.login_password);

        // 成员变量初始化
        mRequest = new Request(this, this);
        mHomeWatcher = new HomeWatcher(this);
        mHandler = new MsgHandler(this);
        LoginModel mLoginModel = new LoginModel(this);
        LoginModel.mPwdErrorCount = 0;

        // 获取数据
        String from = getIntent().getStringExtra("from");
        if (from == null) from = ""; // 设置默认值

        // ActionBar
        if (from.equals("collect") || from.equals("mine") || from.equals("setting")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 登录按钮
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (mUsername.isEmpty()) {
                    ToastManager.showToast(LoginActivity.this,
                            getString(R.string.login_error_username));
                } else if (password.isEmpty()) {
                    ToastManager.showToast(LoginActivity.this,
                            getString(R.string.login_error_password));
                } else {
                    mPwdEncrypt = LoginModel.encrypt(password, "appublisher");
                    if (!mPwdEncrypt.isEmpty()) {
                        ProgressDialogManager.showProgressDialog(LoginActivity.this, false);
                        mRequest.isUserExists(mUsername);
                    }
                }
            }
        });

        // 忘记密码
        tvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 友盟
                UmengManager.sendCountEvent(LoginActivity.this, "RegLog", "Action", "Forget");

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("from", "forget_pwd");
                startActivity(intent);
            }
        });

        // 第三方登录
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");

        // 微信
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, getString(R.string.weixin_appid),
                getString(R.string.weixin_secret));
        wxHandler.addToSocialSDK();
        weixinBtn.setOnClickListener(mLoginModel.weixinOnClick);

        // 微博 设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSinaCallbackUrl("http://sns.whalecloud.com/sina2/callback");
        weiboBtn.setOnClickListener(mLoginModel.weiboOnClick);

        // 保存Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                UmengManager.sendCountEvent(LoginActivity.this, "RegLog", "Action", "Quit");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

        // Umeng
        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

        // Umeng
        MobclickAgent.onPageEnd("LoginActivity");
        MobclickAgent.onPause(this);
        TCAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            // 友盟统计
            UmengManager.sendCountEvent(this, "RegLog", "Action", "Quit");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        LoginModel.dealResp(response, apiName, this);
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
        ToastManager.showToast(this, "你的网络不给力");
    }
}
