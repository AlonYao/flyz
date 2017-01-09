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
     * @param localFile    文件名
     */
    public static void downloadVoiceVideo(final Context context, String url, final String fileFolder, final String localFile, final ICommonCallback ICommonCallback) {
        ProgressDialogManager.showProgressDialog(context);
        DownloadAsyncTask mDownloadAsyncTask = new DownloadAsyncTask(
                url,
                localFile,
                new DownloadAsyncTask.FinishListener() {
                    @Override
                    public void onFinished() {
                        ProgressDialogManager.closeProgressDialog();
                        File file = new File(localFile);
                        if (file.exists()) {
                            if(file.getName().contains(".zip")){
                                ToastManager.showToast(context, "音频下载成功");
                                FileManager.unzipFiles(fileFolder, localFile);
                                FileManager.deleteFiles(localFile);
                            }
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

    /**
     * 下载&解压&播放
     *
     * @param url     下载链接
     */
    public static void downloadVideo(final Context context, String url, final String path, final ICommonCallback ICommonCallback) {
        ProgressDialogManager.showProgressDialog(context);
        DownloadAsyncTask mDownloadAsyncTask = new DownloadAsyncTask(
                url,
                path,
                new DownloadAsyncTask.FinishListener() {
                    @Override
                    public void onFinished() {
                        ProgressDialogManager.closeProgressDialog();
                        File file = new File(path);
                        if (file.exists()) {
                            ToastManager.showToast(context, "音频下载成功");
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
