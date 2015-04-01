package com.appublisher.quizbank.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MediaRecorder录音
 * @author huaxiao
 *
 */
public class MediaRecorderManager {
	
	private MediaRecorder mRecorder;
	private Context mContext;
    private FileInputStream mFileInputStream;

    public MediaPlayer mPlayer;
    public String mFileName;
    public String mPlayFileName;
    public String mUri;

	public MediaRecorderManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 当录音按钮被click时调用此方法，开始或停止录音
	 * @param start
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
     * @param start
     */
    public void onPlay(boolean start) {  
        if (start && mPlayer == null) {
            startPlaying();
        } else {
            stopPlaying();  
        }  
    } 

    /**
     * 开始录音
     * 
     */
    private void startRecording() {
    	mRecorder = new MediaRecorder();  
        //设置音源为Micphone  
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
        //设置封装格式  
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);  
        mRecorder.setOutputFile(mFileName);  
        //设置编码格式  
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  

        try {  
            mRecorder.prepare();  
        } catch (IOException e) {  
        	e.printStackTrace();
        }  
  
        mRecorder.start();
        
        ToastManager.showToastCenter(mContext, "录音中......");
    } 
    
    /**
     * 停止录音
     * 
     */
	private void stopRecording() {
		release();
	}

    /**
     * 播放在线音频
     */
    public void onPlayUrl() {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                if (mUri != null) {
                    mPlayer.setDataSource(mContext, Uri.parse(mUri));
                    mPlayer.prepare();
                    mPlayer.start();
                }
            } else if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放本地资源文件
     * @param resid  资源文件id
     */
    public void playLocalVideo(int resid) {
        try {
            mPlayer = new MediaPlayer();
            mPlayer = MediaPlayer.create(mContext, resid);
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 开始回放
	 * 
	 */
	private void startPlaying() {
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
        				
        				ToastManager.showToastCenter(mContext, "音频播放完毕");
        			}
        		});
        		
        		ToastManager.showToastCenter(mContext, "播放中......");
			} else {
				ToastManager.showToast(mContext, "播放文件异常");
			}
        } catch (IOException e) {  
            e.printStackTrace();
            ToastManager.showToast(mContext, "播放异常");
        }
	}
	
	/**
	 * 停止回放
	 * 
	 */
	private void stopPlaying() {
		release();
	}
	
	/**
	 * Activity暂停时释放录音和播放对象
	 * 
	 */
	public void release() {
		if (mRecorder != null) {  
            mRecorder.release();  
            mRecorder = null;
            
            ToastManager.showToastCenter(mContext, "已停止录音");
        }  
  
        if (mPlayer != null) {  
            mPlayer.release();  
            mPlayer = null;
            
            ToastManager.showToastCenter(mContext, "已停止播放");
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
}
