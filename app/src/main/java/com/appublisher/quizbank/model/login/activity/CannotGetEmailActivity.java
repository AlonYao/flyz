package com.appublisher.quizbank.model.login.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;

/**
 * 收不到邮件页面
 */
public class CannotGetEmailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cannot_get_email);

        // set actionbar
        CommonModel.setToolBar(this);

        // set qq
        TextView tvQQ = (TextView) findViewById(R.id.cannotget_email_qq);

        String qq = getString(R.string.service_qq);
        tvQQ.setText("如果仍然无法收到邮件，请联系客服QQ:\n" + qq);
        CommonModel.setTextLongClickCopy(tvQQ);

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
}
