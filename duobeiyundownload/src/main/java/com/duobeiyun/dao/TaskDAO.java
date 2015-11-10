package com.duobeiyun.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.duobeiyun.entity.DownloadInfo;
import com.duobeiyun.entity.TaskInfo;
import com.duobeiyun.utils.Constants;

import java.io.File;

public class TaskDAO extends DAO {
    public TaskDAO(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DownloadInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + Constants.DBCons.TB_TASK + "(" +
                        Constants.DBCons.TB_TASK_URL_BASE + ", " +
                        Constants.DBCons.TB_TASK_URL_REAL + ", " +
                        Constants.DBCons.TB_TASK_FILE_PATH + ", " +
                        Constants.DBCons.TB_TASK_PROGRESS + ", " +
                        Constants.DBCons.TB_TASK_FILE_LENGTH + ") values (?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.downLoadLocalFile.getAbsolutePath(), i.progress,
                        i.length});
        db.close();
    }

    @Override
    public void deleteInfo(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.DBCons.TB_TASK + " WHERE " +
                Constants.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DownloadInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + Constants.DBCons.TB_TASK + " SET " +
                Constants.DBCons.TB_TASK_PROGRESS + "=? WHERE " +
                Constants.DBCons.TB_TASK_URL_BASE + "=?", new Object[]{i.progress, i.baseUrl});
        db.close();
    }

    @Override
    public DownloadInfo queryInfo(String url) {
        TaskInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                Constants.DBCons.TB_TASK_URL_BASE + ", " +
                Constants.DBCons.TB_TASK_URL_REAL + ", " +
                Constants.DBCons.TB_TASK_FILE_PATH + ", " +
                Constants.DBCons.TB_TASK_PROGRESS + ", " +
                Constants.DBCons.TB_TASK_FILE_LENGTH + " FROM " +
                Constants.DBCons.TB_TASK + " WHERE " +
                Constants.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        if (c.moveToFirst()) {
            info = new TaskInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3), c.getInt(4));
        }
        c.close();
        db.close();
        return info;
    }
}
