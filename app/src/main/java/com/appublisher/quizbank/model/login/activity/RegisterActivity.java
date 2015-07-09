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
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
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
    private String mType;
    private Gson mGson;

    public String mFrom;
    public String mOpenCourseId;

    private int mTimeLimit = 60;
    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;

    /** Umeng */
    public boolean mUmengIsCheckSuccess;
    public long mUmengTimestamp;
    public String mUmengEntry;

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
        mGson = GsonManager.initGson();
        mUmengTimestamp = System.currentTimeMillis();
        mUmengIsCheckSuccess = false;

        // 获取数据 & ActionBar标题修改
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
        mFrom = getIntent().getStringExtra("from");
        if ("UserInfoActivity".equals(mFrom)) {
            mType = getIntent().getStringExtra("type");
            if (mType == null) mType = "";
            if (mType.equals("update")) getSupportActionBar().setTitle("更换手机号");
            if (mType.equals("add")) getSupportActionBar().setTitle("绑定手机号");
        } else if ("forget_pwd".equals(mFrom)) {
            getSupportActionBar().setTitle("找回密码");
        } else if ("book_opencourse".equals(mFrom)
                || "opencourse_started".equals(mFrom)
                || "opencourse_pre".equals(mFrom)) {
            getSupportActionBar().setTitle("验证手机号");
            mOpenCourseId = getIntent().getStringExtra("content");
        }

        // 获取验证码按钮
        mBtnGetSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNum = etPhone.getText().toString();

                if (mPhoneNum.isEmpty()) return;

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

                if ("forget_pwd".equals(mFrom)) {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "resetPswd"));
                    // Umeng
                    UmengManager.sendCountEvent(
                            RegisterActivity.this, "CodeReq", "Type", "Forget");

                } else if ("book_opencourse".equals(mFrom)
                        || "opencourse_started".equals(mFrom)
                        || "opencourse_pre".equals(mFrom)) {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "token_login"));
                    // Umeng
                    UmengManager.sendCountEvent(
                            RegisterActivity.this, "CodeReq", "Type", "Verify");

                } else if ("UserInfoActivity".equals(mFrom)){
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, ""));
                    // Umeng
                    UmengManager.sendCountEvent(
                            RegisterActivity.this, "CodeReq", "Type", "Link");

                } else {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, ""));
                    // Umeng
                    UmengManager.sendCountEvent(
                            RegisterActivity.this, "CodeReq", "Type", "Reg");
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

                    if ("book_opencourse".equals(mFrom) || "opencourse_started".equals(mFrom)) {
                        mRequest.login(ParamBuilder.openCourseLoginParams(
                                "3", mPhoneNum, smsCode));
                    } else {
                        mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mPhoneNum, smsCode));
                    }

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

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RegisterActivity");
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

        MobclickAgent.onPageEnd("RegisterActivity");
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
        if (response == null) return;

        if ("sms_code".equals(apiName)) {
            CommonResponseModel commonResponse = mGson.fromJson(
                    response.toString(), CommonResponseModel.class);

            if (commonResponse != null && commonResponse.getResponse_code() == 1102) {
                // 手机号已存在
                setTimeOut();

                ToastManager.showToast(this, commonResponse.getResponse_msg());
            }
        }

        if ("check_sms_code".equals(apiName)) {
            CommonResponseModel crm = mGson.fromJson(response.toString(),
                    CommonResponseModel.class);

            if (crm != null && crm.getResponse_code() == 1) {
                if ("UserInfoActivity".equals(mFrom)) {
                    // 绑定&修改手机号
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
                } else if ("forget_pwd".equals(mFrom)) {
                    // 忘记密码
                    Intent intent = new Intent(RegisterActivity.this, SetpwdActivity.class);
                    intent.putExtra("phoneNum", mPhoneNum);
                    intent.putExtra("type", "forget_pwd");
                    startActivity(intent);
                    finish();
                } else {
                    // 注册
                    Intent intent = new Intent(RegisterActivity.this, SetpwdActivity.class);
                    intent.putExtra("phoneNum", mPhoneNum);
                    startActivity(intent);
                }
            } else {
                ToastManager.showToast(this, "验证码不正确");
            }
        }

        if ("auth_handle".equals(apiName)) {
            CommonResponseModel crm = mGson.fromJson(response.toString(),
                    CommonResponseModel.class);
            if (crm != null && crm.getResponse_code() == 1) {
                // 修改成功
                User user = UserDAO.findById();
                if (user != null) {
                    UserInfoModel userInfo = mGson.fromJson(user.user, UserInfoModel.class);
                    userInfo.setMobile_num(mPhoneNum);
                    UserDAO.updateUserInfo(mGson.toJson(userInfo));

                    ToastManager.showToast(this, "修改成功");

                    Intent intent = new Intent(this, UserInfoActivity.class);
                    intent.putExtra("user_info", mGson.toJson(userInfo));
                    setResult(11, intent);

                    finish();
                }
            }
        }

        // 处理预约公开课手机号验证部分的回调
        if ("login".equals(apiName)) {
            LoginModel.dealBookOpenCourse(this, response);
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
