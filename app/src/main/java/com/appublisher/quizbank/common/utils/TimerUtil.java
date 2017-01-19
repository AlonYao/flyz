package com.appublisher.quizbank.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {

    private final int KTimerInterval = 1000;

    private Timer mTimer;
    private RedrawHandler mHandler = null;
    private long mDelay = 0;
    private OnTimerCallback mCallback;

    public TimerUtil(OnTimerCallback callback) {
        mCallback = callback;
        mHandler = new RedrawHandler();
    }


    public void setAttrs(long delay) {
        mDelay = delay;
    }

    public void start() {
        removeTimer();
        MyTimerTask task = new MyTimerTask();
        mTimer = new Timer();
        mTimer.schedule(task, mDelay, KTimerInterval);
    }

    public void stop() {
        removeTimer();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    private void removeTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class RedrawHandler extends Handler {
        public RedrawHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mCallback.onTimerTick();
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            mHandler.sendMessage(message);
        }
    }
    public interface OnTimerCallback {
        public void onTimerTick();
    }

}
