package com.duobeiyun.manager;

import android.content.Context;

import com.duobeiyun.dao.TaskDAO;
import com.duobeiyun.dao.ThreadDAO;
import com.duobeiyun.entity.TaskInfo;
import com.duobeiyun.entity.ThreadInfo;

import java.util.List;

public final class DBManager {
    private static DBManager sManager = null;

    private TaskDAO daoTask;
    private ThreadDAO daoThread;

    private DBManager(Context context) {
        daoTask = new TaskDAO(context);
        daoThread = new ThreadDAO(context);
    }
    public static DBManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DBManager(context);
        }
        return sManager;
    }

    public synchronized void insertTaskInfo(TaskInfo info) {
        daoTask.insertInfo(info);
    }

    public synchronized void deleteTaskInfo(String url) {
        daoTask.deleteInfo(url);
    }

    public synchronized void updateTaskInfo(TaskInfo info) {
        daoTask.updateInfo(info);
    }

    public synchronized TaskInfo queryTaskInfoByUrl(String url) {
        return (TaskInfo) daoTask.queryInfo(url);
    }

    public synchronized void insertThreadInfo(ThreadInfo info) {
        daoThread.insertInfo(info);
    }

    public synchronized void deleteThreadInfoById(String id) {
        daoThread.deleteInfo(id);
    }

    public synchronized void deleteThreadInfos(String url) {
        daoThread.deleteInfo(url);
    }

    public synchronized void updateThreadInfo(ThreadInfo info) {
        daoThread.updateInfo(info);
    }

    public synchronized ThreadInfo queryThreadInfoById(String id) {
        return (ThreadInfo) daoThread.queryInfo(id);
    }

    public synchronized List<ThreadInfo> queryThreadInfos(String url) {
        return daoThread.queryInfos(url);
    }
    public void release() {
        daoTask.close();
    }
}
