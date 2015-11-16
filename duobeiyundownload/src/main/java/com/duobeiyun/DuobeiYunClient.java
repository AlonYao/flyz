package com.duobeiyun;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.duobeiyun.listener.DownloadTaskListener;
import com.duobeiyun.manager.DownloadManager;
import com.duobeiyun.utils.FileUtil;
import com.duobeiyun.utils.Httpd;
import com.duobeiyun.utils.NetUtil;
import com.duobeiyun.utils.Unzip;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuguolin on 7/21/15.
 */
public class DuobeiYunClient {

    static String dirPath = Environment.getExternalStorageDirectory().toString() + "/" + "duobeiyun";

    static Httpd nanoHTTPd;

    static final int PORT = 12728;

    static final String BASE_URL = "http://7xod1f.dl1.z0.glb.clouddn.com/";

    public static void download(Context context, String roomId, DownloadTaskListener dlTaskListener){
        String url = BASE_URL + roomId+".zip";
        try {
            DownloadManager.getInstance(context).dlCancel(url);
            delete(roomId);
        }catch (Exception e){

        }

        final Context rootContext = context;
        new Thread(){
            @Override
            public void run() {
                File sdkPlayVersionFile = new File(dirPath + File.separator + "play/version.txt");
                if(!sdkPlayVersionFile.exists()){
                    String basePlayUrl = BASE_URL + "yun-sdk-2.0.zip";
                    DownloadManager.getInstance(rootContext).dlStart(basePlayUrl, dirPath, new DownloadTaskListener(){
                    });
                }
                else{
                    String version = NetUtil.get(BASE_URL + "version.txt");
                    String playVersion = FileUtil.readFile(sdkPlayVersionFile, "UTF-8").toString();
                    if(!playVersion.equals(version)){
                        File sdkPlayDir = new File(dirPath + File.separator + "play");
                        if(sdkPlayDir.exists()){
                            sdkPlayDir.delete();
                        }

                        String basePlayUrl = BASE_URL + "yun-sdk-"+version+".zip";
                        DownloadManager.getInstance(rootContext).dlStart(basePlayUrl, dirPath, new DownloadTaskListener(){
                        });
                    }
                }
            }
        }.start();

        DownloadManager.getInstance(context).dlStart(url, dirPath, dlTaskListener);
    }


    public static void startServer() throws IOException {
        if(nanoHTTPd != null){
            nanoHTTPd.stop();
        }
        nanoHTTPd = Httpd.getInstance(PORT, dirPath);
    }

    public static void stopServer(){
        if(nanoHTTPd != null){
            nanoHTTPd.stop();
        }
    }

    public static String playUrl(String roomId){
        String playUrl = "http://127.0.0.1:" + PORT + "/play/index.html?roomId=" + roomId;
        return playUrl;
    }

    public static boolean delete(String roomId){
        if(new File(dirPath + File.separator + roomId).exists()){
            FileUtil.deleteFile(dirPath + File.separator + roomId);
            return true;
        }
        return false;
    }

    public static String getDownResourceUrl(String roomId){
        String url = BASE_URL + roomId+".zip";
        return url;
    }

    public static void unzipResource(String roomId){
        String downFilePath = dirPath + File.separator + roomId + ".zip";
        try {
            Unzip.unzip(new File(downFilePath), new File(dirPath));
        } catch (IOException e) {
            Log.d(e.getMessage(), e.getMessage());
        }
    }

    public static String fetchLatetVersionUrl(){
        return BASE_URL + "version.txt?" + System.currentTimeMillis();
    }

    public static String fetchCurrentVersion(){
        try {
            File sdkPlayVersionFile = new File(dirPath + File.separator + "play/version.txt");
            String playVersion = FileUtil.readFile(sdkPlayVersionFile, "UTF-8").toString();
            return playVersion;
        } catch (Exception e) {
            // Empty
        }

        return null;
    }

    public static String getPlayerResourceUrl(String version){
        String basePlayUrl = BASE_URL + "yun-sdk-"+version+".zip";
        return basePlayUrl;
    }
}
