package com.appublisher.quizbank.common.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;

import com.appublisher.lib_basic.FileManager;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MediaRecorder录音
 *
 * @author huaxiao
 */
public class MediaRecorderManager {

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    public String mFileName;
    public String mPlayFileName;
    private Context mContext;
    private FileInputStream mFileInputStream;
    private CheckRecordStatusListener mCheckRecordStatusListener;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public MediaRecorderManager(Context context) {
        this.mContext = context;
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public MediaRecorderManager(Context context,
                                CheckRecordStatusListener checkRecordStatusListener) {
        this.mContext = context;
        this.mCheckRecordStatusListener = checkRecordStatusListener;
    }


    /**
     * 检查录音状态回调
     */
    public interface CheckRecordStatusListener {
        void onCheckRecordStatusFinished(boolean enableRecord);

    }

    /**
     * 当录音按钮被click时调用此方法，开始或停止录音
     *
     * @param start 是否开始
     */
    public void onRecord(boolean start) {
        if (start && mRecorder == null) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    /**
     * 当播放按钮被click时调用此方法，开始或停止播放
     *
     * @param start 是否停止
     */
    public void onPlay(boolean start, PlayOverMethod playOverMethod) {
        if (start && mPlayer == null) {
            startPlaying(playOverMethod);
        } else {
            stopPlaying(playOverMethod);
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ToastManager.showToastCenter(mContext, "录音中......");
    }

    /**
     * 开始录制
     *
     * @throws IOException
     */
    private void start() throws IOException, RuntimeException {
        mRecorder = new MediaRecorder();
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
        try {
            mRecorder.start();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        stop();
    }

    /**
     * 开始回放
     */
    private void startPlaying(final PlayOverMethod playOverMethod) {
        mPlayer = new MediaPlayer();
        try {
            // 设置要播放的文件
            if (mPlayFileName != null) {
                File file = new File(mPlayFileName);
                mFileInputStream = new FileInputStream(file);

                mPlayer.setDataSource(mFileInputStream.getFD());

                mPlayer.prepare();
                // 播放
                mPlayer.start();
                ToastManager.showToast(mContext, "开始播放");
                // 播放完成监听
                mPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mPlayer != null) {
                            mPlayer.release();
                            mPlayer = null;
                        }

                        if (mRecorder != null) {
                            mRecorder.release();
                            mRecorder = null;
                        }

                        if (mFileInputStream != null) {
                            try {
                                mFileInputStream.close();
                                mFileInputStream = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        playOverMethod.playOver(true);
                        ToastManager.showToastCenter(mContext, "音频播放完毕");
                    }
                });

                ToastManager.showToastCenter(mContext, "播放中......");
            } else {
                ToastManager.showToast(mContext, "找不到录音文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查录音状态
     */
    public void checkRecordStatus() {
        String fileNameFolder = mContext.getApplicationContext().getFilesDir().getAbsolutePath()
                + "/daily_interview/";
        FileManager.mkDir(fileNameFolder);
        mFileName = fileNameFolder + "test.amr";
        try {
            start();
        } catch (IOException e) {
            Logger.i("start IOException");
        } catch (RuntimeException e) {
            Logger.i("record permission");
        }

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 停止录音&播放
                stop();
                mPlayFileName = mFileName;
                try {
                    play();
                    mCheckRecordStatusListener.onCheckRecordStatusFinished(true);
                } catch (IOException e) {
                    // 异常处理
                    mCheckRecordStatusListener.onCheckRecordStatusFinished(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable, 500);
    }

    /**
     * 播放
     *
     * @throws Exception
     */
    private void play() throws Exception {
        mPlayer = new MediaPlayer();

        if (mPlayFileName == null) return;

        File file = new File(mPlayFileName);
        mFileInputStream = new FileInputStream(file);
        mPlayer.setDataSource(mFileInputStream.getFD());
        mPlayer.prepare();
        // 播放
        mPlayer.start();

        // 播放完成监听
        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
    }

    /**
     * 停止
     */
    public void stop() {

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
                mFileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止回放
     */
    private void stopPlaying(PlayOverMethod playOverMethod) {
        playOverMethod.playOver(false);
        stop();
    }

    public interface PlayOverMethod {
        public void playOver(boolean isPlay);
    }
}
