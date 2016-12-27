package com.appublisher.quizbank.common.interview.network;


import android.content.Context;

import com.appublisher.lib_basic.DownloadAsyncTask;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;

import java.io.File;

public class InterviewModel {


    /**
     * 下载&解压&播放
     *
     * @param url     下载链接
     * @param zipFile zip文件名
     */
    public static void downloadVideo(final Context context, String url, final String fileFolder, final String zipFile, final ICommonCallback ICommonCallback) {
        ProgressDialogManager.showProgressDialog(context);
        DownloadAsyncTask mDownloadAsyncTask = new DownloadAsyncTask(
                url,
                zipFile,
                new DownloadAsyncTask.FinishListener() {
                    @Override
                    public void onFinished() {
                        ProgressDialogManager.closeProgressDialog();
                        File file = new File(zipFile);
                        if (file.exists()) {
                            ToastManager.showToast(context, "音频下载成功");
                            FileManager.unzipFiles(fileFolder, zipFile);
                            FileManager.deleteFiles(zipFile);
                            ICommonCallback.callback(true);
                        } else {
                            ICommonCallback.callback(false);
                            ToastManager.showToast(context, "音频下载失败，请重试");
                        }
                    }
                },
                null);
        mDownloadAsyncTask.execute();
    }

}
