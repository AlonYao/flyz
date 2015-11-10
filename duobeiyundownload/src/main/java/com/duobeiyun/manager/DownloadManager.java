package com.duobeiyun.manager;

import android.content.Context;

import com.duobeiyun.entity.TaskInfo;
import com.duobeiyun.entity.ThreadInfo;
import com.duobeiyun.listener.DownloadTaskListener;
import com.duobeiyun.listener.DownloadThreadListener;
import com.duobeiyun.utils.Constants;
import com.duobeiyun.utils.FileUtil;
import com.duobeiyun.utils.HttpConnParameter;
import com.duobeiyun.utils.NetUtil;
import com.duobeiyun.utils.Unzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DownloadManager {
    private static final int THREAD_POOL_SIZE = 32;

    private static DownloadManager sManager;
    private static Hashtable<String, DLTask> sTaskDLing;
    private static DBManager sDBManager;

    private ExecutorService mExecutor;
    private Context context;

    private String dirPath;

    public DownloadManager(Context context) {
        this.context = context;
        this.mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        sDBManager = DBManager.getInstance(context);
        sTaskDLing = new Hashtable<>();
    }

    public static DownloadManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DownloadManager(context);
        }
        return sManager;
    }

    public void dlStart(String url, String dirPath, DownloadTaskListener listener) {
        this.dirPath = dirPath;
        DLPrepare dlPrepare = new DLPrepare(url, dirPath, listener);
        mExecutor.execute(dlPrepare);
    }

    public void dlStop(String url) {
        if (sTaskDLing.containsKey(url)) {
            DLTask task = sTaskDLing.get(url);
            task.setStop(true);
        }
    }

    public void dlCancel(String url) {
        dlStop(url);
        if (null != sDBManager.queryTaskInfoByUrl(url)) {
            sDBManager.deleteTaskInfo(url);
            List<ThreadInfo> infos = sDBManager.queryThreadInfos(url);
            if (null != infos && infos.size() != 0) {
                sDBManager.deleteThreadInfos(url);
            }
        }
    }

    private class DLPrepare implements Runnable {
        private String url, dirPath;
        private DownloadTaskListener listener;

        private DLPrepare(String url, String dirPath, DownloadTaskListener listener) {
            this.url = url;
            this.dirPath = dirPath;
            this.listener = listener;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                String realUrl = url;
                conn = NetUtil.buildConnection(url);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty(HttpConnParameter.REFERER.content, url);
                if (conn.getResponseCode() == 302 ||
                        conn.getResponseCode() == 301) {
                    realUrl = conn.getHeaderField(HttpConnParameter.LOCATION.content);
                }

                if (sTaskDLing.containsKey(url)) {

                }
                else {
                    TaskInfo info = sDBManager.queryTaskInfoByUrl(url);

                    String fileName = FileUtil.getFileNameFromUrl(realUrl).replace("/", "");
                    File file = new File(dirPath, fileName);
                    if(file.exists()){
                        FileUtil.deleteFile(dirPath + File.separator + fileName);
                    }

                    String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
                    File unzipFile = new File(dirPath, fileNameWithoutExtension);
                    if(unzipFile.exists()){
                        FileUtil.deleteFile(dirPath + File.separator + fileNameWithoutExtension);
                    }

                    if(info != null){
                        sDBManager.deleteTaskInfo(info.baseUrl);
                        sTaskDLing.remove(info.baseUrl);
                    }

                    if (null != listener){
                        listener.onStart(fileName, realUrl);
                    }

                    if (null == info || !file.exists()) {
                        info = new TaskInfo(FileUtil.createFile(dirPath, fileName), url, realUrl, 0, 0);
                    }
                    DLTask task = new DLTask(info, listener);
                    mExecutor.execute(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
            }
        }
    }

    private class DLTask implements Runnable, DownloadThreadListener {
        private static final int LENGTH_PER_THREAD = 2097152;

        private TaskInfo info;
        private DownloadTaskListener mListener;

        private int totalProgress, fileLength;
        private int totalProgressIn100;
        private boolean isResume;
        private boolean isStop;
        private boolean isExists;
        private boolean isConnect = true;

        private List<ThreadInfo> mThreadInfos;

        private DLTask(TaskInfo info, DownloadTaskListener listener) {
            this.info = info;
            this.mListener = listener;
            this.totalProgress = info.progress;
            this.fileLength = info.length;

            if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
                if (!info.downLoadLocalFile.exists()) {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                }
                mThreadInfos = sDBManager.queryThreadInfos(info.baseUrl);
                if (null != mThreadInfos && mThreadInfos.size() != 0) {
                    isResume = true;
                } else {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                }
            }
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        @Override
        public void run() {
            if (NetUtil.getNetWorkType(context) == Constants.NetType.INVALID) {
                if (null != mListener)
                    mListener.onConnect(Constants.NetType.INVALID, "无网络连接");
                isConnect = false;
            } else if (NetUtil.getNetWorkType(context) == Constants.NetType.NO_WIFI) {
                if (null != mListener)
                    isConnect = mListener.onConnect(Constants.NetType.NO_WIFI, "正在使用非WIFI网络下载");
            }
            if (isConnect) {
                sTaskDLing.put(info.baseUrl, this);
                if (isResume) {
                    for (ThreadInfo i : mThreadInfos) {
                        mExecutor.execute(new DLThread(i, this));
                    }
                } else {
                    HttpURLConnection conn = null;
                    try {
                        conn = NetUtil.buildConnection(info.realUrl);
                        conn.setRequestProperty("Range", "bytes=" + 0 + "-" + Integer.MAX_VALUE);
                        if (conn.getResponseCode() == 206) {
                            fileLength = conn.getContentLength();
                            if (info.downLoadLocalFile.exists() && info.downLoadLocalFile.length() == fileLength) {
                                isExists = true;
                                sTaskDLing.remove(info.baseUrl);
                                if (null != mListener){
                                    mListener.onFinish(info.downLoadLocalFile);
                                };
                            }
                            if (!isExists) {
                                info.length = fileLength;
                                sDBManager.insertTaskInfo(info);
                                int threadSize;
                                int length = LENGTH_PER_THREAD;
                                if (fileLength <= LENGTH_PER_THREAD) {
                                    threadSize = 3;
                                    length = fileLength / threadSize;
                                } else {
                                    threadSize = fileLength / LENGTH_PER_THREAD;
                                }
                                int remainder = fileLength % length;
                                for (int i = 0; i < threadSize; i++) {
                                    int start = i * length;
                                    int end = start + length - 1;
                                    if (i == threadSize - 1) {
                                        end = start + length + remainder;
                                    }
                                    String id = UUID.randomUUID().toString();
                                    ThreadInfo ti = new ThreadInfo(info.downLoadLocalFile,
                                            info.baseUrl, info.realUrl, start, end, id);

                                    mExecutor.execute(new DLThread(ti, this));
                                }
                            }
                        } else if (conn.getResponseCode() == 200) {
                            fileLength = conn.getContentLength();
                            if (info.downLoadLocalFile.exists() && info.downLoadLocalFile.length() == fileLength) {
                                sTaskDLing.remove(info.baseUrl);
                                if (null != mListener) mListener.onFinish(info.downLoadLocalFile);
                            } else {
                                ThreadInfo ti = new ThreadInfo(info.downLoadLocalFile, info.baseUrl,
                                        info.realUrl, 0, fileLength, UUID.randomUUID().toString());
                                mExecutor.execute(new DLThread(ti, this));
                            }
                        }
                        else{
                            if (null != mListener) mListener.onError("400");
                        }
                    } catch (Exception e) {
                        if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
                            info.progress = totalProgress;
                            sDBManager.updateTaskInfo(info);
                            sTaskDLing.remove(info.baseUrl);
                        }
                        if (null != mListener) mListener.onError(e.getMessage());
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
            }
        }

        @Override
        public void onThreadProgress(int progress) {
            synchronized (this) {
                totalProgress += progress;
                int tmp = (int) (totalProgress * 1.0 / fileLength * 100);
                if (null != mListener && tmp != totalProgressIn100) {
                    mListener.onProgress(tmp, fileLength);
                    totalProgressIn100 = tmp;
                }
                if (fileLength == totalProgress) {
                    sDBManager.deleteTaskInfo(info.baseUrl);
                    sTaskDLing.remove(info.baseUrl);
                    if (null != mListener){
                        try {
                            if(info.realUrl.contains("yun-sdk")){
                                Unzip.unzip(info.downLoadLocalFile, new File(dirPath + "/play"));
                            }
                            else{
                                Unzip.unzip(info.downLoadLocalFile, new File(dirPath));
                            }

                        } catch (IOException e) {

                        }
                        FileUtil.deleteFile(info.downLoadLocalFile.getAbsolutePath());

                        mListener.onFinish(info.downLoadLocalFile);
                    };
                }
                if (isStop) {
                    info.progress = totalProgress;
                    sDBManager.updateTaskInfo(info);
                    sTaskDLing.remove(info.baseUrl);
                }
            }
        }

        private class DLThread implements Runnable {
            private ThreadInfo info;
            private DownloadThreadListener mListener;

            private int progress;

            public DLThread(ThreadInfo info, DownloadThreadListener listener) {
                this.info = info;
                this.mListener = listener;
            }

            @Override
            public void run() {
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    conn = NetUtil.buildConnection(info.realUrl);
                    conn.setRequestProperty("Range", "bytes=" + info.start + "-" + info.end);

                    raf = new RandomAccessFile(info.downLoadLocalFile,
                            Constants.AccessModes.ACCESS_MODE_RWD);
                    if (conn.getResponseCode() == 206) {
                        if (!isResume) {
                            sDBManager.insertThreadInfo(info);
                        }
                        is = conn.getInputStream();
                        raf.seek(info.start);
                        int total = info.end - info.start;
                        byte[] b = new byte[1024];
                        int len;
                        while (!isStop && (len = is.read(b)) != -1) {
                            raf.write(b, 0, len);
                            progress += len;
                            mListener.onThreadProgress(len);
                            if (progress >= total) {
                                sDBManager.deleteThreadInfoById(info.id);
                            }
                        }
                        if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
                            info.start = info.start + progress;
                            sDBManager.updateThreadInfo(info);
                        }
                    } else if (conn.getResponseCode() == 200) {
                        is = conn.getInputStream();
                        raf.seek(info.start);
                        byte[] b = new byte[1024];
                        int len;
                        while (!isStop && (len = is.read(b)) != -1) {
                            raf.write(b, 0, len);
                            mListener.onThreadProgress(len);
                        }
                    }
                } catch (Exception e) {
                    if (null != sDBManager.queryThreadInfoById(info.id)) {
                        info.start = info.start + progress;
                        sDBManager.updateThreadInfo(info);
                    }
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                        if (null != raf) {
                            raf.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != conn) {
                        conn.disconnect();
                    }
                }
            }
        }
    }
}
