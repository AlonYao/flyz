package com.appublisher.quizbank.model.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 绑定手机号
 */
public class BindingMobileActivity extends ActionBarActivity implements RequestCallback {

    private Request mRequest;
    private String mPhoneNum;
    private static Timer mTimer;
    private Handler mHandler;
    private static TextView mTvGetSmsCode;

    public String mFrom;
    public String mOpenCourseId;

    private static int mTimeLimit;
    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;

    /** Umeng */
    public boolean mUmengIsCheckSuccess;
    public long mUmengTimestamp;
    public String mUmengEntry;

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
                        if (mTvGetSmsCode != null && mTimeLimit != 0) {
                            mTvGetSmsCode.setText(
                                    "获取验证码(" + String.valueOf(mTimeLimit) + "秒)");
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
        setContentView(R.layout.login_activity_binding_mobile);

        // ActionBar
        CommonModel.setToolBar(this);
        getSupportActionBar().setTitle("绑定手机号");

        // view初始化
        mTvGetSmsCode = (TextView) findViewById(R.id.binding_mobile_reget);
        Button btnSubmitCode = (Button) findViewById(R.id.binding_mobile_next);
        TextView tvCannotGet = (TextView) findViewById(R.id.binding_mobile_none_code);
        final EditText etPhone = (EditText) findViewById(R.id.binding_mobile_num);
        final EditText etSmsCode = (EditText) findViewById(R.id.binding_mobile_code);

        // 成员变量初始化
        mRequest = new Request(this, this);
        mUmengTimestamp = System.currentTimeMillis();
        mUmengIsCheckSuccess = false;
        mHandler = new MsgHandler(this);
        mTimeLimit = 60;

        // 获取数据 & ActionBar标题修改
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mFrom = getIntent().getStringExtra("from");

        if (mFrom != null && mFrom.contains("opencourse")) {
            getSupportActionBar().setTitle("短信验证");
        }

        // 获取验证码按钮
        mTvGetSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNum = etPhone.getText().toString();

                if (mPhoneNum.isEmpty()) return;

                mTvGetSmsCode.setClickable(false);

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

                mRequest.isUserExists(mPhoneNum);
            }
        });

        // 校验验证码按钮
        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsCode = etSmsCode.getText().toString();
                mPhoneNum = etPhone.getText().toString();
                if (!smsCode.isEmpty() && !mPhoneNum.isEmpty()) {
                    ProgressDialogManager.showProgressDialog(BindingMobileActivity.this, false);

                    if (mFrom != null && mFrom.contains("opencourse")) {
                        mRequest.login(ParamBuilder.openCourseLoginParams(
                                "3", mPhoneNum, smsCode));
                    } else {
                        mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mPhoneNum, smsCode));
                    }

                } else {
                    ToastManager.showToast(BindingMobileActivity.this, "手机号或验证码为空");
                }
            }
        });

        tvCannotGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BindingMobileActivity.this, CannotGetSmsActivity.class);
                startActivity(intent);
            }
        });

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BindingMobileActivity");
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        if ("opencourse_started".equals(mFrom) && !mUmengIsCheckSuccess) {
            HashMap<String, String> map = new HashMap<>();
            map.put("Entry", mUmengEntry);
            map.put("EnterLive", "0");
            map.put("QQ", "0");
            long dur = System.currentTimeMillis() - mUmengTimestamp;
            UmengManager.sendComputeEvent(this, "OnAir", map, (int) (dur/1000));
        }

        MobclickAgent.onPageEnd("BindingMobileActivity");
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
    private static void setTimeOut() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimeLimit = 60;
        mTvGetSmsCode.setClickable(true);
        mTvGetSmsCode.setText(R.string.login_register_smscode_btn);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if (Globals.gson == null) Globals.gson = GsonManager.initGson();

        if ("is_user_exists".equals(apiName)) {
            // 手机号是否注册接口
            IsUserExistsResp isUserExistsResp =
                    Globals.gson.fromJson(response.toString(), IsUserExistsResp.class);
            if (isUserExistsResp != null && isUserExistsResp.getResponse_code() == 1
                    && isUserExistsResp.isUser_exists()) {
                // 手机号已注册
                setTimeOut();
                AlertManager.openCourseUserChangeAlert(this);
            } else {
                // 手机号未注册
                if (mFrom != null && mFrom.contains("opencourse")) {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "token_login"));
                } else {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, ""));
                }
            }

        } else if ("sms_code".equals(apiName)) {
            // 获取短信验证码接口
            CommonResponseModel commonResponse = Globals.gson.fromJson(
                    response.toString(), CommonResponseModel.class);

            if (commonResponse != null && commonResponse.getResponse_code() == 1102) {
                // 手机号已存在
                setTimeOut();

                ToastManager.showToast(this, commonResponse.getResponse_msg());
            }

        } else if ("check_sms_code".equals(apiName)) {
            // 校验短信验证码接口
            CommonResponseModel crm = Globals.gson.fromJson(response.toString(),
                    CommonResponseModel.class);

            if (crm != null && crm.getResponse_code() == 1) {

                if (LoginModel.checkIsSocialUser()) {
                    // 如果是第三方登录用户，需要提供密码
                    Intent intent = new Intent(this, SetpwdActivity.class);
                    intent.putExtra("phoneNum", mPhoneNum);
                    intent.putExtra("type", "add");
                    startActivityForResult(intent, 10);
                } else {
                    mRequest.authHandle(ParamBuilder.authHandle(
                            "0", "add", mPhoneNum, ""));
                }
            } else {
                ToastManager.showToast(this, "验证码不正确");
            }

        } else if ("auth_handle".equals(apiName)) {
            // 改变用户信息接口
            CommonResponseModel crm = Globals.gson.fromJson(response.toString(),
                    CommonResponseModel.class);
            if (crm != null && crm.getResponse_code() == 1) {
                // 修改成功
                User user = UserDAO.findById();
                if (user != null) {
                    UserInfoModel userInfo = Globals.gson.fromJson(user.user, UserInfoModel.class);
                    userInfo.setMobile_num(mPhoneNum);
                    UserDAO.updateUserInfo(Globals.gson.toJson(userInfo));

                    ToastManager.showToast(this, "修改成功");

                    Intent intent = new Intent(this, UserInfoActivity.class);
                    intent.putExtra("user_info", Globals.gson.toJson(userInfo));
                    setResult(11, intent);

                    finish();
                }
            }

        } else if ("login".equals(apiName)) {
            // 处理预约公开课手机号验证部分的回调
            LoginModel.dealOpenCourseResp(this, response);
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
