package com.duobeiyun.dao;

import android.content.Context;

import com.duobeiyun.entity.DownloadInfo;

public abstract class DAO {
    protected DBOpenHelper dbHelper;

    public DAO(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    public abstract void insertInfo(DownloadInfo info);

    public abstract void deleteInfo(String url);

    public abstract void updateInfo(DownloadInfo info);

    public abstract DownloadInfo queryInfo(String str);

    public void close() {
        dbHelper.close();
    }
}
