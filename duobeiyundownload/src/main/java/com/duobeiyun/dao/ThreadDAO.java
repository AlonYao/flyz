package com.duobeiyun.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.duobeiyun.entity.DownloadInfo;
import com.duobeiyun.entity.ThreadInfo;
import com.duobeiyun.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThreadDAO extends DAO {
    public ThreadDAO(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DownloadInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + Constants.DBCons.TB_THREAD + "(" +
                        Constants.DBCons.TB_THREAD_URL_BASE + ", " +
                        Constants.DBCons.TB_THREAD_URL_REAL + ", " +
                        Constants.DBCons.TB_THREAD_FILE_PATH + ", " +
                        Constants.DBCons.TB_THREAD_START + ", " +
                        Constants.DBCons.TB_THREAD_END + ", " +
                        Constants.DBCons.TB_THREAD_ID + ") VALUES (?,?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.downLoadLocalFile.getAbsolutePath(), i.start,
                        i.end, i.id});
        db.close();
    }

    @Override
    public void deleteInfo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.DBCons.TB_THREAD + " WHERE " +
                Constants.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteInfos(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.DBCons.TB_THREAD + " WHERE " +
                Constants.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DownloadInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + Constants.DBCons.TB_THREAD + " SET " +
                Constants.DBCons.TB_THREAD_START + "=? WHERE " +
                Constants.DBCons.TB_THREAD_URL_BASE + "=? AND " +
                Constants.DBCons.TB_THREAD_ID + "=?", new Object[]{i.start, i.baseUrl, i.id});
        db.close();
    }

    @Override
    public DownloadInfo queryInfo(String id) {
        ThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                Constants.DBCons.TB_THREAD_URL_BASE + ", " +
                Constants.DBCons.TB_THREAD_URL_REAL + ", " +
                Constants.DBCons.TB_THREAD_FILE_PATH + ", " +
                Constants.DBCons.TB_THREAD_START + ", " +
                Constants.DBCons.TB_THREAD_END + " FROM " +
                Constants.DBCons.TB_THREAD + " WHERE " +
                Constants.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        if (c.moveToFirst()) {
            info = new ThreadInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4), id);
        }
        c.close();
        db.close();
        return info;
    }

    public List<ThreadInfo> queryInfos(String url) {
        List<ThreadInfo> infos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                Constants.DBCons.TB_THREAD_URL_BASE + ", " +
                Constants.DBCons.TB_THREAD_URL_REAL + ", " +
                Constants.DBCons.TB_THREAD_FILE_PATH + ", " +
                Constants.DBCons.TB_THREAD_START + ", " +
                Constants.DBCons.TB_THREAD_END + ", " +
                Constants.DBCons.TB_THREAD_ID + " FROM " +
                Constants.DBCons.TB_THREAD + " WHERE " +
                Constants.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        while (c.moveToNext()) {
            infos.add(new ThreadInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3),  c.getInt(4), c.getString(5)));
        }
        c.close();
        db.close();
        return infos;
    }
}
