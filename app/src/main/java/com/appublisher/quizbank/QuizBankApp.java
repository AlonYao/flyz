package com.appublisher.quizbank;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.utils.OpenUDID_manager;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

/**
 * QuizBankApp
 */
public class QuizBankApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取版本号
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            Globals.appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Parse
        Parse.initialize(this, getString(R.string.parse_applicationid),
                getString(R.string.parse_clientkey));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("");

        // TalkingData
        TCAgent.init(this);

        // 初始化UUID
        OpenUDID_manager.sync(this);

        // 初始化本地缓存
        Globals.sharedPreferences = getSharedPreferences("quizbank_store", 0);

        // 已登录状态下进行数据库切换
        if (LoginModel.isLogin()) {
            String user_id = Globals.sharedPreferences.getString("user_id", "");
            LoginModel.setDatabase(user_id, this);
        }

        // 友盟反馈
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();

        // Umeng 统计
        MobclickAgent.openActivityDurationTrack(false);
    }
}
