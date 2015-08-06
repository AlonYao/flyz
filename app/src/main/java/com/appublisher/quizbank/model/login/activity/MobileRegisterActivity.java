package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 手机号注册
 */
public class MobileRegisterActivity extends ActionBarActivity
        implements View.OnClickListener, RequestCallback{

    private EditText mEtMobile;
    private EditText mEtPwd;
    private Request mRequest;
    private String mMobile;
    private String mPwdEncrypt;
    private int mPwdErrorCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_register);

        // ActionBar
        CommonModel.setToolBar(this);

        // 成员变量初始化
        mRequest = new Request(this, this);
        mPwdErrorCount = 0;

        // View 初始化
        mEtMobile = (EditText) findViewById(R.id.mobile_register_num);
        mEtPwd = (EditText) findViewById(R.id.mobile_register_pwd);
        Button button = (Button) findViewById(R.id.mobile_register_next);

        button.setOnClickListener(this);

        // 保存Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("登录"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getTitle().equals("登录")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobile_register_next:
                mMobile = mEtMobile.getText().toString();
                String pwd = mEtPwd.getText().toString();

                if (mMobile.isEmpty()) {
                    ToastManager.showToast(this, "手机号为空");
                } else if (pwd.isEmpty()) {
                    ToastManager.showToast(this, "密码为空");
                } else if (pwd.length() < 6 || pwd.length() > 16) {
                    ToastManager.showToast(this, "密码长度为6-16位");
                } else {
                    mPwdEncrypt = LoginModel.encrypt(pwd, "appublisher");
                    if (!mPwdEncrypt.isEmpty()) {
                        ProgressDialogManager.showProgressDialog(this, false);
                        mRequest.isUserExists(mMobile);
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();

        if ("is_user_exists".equals(apiName)) {
            // 检测用户是否注册 接口
            IsUserExistsResp isUserExistsResp =
                    Globals.gson.fromJson(response.toString(), IsUserExistsResp.class);

            if (isUserExistsResp != null && isUserExistsResp.getResponse_code() == 1
                    && isUserExistsResp.isUser_exists()) {
                // 用户已注册
                mRequest.login(ParamBuilder.loginParams("0", mMobile, "", mPwdEncrypt));
            } else {
                // 用户未注册
                mRequest.getSmsCode(ParamBuilder.phoneNumParams(mMobile, ""));

                Intent intent = new Intent(this, RegisterSmsCodeActivity.class);
                intent.putExtra("user_phone", mMobile);
                intent.putExtra("user_pwd", mPwdEncrypt);
                startActivity(intent);

                ProgressDialogManager.closeProgressDialog();
            }
        } else if ("login".equals(apiName)) {
            // 登录接口
            LoginResponseModel lrm = Globals.gson.fromJson(
                    response.toString(), LoginResponseModel.class);

            if (lrm == null || lrm.getResponse_code() != 1) {
                if (mPwdErrorCount == 0) {
                    ToastManager.showToast(this, "手机号已存在，密码不正确");
                    mPwdErrorCount++;
                } else if (mPwdErrorCount == 1) {
                    LoginModel.showForgetPwdAlert(this, mMobile);
                }
            } else {
                // 执行成功后的操作
                if (LoginModel.saveToLocal(lrm, this)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    ToastManager.showToast(this, "手机号已注册");
                } else {
                    ToastManager.showToast(this, "数据异常");
                }
            }

            ProgressDialogManager.closeProgressDialog();
        } else {
            ProgressDialogManager.closeProgressDialog();
        }
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
