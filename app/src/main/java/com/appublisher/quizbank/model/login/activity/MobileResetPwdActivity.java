package com.appublisher.quizbank.model.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机号用户重置密码
 */
public class MobileResetPwdActivity extends ActionBarActivity implements
        View.OnClickListener, RequestCallback{

    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;
    private static TextView mTvReGet;
    private static int mTimeLimit;
    private static Timer mTimer;
    private Handler mHandler;
    private Request mRequest;
    private String mUserPhone;
    private String mNewPwdEncrypt;
    private EditText mEtSmsCode;
    private EditText mEtNewPwd;
    private EditText mEtNewPwdConfirm;

    private static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIME_ON:
                        if (mTvReGet != null && mTimeLimit != 0) {
                            mTvReGet.setText("重新获取(" + String.valueOf(mTimeLimit) + "s)");
                        }

                        break;

                    case TIME_OUT:
                        setTimeOut();
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
        setContentView(R.layout.activity_mobile_reset_pwd);

        // ActionBar Set
        CommonModel.setToolBar(this);

        // 成员变量初始化
        mTimeLimit = 60;
        mHandler = new MsgHandler(this);
        mRequest = new Request(this, this);

        // 获取数据
        mUserPhone = getIntent().getStringExtra("user_phone");

        // View 初始化
        TextView tvPhone = (TextView) findViewById(R.id.mobile_resetpwd_phone);
        TextView tvNext = (TextView) findViewById(R.id.mobile_resetpwd_next);
        TextView tvNoReply = (TextView) findViewById(R.id.mobile_resetpwd_noreply);
        mEtSmsCode = (EditText) findViewById(R.id.mobile_resetpwd_smscode);
        mEtNewPwd = (EditText) findViewById(R.id.mobile_resetpwd_new);
        mEtNewPwdConfirm = (EditText) findViewById(R.id.mobile_resetpwd_new_confirm);
        mTvReGet = (TextView) findViewById(R.id.mobile_resetpwd_reget);

        tvPhone.setText("已向手机" + mUserPhone + "发送短信\n请输入您收到的验证码");
        tvNext.setOnClickListener(this);
        tvNoReply.setOnClickListener(this);
        mTvReGet.setOnClickListener(this);

        // 保存Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mobile_resetpwd_reget:
                // 重新获取短信验证码
                mRequest.getSmsCode(ParamBuilder.phoneNumParams(mUserPhone, "resetPswd"));
                startTimer();
                break;

            case R.id.mobile_resetpwd_next:
                // 重置密码
                String smsCode = mEtSmsCode.getText().toString();
                String newPwd = mEtNewPwd.getText().toString();
                String newPwdConfirm = mEtNewPwdConfirm.getText().toString();

                if (smsCode.isEmpty()) {
                    ToastManager.showToast(this, "验证码为空");
                    break;

                } else if (newPwd.isEmpty()) {
                    ToastManager.showToast(this, "新密码不能为空");
                    break;

                } else if (newPwdConfirm.isEmpty()) {
                    ToastManager.showToast(this, "确认新密码不能为空");
                    break;

                } else if (!newPwd.equals(newPwdConfirm)) {
                    ToastManager.showToast(this, "两次密码不一致");
                    break;
                }

                mNewPwdEncrypt = LoginModel.encrypt(newPwd, "appublisher");

                if (mNewPwdEncrypt.length() == 0) {
                    ToastManager.showToast(this, "密码格式不正确");
                    break;
                }

                ProgressDialogManager.showProgressDialog(this, false);
                mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mUserPhone, smsCode));
                break;

            case R.id.mobile_resetpwd_noreply:
                // 收不到验证码
                Intent intent = new Intent(this, CannotGetSmsActivity.class);
                startActivity(intent);
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

        if ("check_sms_code".equals(apiName)) {
            // 校验验证码接口
            CommonResponseModel commonResp =
                    Globals.gson.fromJson(response.toString(), CommonResponseModel.class);
            if (commonResp != null && commonResp.getResponse_code() == 1) {
                mRequest.forgetPwd(ParamBuilder.forgetPwd(mUserPhone, mNewPwdEncrypt));
            } else {
                ToastManager.showToast(this, "验证码不正确");
                ProgressDialogManager.closeProgressDialog();
            }

        } else if ("forget_password".equals(apiName)) {
            // 重置密码
            CommonResponseModel commonResponse =
                    Globals.gson.fromJson(response.toString(), CommonResponseModel.class);
            if (commonResponse != null && commonResponse.getResponse_code() == 1) {
                ToastManager.showToast(this, "密码重置成功");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                ToastManager.showToast(this, "密码重置失败");
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

    /**
     * 设置时间结束的操作
     */
    private static void setTimeOut() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimeLimit = 60;
        mTvReGet.setClickable(true);
        mTvReGet.setText("重新获取");
    }

    /**
     * 开始计时器
     */
    private void startTimer() {
        mTvReGet.setClickable(false);

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
    }
}
