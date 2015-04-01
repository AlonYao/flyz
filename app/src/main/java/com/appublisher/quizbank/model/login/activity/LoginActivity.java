package com.appublisher.quizbank.model.login.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.GuestDetaiModel;
import com.appublisher.quizbank.model.login.model.netdata.GuestInfoModel;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.HomeWatcher;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
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
import java.util.HashMap;

/**
 * 登录注册Activity
 */
public class LoginActivity extends ActionBarActivity implements RequestCallback{

    private static final int LOGIN_SUCCESS = 1;

    private Gson mGson;
    private HomeWatcher mHomeWatcher;
    private Handler mHandler;
    private static String mFrom;

    public Request mRequest;
    public UMSocialService mController;
    public String mSocialLoginType;

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
                        ToastManager.showToast(activity, "登录成功");

                        if (mFrom != null && mFrom.equals("splash")) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);
                        } else if (mFrom != null && mFrom.equals("collect")) {
                            Globals.is_fromGuestToUser = true;
                        }

                        activity.finish();

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

        // view初始化
        Button btnLogin = (Button) findViewById(R.id.login_btn);
        Button btnRegister = (Button) findViewById(R.id.login_register);
        ImageButton weixinBtn = (ImageButton) findViewById(R.id.login_weixin);
        ImageButton weiboBtn = (ImageButton) findViewById(R.id.login_weibo);
        TextView tvForgetPwd = (TextView) findViewById(R.id.login_forgetpwd);
        final EditText etUsername = (EditText) findViewById(R.id.login_username);
        final EditText etPassword = (EditText) findViewById(R.id.login_password);

        // 成员变量初始化
        mRequest = new Request(this, this);
        mGson = new Gson();
        LoginModel mLoginModel = new LoginModel(this);
        mHomeWatcher = new HomeWatcher(this);
        mHandler = new MsgHandler(this);

        // 获取数据
        mFrom = getIntent().getStringExtra("from");
        if (mFrom == null) mFrom = ""; // 设置默认值

        // ActionBar
        if (mFrom.equals("collect") || mFrom.equals("mine") || mFrom.equals("setting")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 登录按钮
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty()) {
                    ToastManager.showToast(LoginActivity.this,
                            getString(R.string.login_error_username));
                } else if (password.isEmpty()) {
                    ToastManager.showToast(LoginActivity.this,
                            getString(R.string.login_error_password));
                } else {
                    String pwdEncrypt = LoginModel.encrypt(password, "appublisher");
                    if (!pwdEncrypt.isEmpty()) {
                        ProgressDialogManager.showProgressDialog(LoginActivity.this);
                        mRequest.login(ParamBuilder.loginParams("0", username, "", pwdEncrypt));
                    }
                }
            }
        });

        // 注册按钮
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 忘记密码
        tvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 友盟
                sendToUmeng("Forget");

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
        mController.getConfig().setSinaCallbackUrl("http://www.sina.com");
        weiboBtn.setOnClickListener(mLoginModel.weiboOnClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Home键监听
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {

            @Override
            public void onHomePressed() {
                // 友盟统计
                sendToUmeng("Quit");
            }

            @Override
            public void onHomeLongPressed() {
                // Do Nothing
            }
        });
        mHomeWatcher.startWatch();

        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Home键监听
        mHomeWatcher.stopWatch();

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
            sendToUmeng("Quit");

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    /**
     * 设置登录成功后的操作
     * @param uim 用户个人信息
     * @param ueim 用户考试信息
     */
    @SuppressLint("CommitPrefEdits")
    private void setLoginSuccess(UserInfoModel uim, UserExamInfoModel ueim) {
        // 保存用户信息至数据库
        UserDAO.save(mGson.toJson(uim), mGson.toJson(ueim));

        // 本地缓存
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putString("user_id", uim.getUser_id());
        editor.putString("guest_id", "");
        editor.putString("user_token", uim.getUser_token());
        editor.putBoolean("is_login", true);
        editor.commit();

        // 页面跳转
        mHandler.sendEmptyMessage(LOGIN_SUCCESS);
    }

    /**
     * 友盟
     * @param action 动作
     */
    private void sendToUmeng(String action) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", action);
        MobclickAgent.onEvent(this, Globals.umeng_login_event, map);
        Globals.umeng_quiz_lastevent = Globals.umeng_login_event;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response != null) {
            if (apiName.equals("login")) {
                LoginResponseModel lrm = mGson.fromJson(response.toString(),
                        LoginResponseModel.class);

                if (lrm != null && lrm.getResponse_code() == 1) {
                    UserInfoModel uim = lrm.getUser();
                    UserExamInfoModel ueim = lrm.getExam();

                    if (uim != null) {
                        String user_id = uim.getUser_id();
                        if (user_id != null && !user_id.equals("")) {
                            // 友盟
                            sendToUmeng("Login");

                            // 新建||切换数据库
                            LoginModel.setDatabase(user_id, this);
                            // 执行成功后的操作
                            setLoginSuccess(uim, ueim);
                        }
                    }
                } else if (lrm != null && lrm.getResponse_code() == 1106) {
                    ToastManager.showToast(this, lrm.getResponse_msg());
                } else {
                    ToastManager.showToast(this, "登录失败");
                }
            }

            if (apiName.equals("social_login")) {
                LoginResponseModel lrm = mGson.fromJson(response.toString(),
                        LoginResponseModel.class);
                if (lrm != null && lrm.getResponse_code() == 1) {
                    UserInfoModel uim = lrm.getUser();
                    UserExamInfoModel ueim = lrm.getExam();

                    if (uim != null) {
                        boolean is_new = lrm.isIs_new();
                        String user_id = uim.getUser_id();
                        if (is_new) {
                            // 从游客库切换至用户库
                            LoginModel.migrateGuestToUser(this, user_id);
                        } else {
                            LoginModel.setDatabase(user_id, this);
                        }

                        // 友盟
                        sendToUmeng(mSocialLoginType);

                        // 执行成功后的操作
                        setLoginSuccess(uim, ueim);
                    }
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
        ToastManager.showToast(this, "你的网络不给力");
    }
}
