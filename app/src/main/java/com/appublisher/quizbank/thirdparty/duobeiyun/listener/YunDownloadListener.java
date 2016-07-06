package com.appublisher.quizbank.thirdparty.duobeiyun.listener;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by liuguolin on 2/3/2016.
 */
public interface YunDownloadListener {

    public void pending(BaseDownloadTask task, int soFarBytes, int totalBytes);

    public void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);

    public void blockComplete(BaseDownloadTask task);

    public void completed(BaseDownloadTask task);

    public void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);

    public void error(BaseDownloadTask task, Throwable e);

    public void warn(BaseDownloadTask task);
}
