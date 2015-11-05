package com.appublisher.quizbank.common.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.common.login.model.netdata.IsUserExistsResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 绑定手机号
 */
public class BindingMobileActivity extends ActionBarActivity implements RequestCallback {

    private Request mRequest;
    private String mPhoneNum;
    public String mFrom;
    public int mock_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_binding_mobile);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        final EditText etMobile = (EditText) findViewById(R.id.bindingmobile_phone);
        final Button btnNext = (Button) findViewById(R.id.bindingmobile_next);
        TextView tvOpenCourse = (TextView) findViewById(R.id.bindingmobile_opencourse);

        // 成员变量初始化
        mRequest = new Request(this, this);

        // 获取数据 & ActionBar标题修改
        mFrom = getIntent().getStringExtra("from");
        mock_id = getIntent().getIntExtra("mock_id", 0);

        if (mFrom != null && mFrom.contains("opencourse")) {
            getSupportActionBar().setTitle("短信验证");
            tvOpenCourse.setVisibility(View.VISIBLE);
        } else {
            getSupportActionBar().setTitle("验证手机号");
            tvOpenCourse.setVisibility(View.GONE);
        }
        //模考介绍页
        if ("mock_openopencourse".equals(mFrom)) {
            tvOpenCourse.setText("考试前会收到短信提示哦");
            tvOpenCourse.setVisibility(View.VISIBLE);
        }
        // 下一步
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNum = etMobile.getText().toString();

                if (mPhoneNum.isEmpty()) {
                    ToastManager.showToast(BindingMobileActivity.this, "手机号不能为空");
                    return;
                }

                ProgressDialogManager.showProgressDialog(BindingMobileActivity.this, false);
                mRequest.isUserExists(mPhoneNum);
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
        switch (resultCode) {
            case ActivitySkipConstants.BOOK_OPENCOURSE:
                Intent intent = new Intent(this, OpenCourseUnstartActivity.class);
                setResult(ActivitySkipConstants.BOOK_OPENCOURSE, intent);
                finish();
                break;

            case ActivitySkipConstants.OPENCOURSE_PRE:
                intent = new Intent(this, OpenCourseUnstartActivity.class);
                setResult(ActivitySkipConstants.OPENCOURSE_PRE, intent);
                finish();
                break;
            case ActivitySkipConstants.MOBILE_BOOK_MOCK_RESULT:
                setResult(ActivitySkipConstants.MOBILE_BOOK_MOCK_RESULT);
                finish();
                break;
            case ActivitySkipConstants.BOOK_MOCK_RESULT:
                setResult(ActivitySkipConstants.BOOK_MOCK_RESULT);
                finish();
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
            // 手机号是否注册接口
            IsUserExistsResp isUserExistsResp =
                    Globals.gson.fromJson(response.toString(), IsUserExistsResp.class);
            if (isUserExistsResp != null && isUserExistsResp.getResponse_code() == 1
                    && isUserExistsResp.isUser_exists()) {
                // 手机号已注册
                if ("mock_openopencourse".equals(mFrom)) {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "token_login"));
                    Intent intent = new Intent(this, BindingSmsCodeActivity.class);
                    intent.putExtra("user_phone", mPhoneNum);
                    intent.putExtra("from", mFrom);
                    intent.putExtra("mock_id", mock_id);
                    intent.putExtra("umeng_entry", getIntent().getStringExtra("umeng_entry"));
                    intent.putExtra("opencourse_id", getIntent().getStringExtra("content"));
                    startActivityForResult(intent, ActivitySkipConstants.MOBILE_BOOK_MOCK_ASK);
                } else {
                    AlertManager.openCourseUserChangeAlert(this);
                }
            } else {
                // 手机号未注册
                if (mFrom != null && mFrom.contains("opencourse")) {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, "token_login"));
                } else {
                    mRequest.getSmsCode(ParamBuilder.phoneNumParams(mPhoneNum, ""));
                }

                Intent intent = new Intent(this, BindingSmsCodeActivity.class);
                intent.putExtra("user_phone", mPhoneNum);
                intent.putExtra("from", mFrom);
                intent.putExtra("umeng_entry", getIntent().getStringExtra("umeng_entry"));
                intent.putExtra("opencourse_id", getIntent().getStringExtra("content"));

                if ("book_opencourse".equals(mFrom)) {
                    // 预定公开课
                    startActivityForResult(intent, ActivitySkipConstants.BOOK_OPENCOURSE);
                } else if ("opencourse_pre".equals(mFrom)) {
                    // 公开课回放
                    startActivityForResult(intent, ActivitySkipConstants.OPENCOURSE_PRE);
                } else if ("mock_openopencourse".equals(mFrom)) {
                    //模考
                    startActivityForResult(intent, ActivitySkipConstants.BOOK_MOCK_ASK);
                } else {
                    startActivity(intent);
                    finish();
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
