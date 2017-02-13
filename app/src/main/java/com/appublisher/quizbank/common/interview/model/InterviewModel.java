package com.appublisher.quizbank.common.interview.model;


import android.content.Context;

import com.appublisher.lib_basic.DownloadAsyncTask;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;

import java.io.File;

public class InterviewModel {

    /**
     * 下载&解压&播放
     *
     * @param url     下载链接
     * @param localFile    文件名
     */
    public static void downloadVoiceVideo(final Context context, String url, final String fileFolder, final String localFile, final int questionId , final ICommonCallback ICommonCallback) {
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
                                if( fileFolder.contains("teacher_audio")) {
                                    // 重命名文件名
                                    File fileDir = new File(fileFolder);
                                    File[] files = fileDir.listFiles();
                                    for(File downloadFile:files) {
                                        if(downloadFile.exists() && downloadFile.isFile()){
                                            String renameFilePath = fileFolder + questionId + ".amr";
                                            if (!downloadFile.getAbsolutePath().equals(renameFilePath)){
                                                FileManager.renameFile(downloadFile.getAbsolutePath(), renameFilePath);
                                            }
                                        }
                                    }
                                }
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
    /*
    *   修改时间格式
    * */
    public String formatDateTime(int mss) {

        String DateTimes ;
        int minutes = ( mss % ( 60 * 60) ) / 60;
        int seconds = mss % 60;

        if(minutes>0){
            if(seconds < 10){
                DateTimes = minutes + "\'" + "0" + seconds + "\"";
                return DateTimes;
            }
            DateTimes = minutes + "\'" + seconds + "\"";
        }else{
            DateTimes = seconds + "\"";
        }
        return DateTimes;
    }
}
