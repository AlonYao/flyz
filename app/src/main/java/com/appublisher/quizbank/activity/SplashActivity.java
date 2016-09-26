package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.activity.LoginActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.promote.PromoteModel;
import com.appublisher.quizbank.common.promote.PromoteResp;
import com.appublisher.quizbank.common.update.NewVersion;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
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

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动页
 */
public class SplashActivity extends Activity implements RequestCallback {

    private PromoteModel mPromoteModel;
    private ImageView mImageView;
    private TextView mTextView;

    private static int mSec;
    private static final int TIME_ON = 0;
    private static final int TIME_OUT = 1;

    public static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;
//        private WeakReference<TextView> mTextView;

        public MsgHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
//            mTextView = new WeakReference<>(textView);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final SplashActivity activity = (SplashActivity) mActivity.get();
            final TextView textView = (TextView) activity.findViewById(R.id.splash_timer);
            switch (msg.what) {
                case TIME_ON:
                    textView.setText(mSec);
                    break;

                case TIME_OUT:
                    ToastManager.showToast(activity, "时间到");
                    break;

                default:
                    break;
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mImageView = (ImageView) findViewById(R.id.splash_bg);
        mTextView = (TextView) findViewById(R.id.splash_timer);

        // 获取全局配置
        new Request(this, this).getGlobalSettings();
        mPromoteModel = new PromoteModel(this);
        mSec = 3;

        // 应用版本更新
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        boolean isFirstStart = Globals.sharedPreferences.getBoolean("isFirstStart", true);
        editor.putBoolean("appUpdate", true);

        // 版本更新后，更新内容是否提示
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
                    GsonManager.getObejctFromJSON(response.toString(), GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences("updateVersion", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("versionInfo", GsonManager.getGson().toJson(
                        globalSettingsResp.getNew_version(), NewVersion.class));
                editor.commit();
            }

            // 获取国考公告解读宣传
            mPromoteModel.getPromoteData(new PromoteModel.PromoteDataListener() {
                @Override
                public void onComplete(boolean success, PromoteResp resp) {
                    if (success) {
                        showPromoteImg(resp);
                    } else {
                        skipToMainActivity();
                    }
                }
            });
        }

//        // 版本更新
//        boolean enable = Globals.sharedPreferences.getBoolean("appUpdate", false);
//        if (enable && AppUpdate.showUpGrade(this)) {
//        }else{
//            skipToMainActivity();
//        }
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

    private void showPromoteImg(PromoteResp resp) {
        if (resp == null || resp.getResponse_code() != 1) {
            skipToMainActivity();
            return;
        }

        PromoteResp.InfoBean infoBean = resp.getInfo();
        if (infoBean == null) {
            skipToMainActivity();
            return;
        }

        PromoteResp.InfoBean.ImageBean imageBean = infoBean.getImage();
        if (imageBean == null) {
            skipToMainActivity();
            return;
        }

        String imgUrl = imageBean.getAndroid();
        mPromoteModel.getRequest().loadImage(imgUrl, mImageView);
        final Handler mHandler = new MsgHandler(this);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Logger.e("111111111");

                mSec--;
                if (mSec < 0) {
                    mHandler.sendEmptyMessage(TIME_OUT);
                    timer.cancel();
                } else {
                    mHandler.sendEmptyMessage(TIME_ON);
                }
            }

        }, 0, 1000);
    }

    /**
     * 跳转至主页面
     */
    @SuppressLint("CommitPrefEdits")
    public void skipToMainActivity() {
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
