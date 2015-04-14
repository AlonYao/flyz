package com.appublisher.quizbank.model.login.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 修改密码Activity
 */
public class PwdChangeActivity extends ActionBarActivity implements RequestCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_pwd_change);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        final EditText etPrePwd = (EditText) findViewById(R.id.password_change_pre);
        final EditText etNewPwd = (EditText) findViewById(R.id.password_change_new);
        Button btnChangePwd = (Button) findViewById(R.id.password_change_btn);

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prePwd = etPrePwd.getText().toString();
                String newPwd = etNewPwd.getText().toString();

                if (!prePwd.isEmpty() && !newPwd.isEmpty() && !prePwd.equals(newPwd)) {
                    String prePwdEncrypt = LoginModel.encrypt(prePwd, "appublisher");
                    String newPwdEncrypt = LoginModel.encrypt(newPwd, "appublisher");

                    if (!prePwdEncrypt.isEmpty() && !newPwdEncrypt.isEmpty()) {
                        ProgressDialogManager.showProgressDialog(PwdChangeActivity.this, false);
                        new Request(PwdChangeActivity.this, PwdChangeActivity.this).changePwd(
                                ParamBuilder.changePwd(prePwdEncrypt, newPwdEncrypt)
                        );
                    }
                } else {
                    ToastManager.showToast(PwdChangeActivity.this, "密码为空或者密码相同");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
        if (response != null) {

            Logger.i(response.toString());

            Gson gson = new Gson();
            if (apiName.equals("change_password")) {
                CommonResponseModel commonResponse = gson.fromJson(
                        response.toString(), CommonResponseModel.class);
                if (commonResponse != null && commonResponse.getResponse_code() == 1) {
                    finish();
                    ToastManager.showToast(this, "修改成功");
                } else {
                    ToastManager.showToast(this, "修改密码失败");
                }
            }
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
