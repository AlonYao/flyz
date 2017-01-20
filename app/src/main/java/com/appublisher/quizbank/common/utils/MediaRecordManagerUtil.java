package com.appublisher.quizbank.common.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jinbao on 2017/1/18.
 */

public class MediaRecordManagerUtil implements TimerUtil.OnTimerCallback {

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private TimerUtil mTimerUtil;
    private String mRecordFilePath;
    private String mPlayFilePath;
    private FileInputStream mPlayFileInputStream;
    private int mRecordDuration = 0;
    private IRecordDurationCallback mIRecordDurationCallback;

    public MediaRecordManagerUtil() {
        mTimerUtil = new TimerUtil(this);
    }

    @Override
    public void onTimerTick() {
        if (mIRecordDurationCallback != null)
            mIRecordDurationCallback.onRecordDuration(++mRecordDuration);
    }

    public void checkRecordStatus(final ICheckRecordStatusListener iCheckRecordStatusListener) {
        String filePath = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/test";
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            Log.i("file", "==" + file.mkdirs());
        }
        filePath = filePath + "/test.amr";
        setRecordFilePath(filePath);
        try {
            startRecord();
        } catch (IOException e) {
            e.printStackTrace();
            if (iCheckRecordStatusListener != null)
                iCheckRecordStatusListener.onCheckRecordStatusFinished(false);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (iCheckRecordStatusListener != null)
                iCheckRecordStatusListener.onCheckRecordStatusFinished(false);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopReocrd();
                if (iCheckRecordStatusListener != null)
                    iCheckRecordStatusListener.onCheckRecordStatusFinished(true);
            }
        }, 20);

    }

    public void setRecordFilePath(String filePath) {
        this.mRecordFilePath = filePath;
    }

    public void setPlayFilePath(String filePath) {
        this.mPlayFilePath = filePath;
    }

    /**
     * 开始录音
     * 内部调用
     *
     * @throws IOException
     */
    public void startRecord() throws IOException {
        if (mRecordFilePath == null || "".equals(mRecordFilePath))
            return;
        if (mRecorder != null) return;
        File file = new File(mRecordFilePath);
        if (file.exists())
            file.delete();
        mRecorder = new MediaRecorder();
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mRecordFilePath);
        mRecorder.prepare();

        try {
            mRecordDuration = 0;
            mRecorder.start();
            mTimerUtil.start();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 开始录音
     * 外部调用
     *
     * @param iRecordDurationCallback
     */
    public void startRecord(IRecordDurationCallback iRecordDurationCallback) {
        try {
            this.mIRecordDurationCallback = iRecordDurationCallback;
            startRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopReocrd() {
        mTimerUtil.stop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * 开始播放
     *
     * @param offset
     * @param iPlayCompleteCallback
     */
    public void startPlay(int offset, final IPlayCompleteCallback iPlayCompleteCallback) {
        if (mPlayFilePath == null || "".equals(mPlayFilePath)) return;
        if (offset < 0) return;
        if (mPlayer != null) return;
        mPlayer = new MediaPlayer();
        try {
            File file = new File(mPlayFilePath);
            mPlayFileInputStream = new FileInputStream(file);
            mPlayer.setDataSource(mPlayFileInputStream.getFD());
            mPlayer.prepare();
            mPlayer.seekTo(offset);
            Log.i("dur", "===" + mPlayer.getDuration());

            // 播放
            mPlayer.start();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay();
                    if (iPlayCompleteCallback != null)
                        iPlayCompleteCallback.onPlayComplete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            //播放文件地址有误
        }
    }

    public void startPlay() {

    }

    /**
     * 暂停播放，返回断点值
     *
     * @param iPlayFileOffsetCallback
     */
    public void playOnPause(IPlayFileOffsetCallback iPlayFileOffsetCallback) {
        if (mPlayer != null) {
            if (iPlayFileOffsetCallback != null)
                iPlayFileOffsetCallback.onPlayOffset(mPlayer.getCurrentPosition());
            mPlayer.release();
            mPlayer = null;
        }

        if (mPlayFileInputStream != null) {
            try {
                mPlayFileInputStream.close();
                mPlayFileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlay() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        if (mPlayFileInputStream != null) {
            try {
                mPlayFileInputStream.close();
                mPlayFileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 录音时长回调
     */
    public interface IRecordDurationCallback {
        void onRecordDuration(int duration);
    }

    /**
     * 检查录音权限
     */
    public interface ICheckRecordStatusListener {
        void onCheckRecordStatusFinished(boolean enableRecord);
    }

    /**
     * 播放断点回调
     */
    public interface IPlayFileOffsetCallback {
        void onPlayOffset(int offset);
    }

    /**
     * 播放完成
     */
    public interface IPlayCompleteCallback {
        void onPlayComplete();
    }
}
