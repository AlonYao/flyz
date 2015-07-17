package com.appublisher.quizbank.model.login.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.utils.Utils;

/**
 * 找回密码
 */
public class ForgetPwdActivity extends ActionBarActivity implements View.OnClickListener{

    public String mUserName;
    private EditText mEtUserName;

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

        btnNext.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forget_pwd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                    ProgressDialogManager.showProgressDialog(ForgotPwdActivity.this, true);
                    mLoginRequest.isUserExists(mUserName);

                } else {
                    // 非邮箱用户，默认是手机号用户
                    mType = PHONE;
                    ProgressDialogManager.showProgressDialog(ForgotPwdActivity.this, true);
                    mLoginRequest.isUserExists(mUserName);

                    // Umeng
                    UmengManager.sendCountEvent(
                            ForgotPwdActivity.this, "CodeReq", "Type", "Forget");

                }

                break;
        }
    }
}
