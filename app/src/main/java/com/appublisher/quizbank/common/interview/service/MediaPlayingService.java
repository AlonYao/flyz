package com.appublisher.quizbank.common.interview.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import com.appublisher.lib_basic.Logger;

public class MediaPlayingService extends Service {
    private AudioManager mAm;
    private MyOnAudioFocusChangeListener mListener;
    private Intent mAudioFocusIntent = new Intent("com.appublisher.quizbank.common.interview.fragment.AUDIOSTREAMFOCUSRECEIVER");  // 注册

    @Override
    public void onCreate(){
        mAm = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE); // 注册管理器
        mListener = new MyOnAudioFocusChangeListener();
    }
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onStart(Intent intent, int startid){
        int result = mAm.requestAudioFocus(mListener,           // 请求焦点
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){ // 成功获取到了焦点
            Logger.e("requestAudioFocus successfully.");
            // 发送广播通知activity中播放器可以播放
            mAudioFocusIntent.putExtra("isGetAudioFocus", true);
            sendBroadcast(mAudioFocusIntent);
        }
        else{
            Logger.e("requestAudioFocus failed.");
            mAudioFocusIntent.putExtra("isGetAudioFocus", false);
            sendBroadcast(mAudioFocusIntent);
        }
    }

    private class MyOnAudioFocusChangeListener implements
            AudioManager.OnAudioFocusChangeListener{
        @Override
        public void onAudioFocusChange(int focusChange){     // 音频焦点的变化
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {    // 短暂停
                // 通知activity播放器暂停播放 :都需要判断播放器的状态
                mAudioFocusIntent.putExtra("isGetAudioFocus", false);
                sendBroadcast(mAudioFocusIntent);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {       // 恢复焦点
                // 通知activity播放器继续播放
                mAudioFocusIntent.putExtra("isGetAudioFocus", true);
                sendBroadcast(mAudioFocusIntent);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {       // 长久失去焦点
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                mAm.abandonAudioFocus(mListener);
                // 通知activity播放器停止播放
                mAudioFocusIntent.putExtra("isGetAudioFocus", false);
                sendBroadcast(mAudioFocusIntent);
            }
        }
    }
    @Override
    public void onDestroy(){
        mAm.abandonAudioFocus(mListener);
    }
}
