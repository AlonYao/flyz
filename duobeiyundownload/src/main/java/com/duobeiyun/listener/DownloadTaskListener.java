package com.duobeiyun.listener;

import java.io.File;

public class DownloadTaskListener {

    public void onStart(String fileName, String url) {}

    public boolean onConnect(int type, String msg) {
        return true;
    }

    public void onProgress(int progress, int fileLength) {

    }

//    public void onStop() {
//
//    }

    public void onFinish(File file) {
    }

    public void onError(String error) {
    }
}
