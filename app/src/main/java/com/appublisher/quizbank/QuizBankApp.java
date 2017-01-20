package com.appublisher.quizbank;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.appublisher.lib_basic.ActiveAndroidManager;
import com.appublisher.lib_basic.LibBasicManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.LinkedList;

/**
 * QuizBankApp
 */
public class QuizBankApp extends Application {

    public static QuizBankApp mInstance;

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

        // 初始化多贝云离线下载
        FileDownloader.init(this);
        FileDownloadUtils.setDefaultSaveRootPath(
                Environment.getExternalStorageDirectory().toString());


        mInstance = this;
    }

    // 单例模式中获取唯一的QuizBankApp实例
    public static QuizBankApp getInstance() {
        return mInstance;
    }

}
