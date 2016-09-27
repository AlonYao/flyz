package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.promote.PromoteModel;
import com.appublisher.quizbank.common.promote.PromoteResp;
import com.appublisher.quizbank.common.update.NewVersion;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
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
        private WeakReference<TextView> mTextView;

        public MsgHandler(Activity activity, TextView textView) {
            mActivity = new WeakReference<>(activity);
            mTextView = new WeakReference<>(textView);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final SplashActivity activity = (SplashActivity) mActivity.get();
            final TextView textView = mTextView.get();
            switch (msg.what) {
                case TIME_ON:
                    textView.setText(String.valueOf(mSec));
                    break;

                case TIME_OUT:
                    skipToMainActivity(activity);
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
            skipToMainActivity(this);
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
            if (isFirstStart() || !LoginModel.isLogin()) {
                skipToMainActivity(this);
            } else {
                mPromoteModel.getPromoteData(new PromoteModel.PromoteDataListener() {
                    @Override
                    public void onComplete(boolean success, PromoteResp resp) {
                        if (success) {
                            showPromoteImg(resp);
                        } else {
                            skipToMainActivity(SplashActivity.this);
                        }
                    }
                });
            }
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
        skipToMainActivity(this);
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ToastManager.showOvertimeToash(this);
        skipToMainActivity(this);
    }

    private void showPromoteImg(PromoteResp resp) {
        if (resp == null || resp.getResponse_code() != 1) {
            skipToMainActivity(this);
            return;
        }

        final PromoteResp.ImageBean imageBean = resp.getImage();
        if (imageBean == null || !imageBean.isEnable()) {
            skipToMainActivity(this);
            return;
        }

        String imgUrl = imageBean.getAndroid();
        mPromoteModel.getRequest().loadImage(imgUrl, mImageView);

        mTextView.setText("3");
        mTextView.setVisibility(View.VISIBLE);
        final Handler mHandler = new MsgHandler(this, mTextView);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mSec--;
                if (mSec < 0) {
                    mHandler.sendEmptyMessage(TIME_OUT);
                    timer.cancel();
                } else {
                    mHandler.sendEmptyMessage(TIME_ON);
                }
            }

        }, 0, 1000);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetType = imageBean.getTarget_type();
                if ("url".equals(targetType)) {
                    // 外部链接
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    intent = new Intent(SplashActivity.this, WebViewActivity.class);
                    intent.putExtra("url", imageBean.getTarget());
                    startActivity(intent);
                } else if ("mokao".equals(targetType)) {
                    // 模考
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    intent = new Intent(SplashActivity.this, MockPreActivity.class);
                    intent.putExtra("type", "mock");
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 跳转至主页面
     */
    @SuppressLint("CommitPrefEdits")
    public static void skipToMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("from", "splash");
        activity.startActivity(intent);

        // app 引导页
        if (isFirstStart()) {
            intent = new Intent(activity, AppGuideActivity.class);
            activity.startActivity(intent);

            SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
            editor.putBoolean("isFirstStart", false);
            editor.commit();
        }

        activity.finish();

        // Umeng
        UmengManager.sendCountEvent(activity, "Home", "Entry", "Launch");
    }

    /**
     * 是否是首次安装
     * @return boolean
     */
    private static boolean isFirstStart() {
        return Globals.sharedPreferences.getBoolean("isFirstStart", true);
    }

}
