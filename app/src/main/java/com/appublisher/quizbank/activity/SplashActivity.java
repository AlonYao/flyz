package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_course.promote.PromoteModel;
import com.appublisher.lib_course.promote.PromoteResp;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.update.AppUpdate;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.MockDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.network.QRequest;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动页
 */
public class SplashActivity extends Activity implements RequestCallback {

    private PromoteModel mPromoteModel;
    private ImageView mImageView;
    private TextView mTextView;
    private PromoteResp mPromoteResp;

    private static int mSec;
    private static final int TIME_ON = 0;
    private static final int TIME_OUT = 1;

    public static class MsgHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private WeakReference<TextView> mTextView;

        MsgHandler(Activity activity, TextView textView) {
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
                    activity.skipToMainActivity();
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
        new QRequest(this, this).getGlobalSettings();
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
        if ("global_settings".equals(apiName)) {
            if (response == null) response = new JSONObject();
            GlobalSettingDAO.save(response.toString());

            // 缓存至本地
            SharedPreferences sp = getSharedPreferences("global_setting", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("global_setting", response.toString());
            editor.commit();

            GlobalSettingsResp globalSettingsResp =
                    GsonManager.getModel(response.toString(), GlobalSettingsResp.class);
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                SharedPreferences sharedPreferences =
                        getSharedPreferences("updateVersion", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("versionInfo", GsonManager.modelToString(
                        globalSettingsResp.getNew_version(),
                        GlobalSettingsResp.NewVersionBean.class));
                editor.commit();
            }

            if (AppUpdate.showUpGrade(SplashActivity.this)) return;

            // 获取国考公告解读宣传
            mPromoteModel.getPromoteData(new PromoteModel.PromoteDataListener() {
                @Override
                public void onComplete(boolean success, PromoteResp resp) {
                    mPromoteResp = resp;
                    if (success && LoginModel.isLogin() && !isFirstStart()) {
                        PromoteResp.ImageBean imageBean = resp.getImage();
                        if (imageBean == null) {
                            // 数据异常
                            skipToMainActivity();
                        } else {
                            String target = imageBean.getTarget();
                            int isBook = MockDAO.getIsDateById(target);
                            if (isBook == 1) {
                                // 已预约
                                skipToMainActivity();
                            } else {
                                showPromoteImg(resp);
                            }
                        }
                    } else {
                        skipToMainActivity();
                    }
                }
            });
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

    private void showPromoteImg(PromoteResp resp) {
        if (resp == null || resp.getResponse_code() != 1) {
            skipToMainActivity();
            return;
        }

        final PromoteResp.ImageBean imageBean = resp.getImage();
        if (imageBean == null || !imageBean.isEnable()) {
            skipToMainActivity();
            return;
        }

        String imgUrl = imageBean.getAndroid();
        ImageManager.displayImage(imgUrl, mImageView, new ImageManager.LoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                skipToMainActivity();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mTextView.setText(String.valueOf(mSec));
                mTextView.setVisibility(View.VISIBLE);
                final Handler mHandler = new MsgHandler(SplashActivity.this, mTextView);
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mSec--;
                        if (mSec < 1) {
                            mHandler.sendEmptyMessage(TIME_OUT);
                            timer.cancel();
                        } else {
                            mHandler.sendEmptyMessage(TIME_ON);
                        }
                    }

                }, 1000, 1000);

                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String targetType = imageBean.getTarget_type();
                        String target = imageBean.getTarget();
                        if (target == null || target.length() == 0) return;

                        if ("url".equals(targetType)) {
                            // 外部链接
                            toMainActivity();
                            Intent intent = new Intent(SplashActivity.this, CourseWebViewActivity.class);
                            intent.putExtra("url", LoginParamBuilder.finalUrl(target));
                            if (target.contains("course_id"))
                                intent.putExtra("from", "course");
                            startActivity(intent);

                        } else if ("mokao".equals(targetType)) {
                            // 模考
                            toMainActivity();
                            Intent intent = new Intent(SplashActivity.this, MockPreActivity.class);
                            intent.putExtra("type", "mock");
                            intent.putExtra("mock_id", Integer.parseInt(target));
                            startActivity(intent);
                        }
                        timer.cancel();
                        finish();

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Screen");
                        UmengManager.onEvent(SplashActivity.this, "Ad", map);
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                skipToMainActivity();
            }
        });
    }

    /**
     * 跳转至主页面
     */
    @SuppressLint("CommitPrefEdits")
    public void skipToMainActivity() {
        toMainActivity();

        // app 引导页
        if (isFirstStart()) {
            Intent intent = new Intent(this, AppGuideActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
            editor.putBoolean("isFirstStart", false);
            editor.commit();
        }

        finish();

        // Umeng
//        UmengManager.sendCountEvent(this, "Home", "Entry", "Launch");
    }

    public void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from", "splash");
        intent.putExtra(
                MainActivity.INTENT_PROMOTE,
                GsonManager.modelToString(mPromoteResp, PromoteResp.class));
        startActivity(intent);
    }

    /**
     * 是否是首次安装
     *
     * @return boolean
     */
    private static boolean isFirstStart() {
        return Globals.sharedPreferences.getBoolean("isFirstStart", true);
    }

}