package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.LoginResponseModel;
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

import java.util.HashMap;

public class SetpwdActivity extends ActionBarActivity implements RequestCallback{

    private static final int SET_PASSWORD_SUCCESS = 10;

    private Gson mGson;
    private String mPhoneNum;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_PASSWORD_SUCCESS:
                    Intent intent = new Intent(SetpwdActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_setpwd);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        final EditText etPwd = (EditText) findViewById(R.id.setpwd_et);
        final Request request = new Request(this, this);
        Button btnSubmit = (Button) findViewById(R.id.setpwd_btn);

        // 成员变量初始化
        mGson = new Gson();

        // 确认按钮
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString();

                if (!pwd.isEmpty()) {
                    String phoneNum = getIntent().getStringExtra("phoneNum");
                    if (phoneNum != null && !phoneNum.equals("")) {
                        String pwdEncrypt = LoginModel.encrypt(pwd, "appublisher");
                        if (!pwdEncrypt.isEmpty()) {
                            // 获取数据
                            String type = getIntent().getStringExtra("type");
                            if (type != null && type.equals("add")) {
                                // 第三方登录用户绑定手机号需要提供密码
                                mPhoneNum = getIntent().getStringExtra("phoneNum");
                                if (mPhoneNum != null && !mPhoneNum.equals("")) {
                                    ProgressDialogManager.showProgressDialog(SetpwdActivity.this);
                                    request.authHandle(ParamBuilder.authHandle(
                                            "0", type, mPhoneNum, pwdEncrypt));
                                }
                            } else if (type != null && type.equals("forget_pwd")) {
                                ProgressDialogManager.showProgressDialog(SetpwdActivity.this);
                                request.forgetPwd(ParamBuilder.forgetPwd(phoneNum, pwdEncrypt));
                            } else {
                                ProgressDialogManager.showProgressDialog(SetpwdActivity.this);
                                request.register(ParamBuilder.register(phoneNum, pwdEncrypt));
                            }
                        }
                    }
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
            if (apiName.equals("register")) {
                LoginResponseModel lrm = mGson.fromJson(response.toString(),
                        LoginResponseModel.class);

                if (lrm != null && lrm.getResponse_code() == 1) {
                    UserInfoModel uim = lrm.getUser();

                    if (uim != null) {
                        String user_id = uim.getUser_id();
                        if (user_id != null && !user_id.equals("")) {
                            LoginModel.migrateGuestToUser(this, user_id);
                            UserDAO.updateUserInfo(mGson.toJson(uim));

                            // 友盟
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Action", "Reg");
                            MobclickAgent.onEvent(this, Globals.umeng_login_event, map);
                            Globals.umeng_quiz_lastevent = Globals.umeng_login_event;

                            mHandler.sendEmptyMessage(SET_PASSWORD_SUCCESS);
                        }
                    }
                }
            }

            if (apiName.equals("auth_handle")) {
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

                        Intent intent = new Intent(this, RegisterActivity.class);
                        intent.putExtra("user_info", mGson.toJson(userInfo));
                        setResult(10, intent);

                        finish();
                    }
                }
            }

            if (apiName.equals("forget_password")) {
                CommonResponseModel commonResponse =
                        mGson.fromJson(response.toString(), CommonResponseModel.class);
                if (commonResponse != null && commonResponse.getResponse_code() == 1) {
                    ToastManager.showToast(this, "密码重置成功");
                    finish();
                } else if (commonResponse != null && commonResponse.getResponse_code() == 1000) {
                    ToastManager.showToast(this, "该手机号未注册");
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
