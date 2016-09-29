package com.appublisher.quizbank;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.appublisher.lib_basic.ActiveAndroidManager;
import com.appublisher.lib_basic.LibBasicManager;
import com.appublisher.lib_basic.OpenUDIDManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.igexin.sdk.PushManager;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import java.util.LinkedList;

/**
 * QuizBankApp
 */
public class QuizBankApp extends Application {

    public LinkedList<Activity> mActivityList = new LinkedList<>();
    public static QuizBankApp mInstance;

    public QuizBankApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        MobclickAgent.setDebugMode(true);

        // 获取版本号
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            Globals.appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // TalkingData
//        TCAgent.init(this);

        // 初始化UUID
//        OpenUDIDManager.sync(this);

        // 初始化本地缓存
        Globals.sharedPreferences = getSharedPreferences("quizbank_store", 0);

        // 初始化基本库
        LibBasicManager.init(this);

        // 初始化登录注册模块
        LoginModel.init(this);

        // 已登录状态下进行数据库切换
        if (LoginModel.isLogin()) {
            ActiveAndroidManager.setDatabase(com.appublisher.lib_login.model.business.LoginModel.getUserId(), this);
        } else {
            ActiveAndroidManager.setDatabase("guest", this);
        }

        // 友盟反馈
//        FeedbackAgent agent = new FeedbackAgent(this);
//        agent.sync();

        // Umeng 统计
//        MobclickAgent.openActivityDurationTrack(false);

        // 听云
//        NBSAppAgent.setLicenseKey(getString(R.string.tingyun_appkey))
//                .withLocationServiceEnabled(true).start(this);

        // 初始化多贝云离线下载
        FileDownloader.init(this);
        FileDownloadUtils.setDefaultSaveRootPath(
                Environment.getExternalStorageDirectory().toString());
    }

    // 单例模式中获取唯一的QuizBankApp实例
    public static QuizBankApp getInstance() {
        if (null == mInstance) {
            mInstance = new QuizBankApp();
        }
        return mInstance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (mActivityList == null) mActivityList = new LinkedList<>();

        mActivityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        if (mActivityList == null) return;

        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }
}
