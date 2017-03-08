package com.appublisher.quizbank.common.interview.netdata;

import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.customui.RoundProgressBarWidthNumber;

/**
 * 面试页面控件bean
 */
public class InterviewControlsStateBean {
    private String state;
    private ControlsViewBean controlsViewBean = new ControlsViewBean();
    private int offset;
    private String mediaName;
    private int totalDuration;

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public ControlsViewBean getControlsViewBean() {
        return controlsViewBean;
    }
    public void setControlsViewBean(ControlsViewBean controlsViewBean) {
        this.controlsViewBean = controlsViewBean;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public class ControlsViewBean{
        private boolean isFirstCreate = true;
        private RoundProgressBarWidthNumber progressBar;
        private ImageView progressBarTimeIv;
        private TextView progressBarTimeTv;
        private TextView progressBarStateTv;

        public boolean isFirstCreate() {
            return isFirstCreate;
        }

        public void setFirstCreate(boolean firstCreate) {
            isFirstCreate = firstCreate;
        }

        public RoundProgressBarWidthNumber getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(RoundProgressBarWidthNumber progressBar) {
            this.progressBar = progressBar;
        }

        public ImageView getProgressBarTimeIv() {
            return progressBarTimeIv;
        }

        public void setProgressBarTimeIv(ImageView progressBarTimeIv) {
            this.progressBarTimeIv = progressBarTimeIv;
        }

        public TextView getProgressBarTimeTv() {
            return progressBarTimeTv;
        }

        public void setProgressBarTimeTv(TextView progressBarTimeTv) {
            this.progressBarTimeTv = progressBarTimeTv;
        }

        public TextView getProgressBarStateTv() {
            return progressBarStateTv;
        }

        public void setProgressBarStateTv(TextView progressBarStateTv) {
            this.progressBarStateTv = progressBarStateTv;
        }
    }
}
