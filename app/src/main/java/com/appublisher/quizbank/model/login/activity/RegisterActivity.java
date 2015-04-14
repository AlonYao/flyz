package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机号注册&修改Activity
 */
public class RegisterActivity extends ActionBarActivity implements RequestCallback {

    private Request mRequest;
    private String mPhoneNum;
    private Timer mTimer;
    private Button mBtnGetSmsCode;
    private String mFrom;
    private String mType;

    private int mTimeLimit = 60;
    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case TIME_ON:
                    if (mBtnGetSmsCode != null && mTimeLimit != 0) {
                        mBtnGetSmsCode.setText("获取验证码(" + String.valueOf(mTimeLimit) + "秒)");
                    }

                    break;

                case TIME_OUT:
                    setTimeOut();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_register);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        mBtnGetSmsCode = (Button) findViewById(R.id.register_getsmscode_btn);
        ImageButton btnSubmitCode = (ImageButton) findViewById(R.id.register_smscode_btn);
        TextView tvCannotGet = (TextView) findViewById(R.id.register_cannotget);
        final EditText etPhone = (EditText) findViewById(R.id.register_phone_edittext);
        final EditText etSmsCode = (EditText) findViewById(R.id.register_smscode_edittext);

        // 成员变量初始化
        mRequest = new Request(RegisterActivity.this, RegisterActivity.this);

        // 获取数据
        mFrom = getIntent().getStringExtra("from");
        if (mFrom == null) mFrom = "";
        if (mFrom.equals("UserInfoActivity")) {
            mType = getIntent().getStringExtra("type");
            if (mType == null) mType = "";
            if (mType.equals("update")) getSupportActionBar().setTitle("更换手机号");
            if (mType.equals("add")) getSupportActionBar().setTitle("绑定手机号");
        } else if (mFrom.equals("forget_pwd")) {
            getSupportActionBar().setTitle("找回密码");
        }

        // 获取验证码按钮
        mBtnGetSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNum = etPhone.getText().toString();

                if (!mPhoneNum.isEmpty()) {
                    mBtnGetSmsCode.setBackgroundResource(R.drawable.login_button_wait);
                    mBtnGetSmsCode.setClickable(false);

                    if (mTimer != null) {
                        mTimer.cancel();
                    }

                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            mTimeLimit--;
                            mHandler.sendEmptyMessage(TIME_ON);
                            if (mTimeLimit < 0) {
                                mTimer.cancel();
                                mHandler.sendEmptyMessage(TIME_OUT);
                            }
                        }
                    }, 0, 1000);

                    if (mFrom.equals("forget_pwd")) {
                        mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "resetPswd"));
                    } else {
                        mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, ""));
                    }
                }
            }
        });

        // 校验验证码按钮
        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsCode = etSmsCode.getText().toString();
                mPhoneNum = etPhone.getText().toString();
                if (!smsCode.isEmpty() && !mPhoneNum.isEmpty()) {
                    ProgressDialogManager.showProgressDialog(RegisterActivity.this, false);
                    mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mPhoneNum, smsCode));
                } else {
                    ToastManager.showToast(RegisterActivity.this, "手机号或验证码为空");
                }
            }
        });

        tvCannotGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, CannotGetSmsActivity.class);
                startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                // 第三方登录绑定手机号的回调
                if (data != null) {
                    String user_info = data.getStringExtra("user_info");
                    if (user_info != null && !user_info.equals("")) {
                        ToastManager.showToast(this, "修改成功");
                        Intent intent = new Intent(this, UserInfoActivity.class);
                        intent.putExtra("user_info", user_info);
                        setResult(11, intent);
                        finish();
                    }
                }

                break;
        }
    }

    /**
     * 设置时间结束的操作
     */
    private void setTimeOut() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimeLimit = 60;
        mBtnGetSmsCode.setClickable(true);
        mBtnGetSmsCode.setBackgroundResource(R.drawable.login_button_login);
        mBtnGetSmsCode.setText(getString(R.string.login_register_smscode_btn));
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response != null) {
            Gson gson = new Gson();
            if (apiName.equals("sms_code")) {
                CommonResponseModel commonResponse = gson.fromJson(
                        response.toString(), CommonResponseModel.class);

                if (commonResponse != null && commonResponse.getResponse_code() == 1102) {
                    // 手机号已存在
                    setTimeOut();

                    ToastManager.showToast(this, commonResponse.getResponse_msg());
                }
            }

            if (apiName.equals("check_sms_code")) {
                CommonResponseModel crm = gson.fromJson(response.toString(),
                        CommonResponseModel.class);

                if (crm != null && crm.getResponse_code() == 1) {
                    if (mFrom != null && mFrom.equals("UserInfoActivity")) {
                        if (mType != null && !mType.equals("")) {
                            // 检查是否是第三方登录
                            if (mType.equals("add") && LoginModel.checkIsSocialUser()) {
                                Intent intent = new Intent(this, SetpwdActivity.class);
                                intent.putExtra("phoneNum", mPhoneNum);
                                intent.putExtra("type", "add");
                                startActivityForResult(intent, 10);
                            } else {
                                mRequest.authHandle(ParamBuilder.authHandle("0", mType, mPhoneNum, ""));
                            }
                        }
                    } else if (mFrom != null && mFrom.equals("forget_pwd")) {
                        Intent intent = new Intent(RegisterActivity.this, SetpwdActivity.class);
                        intent.putExtra("phoneNum", mPhoneNum);
                        intent.putExtra("type", "forget_pwd");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, SetpwdActivity.class);
                        intent.putExtra("phoneNum", mPhoneNum);
                        startActivity(intent);
                    }
                } else {
                    ToastManager.showToast(this, "验证码不正确");
                }
            }

            if (apiName.equals("auth_handle")) {
                CommonResponseModel crm = gson.fromJson(response.toString(),
                        CommonResponseModel.class);
                if (crm != null && crm.getResponse_code() == 1) {
                    // 修改成功
                    User user = UserDAO.findById();
                    if (user != null) {
                        UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);
                        userInfo.setMobile_num(mPhoneNum);
                        UserDAO.updateUserInfo(gson.toJson(userInfo));

                        ToastManager.showToast(this, "修改成功");

                        Intent intent = new Intent(this, UserInfoActivity.class);
                        intent.putExtra("user_info", gson.toJson(userInfo));
                        setResult(11, intent);

                        finish();
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
    }
}
