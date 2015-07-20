package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.login.activity.LoginActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 启动页
 */
public class SplashActivity extends Activity implements RequestCallback{

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
                    if (LoginModel.hasExamInfo()) {
                        cls = MainActivity.class;
                    } else {
                        // 没有考试项目
                        cls = ExamChangeActivity.class;
                    }

                    // Umeng
                    UmengManager.sendCountEvent(SplashActivity.this, "Home", "Entry", "Launch");

                } else {
                    // 未登录
                    cls = LoginActivity.class;
                }

                Intent intent = new Intent(SplashActivity.this, cls);
                intent.putExtra("from", "splash");
                startActivity(intent);

                boolean isFirstStart = Globals.sharedPreferences.getBoolean("isFirstStart", true);
                if (isFirstStart) {
                    // app 引导页
                    intent = new Intent(SplashActivity.this, AppGuideActivity.class);
                    startActivity(intent);

                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putBoolean("isFirstStart", false);
                    editor.commit();
                }

                finish();
            }
        };

        handler.postDelayed(runnable, 1000);

        // Update Use Count
        if (LoginModel.isLogin() && !GlobalSettingDAO.isGrade()) CommonModel.updateUseCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("SplashActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("SplashActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ToastManager.showOvertimeToash(this);
    }
}
