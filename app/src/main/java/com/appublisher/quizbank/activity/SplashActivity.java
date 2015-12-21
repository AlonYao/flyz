package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.activity.LoginActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.globalsettings.NewVersion;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 启动页
 */
public class SplashActivity extends Activity implements RequestCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 获取全局配置
        new Request(this, this).getGlobalSettings();
        //应用版本更新
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        boolean isFirstStart = Globals.sharedPreferences.getBoolean("isFirstStart", true);
        editor.putBoolean("appUpdate", true);
        //版本更新后，更新内容是否提示
        editor.putBoolean("firstNotice", isFirstStart);
        editor.commit();
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
        if (response == null) {
            skipToMainActivity();
            return;
        }

        if ("global_settings".equals(apiName)) {
            GlobalSettingDAO.save(response.toString());
            Logger.i("" + response.toString());
            GlobalSettingsResp globalSettingsResp = GsonManager.getObejctFromJSON(response.toString(), GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                SharedPreferences sharedPreferences = getSharedPreferences("updateVersion", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("versionInfo", globalSettingsResp.getNew_version().toString());
                Logger.i("ceshi");
                NewVersion object = globalSettingsResp.getNew_version();
                Logger.i("versionInfo=" + object.toString());
                editor.commit();
            }
        }
        skipToMainActivity();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        skipToMainActivity();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ToastManager.showOvertimeToash(this);
        skipToMainActivity();
    }

    /**
     * 跳转至主页面
     */
    @SuppressLint("CommitPrefEdits")
    private void skipToMainActivity() {
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

}
