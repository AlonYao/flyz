package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.login.activity.LoginActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {

            @SuppressLint("CommitPrefEdits")
            @Override
            public void run() {
                // 页面跳转
                Class<?> cls;
                boolean is_login = Globals.sharedPreferences.getBoolean("is_login", false);
                if (is_login) {
                    // 已登录
                    cls = MainActivity.class;
                } else {
                    // 未登录
                    cls = LoginActivity.class;

                    // 友盟
                    Globals.umeng_login_event = "RegLogLaunch";
                }

                Intent intent = new Intent(SplashActivity.this, cls);
                intent.putExtra("from", "splash");
                startActivity(intent);

                boolean isFirstStart = Globals.sharedPreferences.getBoolean("isFirstStart", true);
                if (isFirstStart) {
                    // app 引导页
                }

                finish();
            }
        };

        handler.postDelayed(runnable, 1000);
    }
}
