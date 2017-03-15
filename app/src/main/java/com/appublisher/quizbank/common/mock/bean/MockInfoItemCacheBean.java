package com.appublisher.quizbank.common.mock.bean;

import android.widget.Button;
import android.widget.TextView;

import com.appublisher.quizbank.model.netdata.mock.MockPreResp;

/**
 * 用于缓存模考信息页面模考item中的相关内容
 */

public class MockInfoItemCacheBean {

    private Button btnStatus;
    private TextView tvTimer;
    private int hour;
    private int min;
    private int sec;
    private MockPreResp.MockListBean mockListBean;

    public MockPreResp.MockListBean getMockListBean() {
        return mockListBean;
    }

    public void setMockListBean(MockPreResp.MockListBean mockListBean) {
        this.mockListBean = mockListBean;
    }

    public Button getBtnStatus() {
        return btnStatus;
    }

    public void setBtnStatus(Button btnStatus) {
        this.btnStatus = btnStatus;
    }

    public TextView getTvTimer() {
        return tvTimer;
    }

    public void setTvTimer(TextView tvTimer) {
        this.tvTimer = tvTimer;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }
}
