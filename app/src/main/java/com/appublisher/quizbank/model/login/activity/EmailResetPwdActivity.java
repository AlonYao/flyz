package com.appublisher.quizbank.model.login.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;

/**
 * 邮箱用户重置密码
 */
public class EmailResetPwdActivity extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reset_pwd);

        // set actionbar
        CommonModel.setToolBar(this);

        // view init
        TextView tvUserEmail = (TextView) findViewById(R.id.emailreset_useremail);
        TextView tvEmailNoReply = (TextView) findViewById(R.id.emailreset_cannot);
        Button btnLogin = (Button) findViewById(R.id.emailreset_login);

        // 获取数据
        String userEmail = getIntent().getStringExtra("user_email");

        tvUserEmail.setText("已向邮箱" + userEmail + "发送邮件，请查看邮件并重置密码。");

        // 收不到邮件
        CommonModel.setTextUnderLine(tvEmailNoReply);
        tvEmailNoReply.setOnClickListener(this);

        // 登陆按钮
        btnLogin.setOnClickListener(this);

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
            case R.id.emailreset_cannot:
                // 收不到邮件
                Intent intent = new Intent(this, CannotGetEmailActivity.class);
                startActivity(intent);
                break;

            case R.id.emailreset_login:
                // 重新登录
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
