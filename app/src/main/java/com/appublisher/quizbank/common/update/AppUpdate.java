package com.appublisher.quizbank.common.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.SplashActivity;
import com.appublisher.quizbank.utils.FileManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.Utils;

import java.io.File;

/**
 * Created on 15/12/14.
 */
public class AppUpdate {
    private static DownloadManager downloadManager;
    private static Long downloadId;
    public static final String DOWNLOAD_FOLDER_NAME = "quzibank";
    public static final String DOWNLOAD_FILE_NAME = "app-release.apk";

    /**
     * 显示更新提示
     */
    public static boolean showUpGrade(final Activity activity) {
        String newAppVersion;
        final String appDwonUrl;
        String updateMessage;
        SharedPreferences sharedPreferences = activity.getSharedPreferences("updateVersion", activity.MODE_PRIVATE);
        String versionInfo = sharedPreferences.getString("versionInfo", "");
        if (versionInfo == "") return false;
        NewVersion newVersion = GsonManager.getObejctFromJSON(versionInfo, NewVersion.class);
        if (newVersion == null) return false;
        newAppVersion = newVersion.getApp_version();
        appDwonUrl = newVersion.getTarget_url();
        updateMessage = "大小：" + newVersion.getSize() + "\n更新内容：\n" + newVersion.getNotice_text();
        //可更新
        if (Utils.compareVersion(newAppVersion, Globals.appVersion) == 1) {
            if (newVersion.getForce_update()) {//强制更新
                new AlertDialog.Builder(activity)
                        .setTitle("新版本 " + newAppVersion + "")
                        .setMessage(updateMessage)
                        .setCancelable(false)
                        .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadApk(activity, appDwonUrl);
                                if(activity instanceof SplashActivity){
                                    ((SplashActivity) activity).skipToMainActivity();
                                }
                            }
                        })
                        .create().show();
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("新版本 " + newAppVersion + "")
                        .setMessage(updateMessage)
                        .setCancelable(false)
                        .setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadApk(activity, appDwonUrl);
                                if(activity instanceof SplashActivity){
                                    ((SplashActivity) activity).skipToMainActivity();
                                }
                            }

                        })
                        .setNegativeButton(R.string.nexttime, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(activity instanceof SplashActivity){
                                    ((SplashActivity) activity).skipToMainActivity();
                                }
                            }
                        })
                        .create().show();
            }
            return true;
        }
        //一次登录应用提示一次
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putBoolean("appUpdate", false);
        editor.commit();
        return false;
    }

    /**
     * 下载Apk
     *
     * @param context 上下文
     * @param uri     Url
     */
    public static void downloadApk(Context context, String uri) {
        final String apkFilePath = new StringBuilder(
                Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(
                        File.separator)
                .append(DOWNLOAD_FILE_NAME).toString();
        // 下载完成后的广播事件处理
        class CompleteReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                /**
                 * get the id of download which have download success, if the id is my id
                 * and it's status is successful,
                 * then install it
                 **/
                long completeDownloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completeDownloadId == downloadId) {
                    // if download successful, install apk
                    if (getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                        //noinspection StringBufferReplaceableByString
                        if (install(context, apkFilePath)) {
                            ToastManager.showToast(context, "安装成功");
                        }
                    }
                }
            }
        }
        String path = new StringBuilder(
                Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator).append(DOWNLOAD_FOLDER_NAME).toString();
        FileManager.deleteFiles(path);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        // APK文件保存位置&名称
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
        // 通知栏显示
        if (android.os.Build.VERSION.SDK_INT < 11) {
            request.setShowRunningNotification(true);
        } else {
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        // 下载请求ID
        downloadId = downloadManager.enqueue(request);
        CompleteReceiver completeReceiver = new CompleteReceiver();
        // 下载完成后发出广播
        context.getApplicationContext().registerReceiver(completeReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Toast.makeText(context.getApplicationContext(), "正在下载，请稍候……",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * install app
     *
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

    /**
     * get download status
     *
     * @param downloadId
     * @return
     */
    public static int getStatusById(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }

    /**
     * get int column
     *
     * @param downloadId
     * @param columnName
     * @return
     */
    private static int getInt(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        int result = -1;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * 判断是否有新版本更新
     *
     * @return
     */
    public static boolean getIsNewAppVersion(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("updateVersion", context.MODE_PRIVATE);
        String versionInfo = sharedPreferences.getString("versionInfo", "");
        if (versionInfo == "") return false;
        NewVersion newVersion = GsonManager.getObejctFromJSON(versionInfo, NewVersion.class);
        if (newVersion == null) return false;
        return Utils.compareVersion(newVersion.getApp_version(), Globals.appVersion) == 1;
    }
}
