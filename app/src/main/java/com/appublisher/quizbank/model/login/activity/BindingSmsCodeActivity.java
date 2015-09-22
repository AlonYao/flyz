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
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 绑定手机号短信验证码页面
 */
public class BindingSmsCodeActivity extends ActionBarActivity
        implements RequestCallback, View.OnClickListener {

    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;
    private static TextView mTvReGet;
    private static int mTimeLimit;
    private static Timer mTimer;
    private Handler mHandler;
    private Request mRequest;
    private String mUserPhone;
    private EditText mEtSmsCode;

    public String mOpenCourseId;
    public String mFrom;

    /**
     * Umeng
     */
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
                        if (mTvReGet != null && mTimeLimit != 0) {
                            mTvReGet.setText("重新获取(" + String.valueOf(mTimeLimit) + "s)");
                            mTvReGet.setBackgroundResource(R.color.login_reget_smscode);
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
        setContentView(R.layout.activity_binding_sms_code);

        // ActionBar Set
        CommonModel.setToolBar(this);

        // View 初始化
        TextView tvPhone = (TextView) findViewById(R.id.binding_smscode_phone);
        TextView tvNext = (TextView) findViewById(R.id.binding_smscode_next);
        TextView tvNoReply = (TextView) findViewById(R.id.binding_smscode_noreply);
        mEtSmsCode = (EditText) findViewById(R.id.binding_smscode_code);
        mTvReGet = (TextView) findViewById(R.id.binding_smscode_reget);

        // 成员变量初始化
        mTimeLimit = 60;
        mHandler = new MsgHandler(this);
        mRequest = new Request(this, this);
        mFrom = getIntent().getStringExtra("from");

        // 获取数据
        mUserPhone = getIntent().getStringExtra("user_phone");
        mOpenCourseId = getIntent().getStringExtra("opencourse_id");

        // Umeng
        mUmengTimestamp = System.currentTimeMillis();
        mUmengIsCheckSuccess = false;
        mUmengEntry = getIntent().getStringExtra("umeng_entry");

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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BindingSmsCodeActivity");
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
            UmengManager.sendComputeEvent(this, "OnAir", map, (int) (dur / 1000));
        }

        MobclickAgent.onPageEnd("BindingSmsCodeActivity");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.binding_smscode_next:
                // 下一步（校验验证码）
                String smsCode = mEtSmsCode.getText().toString();

                if (mUserPhone == null || mUserPhone.length() == 0) return;

                ProgressDialogManager.showProgressDialog(this, false);

                if (mFrom != null && mFrom.contains("opencourse")) {
                    mRequest.login(ParamBuilder.openCourseLoginParams(
                            "3", mUserPhone, smsCode));
                } else {
                    mRequest.checkSmsCode(ParamBuilder.checkSmsCodeParams(mUserPhone, smsCode));
                }

                break;

            case R.id.binding_smscode_reget:
                // 重新获取短信验证码
                mRequest.getSmsCode(ParamBuilder.phoneNumParams(mUserPhone, ""));
                startTimer();
                break;

            case R.id.binding_smscode_noreply:
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
            // 校验短信验证码接口
            CommonResponseModel crm = Globals.gson.fromJson(response.toString(),
                    CommonResponseModel.class);

            if (crm != null && crm.getResponse_code() == 1) {

                if (LoginModel.checkIsSocialUser()) {
                    // 如果是第三方登录用户，需要提供密码
                    Intent intent = new Intent(this, SetpwdActivity.class);
                    intent.putExtra("phoneNum", mUserPhone);
                    intent.putExtra("type", "add");
                    startActivityForResult(intent, 10);
                } else {
                    mRequest.authHandle(ParamBuilder.authHandle(
                            "0", "add", mUserPhone, ""));
                }
            } else {
                ToastManager.showToast(this, getString(R.string.login_smscode_error));
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
                    userInfo.setMobile_num(mUserPhone);
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
        mTvReGet.setBackgroundResource(R.color.white);
    }
}
