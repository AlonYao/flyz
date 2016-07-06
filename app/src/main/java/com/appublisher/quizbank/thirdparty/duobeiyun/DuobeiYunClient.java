package com.appublisher.quizbank.thirdparty.duobeiyun;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.appublisher.quizbank.thirdparty.duobeiyun.listener.YunDownloadListener;
import com.appublisher.quizbank.thirdparty.duobeiyun.utils.FileUtil;
import com.appublisher.quizbank.thirdparty.duobeiyun.utils.Httpd;
import com.appublisher.quizbank.thirdparty.duobeiyun.utils.NetUtil;
import com.appublisher.quizbank.thirdparty.duobeiyun.utils.TempHttpd;
import com.appublisher.quizbank.thirdparty.duobeiyun.utils.Unzip;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuguolin on 2/3/2016.
 */
public class DuobeiYunClient {

    static String dirPath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "duobeiyun";

    static Httpd nanoHTTPd;

    static TempHttpd tempHttpd;

    static final int PORT = 12728;
    static final int PORT2 = 12729;

    static final String BASE_URL = "http://7xod1f.dl1.z0.glb.clouddn.com/";

    public static BaseDownloadTask download(Context context, String roomId, final YunDownloadListener yunDownloadListener){
//        FileDownloadUtils.setDefaultSaveRootPath(Environment.getExternalStorageDirectory().toString());
        try {
            String url = BASE_URL + roomId+".zip";

            if(!new File(dirPath).exists()){
                new File(dirPath).mkdir();
            }

            downPlayer(yunDownloadListener);

            BaseDownloadTask baseDownloadTask = FileDownloader.getImpl().create(url)
                    .setPath(dirPath + File.separator + roomId  + ".zip")
                    .setForceReDownload(true)
                    .setListener(new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            yunDownloadListener.pending(task, soFarBytes, totalBytes);
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            yunDownloadListener.progress(task, soFarBytes, totalBytes);
                        }

                        @Override
                        protected void blockComplete(BaseDownloadTask task) {
                            yunDownloadListener.blockComplete(task);
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            try {
                                final BaseDownloadTask downloadTask = task;
                                Log.d("duobeiyun", task.getPath());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Unzip.unzip(new File(downloadTask.getPath()), new File(dirPath));
                                        } catch (IOException e) {
                                            yunDownloadListener.error(downloadTask, e);
                                        }
                                        FileUtil.deleteFile(downloadTask.getPath());
                                    }
                                }).start();

                                yunDownloadListener.completed(task);

                            } catch (Exception e) {
                                yunDownloadListener.error(task, e);
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            yunDownloadListener.paused(task, soFarBytes, totalBytes);
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            yunDownloadListener.error(task, e);
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                            yunDownloadListener.warn(task);
                        }
                    });
            baseDownloadTask.start();

            return baseDownloadTask;
        }catch (Exception e){
            Log.d("duobeiyun", e.getMessage());
        }

        return null;
    }

    public static void pauseDownload(int downloadId){
        FileDownloader.getImpl().pause(downloadId);
    }

    public static boolean delete(String roomId){
        String zipFile = dirPath + File.separator + roomId  + ".zip";
        if(new File(zipFile).exists()){
            FileUtil.deleteFile(zipFile);
        }
        String fileDir = dirPath + File.separator + roomId;
        if(new File(fileDir).exists()){
            FileUtil.deleteFile(fileDir);
            return true;
        }
        return false;
    }

    public static void downPlayer(final YunDownloadListener yunDownloadListener){
        new Thread(){
            @Override
            public void run() {
                String version = NetUtil.get(BASE_URL + "version.txt?t=" + System.currentTimeMillis());
                String basePlayUrl = BASE_URL + "yun-sdk-"+version+".zip";
                File sdkPlayVersionFile = new File(dirPath + File.separator + "play/version.txt");
                boolean download = false;
                if(!sdkPlayVersionFile.exists()){
                    basePlayUrl = BASE_URL + "yun-sdk-"+version+".zip";
                    download = true;
                }
                else{
                    String playVersion = FileUtil.readFile(sdkPlayVersionFile, "UTF-8").toString();
                    if(!playVersion.equals(version)){
                        File sdkPlayDir = new File(dirPath + File.separator + "play");
                        if(sdkPlayDir.exists()){
                            sdkPlayDir.delete();
                        }
                        basePlayUrl = BASE_URL + "yun-sdk-"+version+".zip";
                        download = true;
                    }
                }
                if(download){
                    FileDownloader.getImpl().create(basePlayUrl)
                            .setPath(dirPath + File.separator + "yun-sdk.zip")
                            .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void blockComplete(BaseDownloadTask task) {

                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                    try {
                                        final BaseDownloadTask downloadTask = task;
                                        Log.d("duobeiyun", task.getPath());
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Unzip.unzip(new File(downloadTask.getPath()), new File(dirPath + "/play"));
                                                } catch (IOException e) {
                                                    yunDownloadListener.error(downloadTask, e);
                                                }
                                                FileUtil.deleteFile(downloadTask.getPath());
                                            }
                                        }).start();
//                                        yunDownloadListener.completed(task);
                                    } catch (Exception e) {
                                        yunDownloadListener.error(task, e);
                                    }
                                }
                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {

                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {

                                }
                            })
                            .start();
                }

            }
        }.start();
    }

    public static void startServer() throws IOException {
        String filePath = null;
        if (FileDownloadHelper.getAppContext().getExternalCacheDir() == null) {
            filePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            filePath = FileDownloadHelper.getAppContext().getExternalCacheDir().getAbsolutePath();
        }

        String oldFilePath = filePath + File.separator + "duobeiyun";

        if(nanoHTTPd != null){
            nanoHTTPd.stop();
        }
        nanoHTTPd = Httpd.getInstance(PORT, dirPath);

        if(tempHttpd != null){
            tempHttpd.stop();
        }
        tempHttpd = TempHttpd.getInstance(PORT2, oldFilePath);


    }

    public static void stopServer(){
        if(nanoHTTPd != null){
            nanoHTTPd.stop();
        }
        if(tempHttpd != null){
            tempHttpd.stop();
        }
    }

    public static String playUrl(String roomId){
        String playUrl = "";

        if(new File(dirPath + File.separator + roomId).exists()){
            playUrl = "http://127.0.0.1:" + PORT + "/play/index.html?roomId=" + roomId;
        }
        else{
            playUrl = "http://127.0.0.1:" + PORT2 + "/play/index.html?roomId=" + roomId;
        }

        return playUrl;
    }

    public static int status(int downloadId){
        return FileDownloader.getImpl().getStatus(downloadId);
    }

    public static long sizeOfDirectory(String roomId){
       return FileUtil.sizeOfDirectory(new File(dirPath + File.separator + roomId));
    }

    public static long numOfDirectory(String roomId){
        return FileUtil.numOfDirectory(new File(dirPath + File.separator + roomId));
    }
}
