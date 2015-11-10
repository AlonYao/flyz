package com.duobeiyun.entity;

import java.io.File;
import java.io.Serializable;

public class DownloadInfo implements Serializable {

    public File downLoadLocalFile;
    public String baseUrl;
    public String realUrl;

    public DownloadInfo(File downLoadLocalFile, String baseUrl, String realUrl) {
        this.downLoadLocalFile = downLoadLocalFile;
        this.baseUrl = baseUrl;
        this.realUrl = realUrl;
    }
}
