package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.activity.ExamChangeActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.update.AppUpdate;
import com.appublisher.quizbank.common.update.NewVersion;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity implements RequestCallback {

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 获取全局配置
        new QRequest(this, this).getGlobalSettings();

        // 应用版本更新
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        boolean isFirstStart = Globals.sharedPreferences.getBoolean("isFirstStart", true);
        editor.putBoolean("appUpdate", true);

        // 版本更新后，更新内容是否提示
        editor.putBoolean("firstNotice", isFirstStart);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) {
            skipToMainActivity();
            return;
        }

        if ("global_settings".equals(apiName)) {
            GlobalSettingDAO.save(response.toString());
            GlobalSettingsResp globalSettingsResp =
                    GsonManager.getModel(response.toString(), GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences("updateVersion", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("versionInfo", GsonManager.modelToString(
                        globalSettingsResp.getNew_version(), NewVersion.class));
                editor.commit();
            }
        }

        // 版本更新
        boolean enable = Globals.sharedPreferences.getBoolean("appUpdate", false);
        if (enable && AppUpdate.showUpGrade(this)) {
        } else {
            skipToMainActivity();
        }
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
    public void skipToMainActivity() {
        // 页面跳转
        Class<?> cls;
        if (LoginModel.isLogin()) {
            // 已登录
            if (LoginModel.hasExamInfo()) {
                cls = MainActivity.class;
            } else {
                // 没有考试项目
                cls = ExamChangeActivity.class;
            }

            // Umeng
            final Map<String, String> um_map = new HashMap<String, String>();
            um_map.put("Entry", "Launch");
            UmengManager.onEvent(SplashActivity.this, "Home", um_map);

        } else {
            // 未登录
            cls = MainActivity.class;
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
