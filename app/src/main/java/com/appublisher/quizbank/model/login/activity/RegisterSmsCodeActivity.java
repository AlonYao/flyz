package com.appublisher.quizbank.model.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.ExamChangeActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
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
 * 注册时获取短信验证码
 */
public class RegisterSmsCodeActivity extends ActionBarActivity
        implements RequestCallback, View.OnClickListener{

    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;
    private static TextView mTvReGet;
    private static int mTimeLimit;
    private static Timer mTimer;
    private Handler mHandler;
    private Request mRequest;
    private String mUserPhone;
    private String mUserPwd;
    private EditText mEtSmsCode;

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
        setContentView(R.layout.activity_register_sms_code);

        // ActionBar Set
        CommonModel.setToolBar(this);

        // View 初始化
        TextView tvPhone = (TextView) findViewById(R.id.register_smscode_phone);
        TextView tvNext = (TextView) findViewById(R.id.register_smscode_next);
        TextView tvNoReply = (TextView) findViewById(R.id.register_smscode_noreply);
        mEtSmsCode = (EditText) findViewById(R.id.register_smscode_code);
        mTvReGet = (TextView) findViewById(R.id.register_smscode_reget);

        // 成员变量初始化
        mTimeLimit = 60;
        mHandler = new MsgHandler(this);
        mRequest = new Request(this, this);

        // 获取数据
        mUserPhone = getIntent().getStringExtra("user_phone");
        mUserPwd = getIntent().getStringExtra("user_pwd");

        // 控制EditText点击时提示文字消失
        CommonModel.setEditTextHintHideOnFocus(mEtSmsCode,
                getString(R.string.login_register_smscode_edittext));

        if (mUserPhone == null) mUserPhone = "";
        Spannable word = new SpannableString("请输入手机号" + mUserPhone + "收到的短信校验码");
        word.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.login_red)),
                6,
                word.toString().indexOf("收"),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tvPhone.setText(word);

        // 重新获取验证码
        mTvReGet.setOnClickListener(this);

        // 下一步
        tvNext.setOnClickListener(this);

        // 收不到验证码
        CommonModel.setTextUnderLine(tvNoReply);
        tvNoReply.setOnClickListener(this);

        // 开始计时
        startTimer();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_smscode_reget:
                // 重新获取短信验证码
                mRequest.getSmsCode(ParamBuilder.phoneNumParams(mUserPhone, ""));
                startTimer();
                break;

            case R.id.register_smscode_next:
                // 下一步
                String smsCode = mEtSmsCode.getText().toString();

                if (smsCode.isEmpty()) {
                    ToastManager.showToast(this, getString(R.string.login_smscode_error));
                    break;
                }

                ProgressDialogManager.showProgressDialog(RegisterSmsCodeActivity.this, false);
                mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mUserPhone, smsCode));
                break;

            case R.id.register_smscode_noreply:
                // 收不到短信验证码
                Intent intent = new Intent(
                        RegisterSmsCodeActivity.this, CannotGetSmsActivity.class);
                startActivity(intent);
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

        if ("check_sms_code".equals(apiName)) {
            /** 短信验证码校验接口 **/
            CommonResponseModel commonResp = Globals.gson.fromJson(response.toString(),
                    CommonResponseModel.class);

            if (commonResp != null && commonResp.getResponse_code() == 1) {
                // 注册成功
                mRequest.register(ParamBuilder.register(mUserPhone, mUserPwd));
            } else {
                // 验证码校验失败
                ToastManager.showToast(this, getString(R.string.login_smscode_error));
                ProgressDialogManager.closeProgressDialog();
            }

        } else if ("register".equals(apiName)) {
            /** 注册接口 **/
            LoginResponseModel lrm =
                    Globals.gson.fromJson(response.toString(), LoginResponseModel.class);

            if (LoginModel.saveToLocal(lrm, this)) {
                // 注册成功
                Intent intent = new Intent(this, ExamChangeActivity.class);
                intent.putExtra("from", "reg");
                startActivity(intent);
                finish();
            } else {
                // 注册失败
                ToastManager.showToast(this, "注册失败");
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
