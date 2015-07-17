package com.appublisher.quizbank.model.login.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;

import java.lang.ref.WeakReference;
import java.util.Timer;

/**
 * 手机号用户重置密码
 */
public class MobileResetPwdActivity extends ActionBarActivity implements View.OnClickListener{

    private static final int TIME_ON = 1;
    private static final int TIME_OUT = 2;
    private static TextView mTvReGet;
    private static int mTimeLimit;
    private static Timer mTimer;

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

        // View 初始化
        TextView tvPhone = (TextView) findViewById(R.id.mobile_resetpwd_phone);
        TextView tvReSetPwd = (TextView) findViewById(R.id.mobile_resetpwd_next);
        TextView tvNoReply = (TextView) findViewById(R.id.smscode_noreply);
        final EditText etSmsCode = (EditText) findViewById(R.id.smscode_code);
        mTvReGet = (TextView) findViewById(R.id.smscode_reget);
        mTvSmsCodeError = (TextView) findViewById(R.id.smscode_error);
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
}
