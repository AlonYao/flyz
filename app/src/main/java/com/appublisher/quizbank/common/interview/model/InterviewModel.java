package com.appublisher.quizbank.common.interview.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.appublisher.lib_basic.DownloadAsyncTask;
import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.network.ICommonCallback;

import java.io.File;

public class InterviewModel {

    private String mTimeStamp;

    /**
     * 下载&解压&播放
     *
     * @param url     下载链接
     * @param localFile    文件名
     */
    public void downloadAudio(final Context context, String url, final String fileFolder, final String localFile,  final ICommonCallback ICommonCallback) {
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
                                    if(files == null || files.length <=0) return;
                                    for(File downloadFile:files) {
                                        if(downloadFile.exists() && downloadFile.isFile()){
                                            String renameFilePath = fileFolder +"/" + mTimeStamp + ".amr";
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
        if (minutes>0){
            if (seconds < 10){
                DateTimes = minutes + "\'" + "0" + seconds + "\"";
                return DateTimes;
            }
            DateTimes = minutes + "\'" + seconds + "\"";
        } else {
            DateTimes = seconds + "\"";
        }
        return DateTimes;
    }

    /*
    *   获取面试页面中的本地文件
    * */
    public static SharedPreferences getInterviewSharedPreferences(InterviewPaperDetailActivity activity){
        return activity.getSharedPreferences("interview", Context.MODE_PRIVATE);
    }

    /*
    *   将时间戳变成一个数字
    * */
    public String changeTimeStampToText(String timeStamp){
        return (timeStamp.substring(0,4))
                + (timeStamp.substring(5,7)) + (timeStamp.substring(8,10))
                + (timeStamp.substring(11,13)) + (timeStamp.substring(14,16))
                +(timeStamp.substring(17,19));
    }
    /*
    *  传入时间戳
    * */
    public void setTimeStamp(String timeStamp){
        if(timeStamp == null || timeStamp.length() <= 0) return;
        mTimeStamp = timeStamp;
    }

}
