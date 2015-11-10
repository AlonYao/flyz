package com.duobeiyun.entity;

import java.io.File;
import java.io.Serializable;

public class TaskInfo extends DownloadInfo implements Serializable {

    public int progress;

    public int length;

    public TaskInfo(File downloadLocalFile, String baseUrl, String realUrl, int progress, int length) {
        super(downloadLocalFile, baseUrl, realUrl);
        this.progress = progress;
        this.length = length;
    }
}
