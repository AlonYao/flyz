package com.appublisher.quizbank.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class AppDownload {
	private static DownloadManager downloadManager;
	private static Long downloadId;
	public static final String DOWNLOAD_FOLDER_NAME = "quizbank";
    public static final String DOWNLOAD_FILE_NAME   = "Application.apk";

	/**
	 * 下载Apk
	 * @param context 上下文
	 * @param uri Url
	 */
	public static void downloadApk(Context context, String uri) {
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
						String apkFilePath = new StringBuilder(
								Environment.getExternalStorageDirectory().getAbsolutePath())
								.append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(
										File.separator)
								.append(DOWNLOAD_FILE_NAME).toString();
						install(context, apkFilePath);
					}
				}
			}
		}

		downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		Request request = new Request(Uri.parse(uri));
		// APK文件保存位置&名称
		request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
		// 通知栏显示
		if (android.os.Build.VERSION.SDK_INT < 11) {
			//noinspection deprecation
			request.setShowRunningNotification(true);
		} else {
			request.setNotificationVisibility(
					Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
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
     * @param context context
     * @param filePath filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath),
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
     * @param downloadId downloadId
     * @return int
     */
    public static int getStatusById(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }
    
    /**
     * get int column
     * 
     * @param downloadId downloadId
     * @param columnName columnName
     * @return int
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
}
