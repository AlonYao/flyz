package com.duobeiyun.entity;

import java.io.File;
import java.io.Serializable;

public class ThreadInfo extends DownloadInfo implements Serializable {
    public String id;
    public int start, end;

    public ThreadInfo(File downloadLocalFile, String baseUrl, String realUrl, int start, int end, String id) {
        super(downloadLocalFile, baseUrl, realUrl);
        this.start = start;
        this.end = end;
        this.id = id;
    }
}
