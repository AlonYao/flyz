package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 找回密码
 */
public class ForgetPwdActivity extends ActionBarActivity implements
        View.OnClickListener, RequestCallback{

    private String mUserName;
    private EditText mEtUserName;
    private Request mRequest;
    private int mType;

    private static final int EMAIL = 10;
    private static final int PHONE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);

        // set bar
        CommonModel.setToolBar(this);

        // View 初始化
        Button btnNext = (Button) findViewById(R.id.forgetpwd_next);
        mEtUserName = (EditText) findViewById(R.id.forgetpwd_username);

        // 成员变量初始化
        mRequest = new Request(this, this);

        btnNext.setOnClickListener(this);

        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetpwd_next:
                mUserName = mEtUserName.getText().toString();
                if (mUserName.isEmpty()) return;

                if (Utils.isEmail(mUserName)) {
                    // 邮箱用户
                    mType = EMAIL;
                } else {
                    // 非邮箱用户，默认是手机号用户
                    mType = PHONE;
                    // Umeng
                    UmengManager.sendCountEvent(
                            ForgetPwdActivity.this, "CodeReq", "Type", "Forget");
                }

                ProgressDialogManager.showProgressDialog(ForgetPwdActivity.this, true);
                mRequest.isUserExists(mUserName);

                break;
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if ("is_user_exists".equals(apiName)) {
            if (Globals.gson == null) Globals.gson = GsonManager.initGson();
            IsUserExistsResp isUserExistsResp =
                    Globals.gson.fromJson(response.toString(), IsUserExistsResp.class);

            if (isUserExistsResp != null && isUserExistsResp.getResponse_code() == 1
                    && isUserExistsResp.isUser_exists()) {
                // 用户存在
                switch (mType) {
                    case EMAIL:
                        mRequest.resetPassword(mUserName);
                        Intent intent = new Intent(this, EmailResetPwdActivity.class);
                        intent.putExtra("user_email", mUserName);
                        startActivity(intent);

                        break;

                    case PHONE:
                        mRequest.getSmsCode(ParamBuilder.phoneNumParams(mUserName, "resetPswd"));
                        intent = new Intent(this, MobileResetPwdActivity.class);
                        intent.putExtra("user_phone", mUserName);
                        startActivity(intent);

                        break;
                }
            } else {
                // 用户不存在
                ToastManager.showToast(this, "该用户不存在");
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
}
