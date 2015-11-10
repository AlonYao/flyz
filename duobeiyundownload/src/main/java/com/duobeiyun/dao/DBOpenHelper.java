package com.duobeiyun.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duobeiyun.utils.Constants;


public final class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dl.db";
    private static final int DB_VERSION = 3;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.DBCons.TB_TASK_SQL_CREATE);
        db.execSQL(Constants.DBCons.TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.DBCons.TB_TASK_SQL_UPGRADE);
        db.execSQL(Constants.DBCons.TB_THREAD_SQL_UPGRADE);
        onCreate(db);
    }
}
