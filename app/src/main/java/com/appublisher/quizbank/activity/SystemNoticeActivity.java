package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.SystemNoticeModel;
import com.appublisher.quizbank.model.netdata.notice.NoticeM;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 系统通知
 */
public class SystemNoticeActivity extends BaseActivity implements
        XListView.IXListViewListener, RequestCallback {

    public XListView mXListView;
    public ArrayList<NoticeM> mNotices;
    public int mOffset;
    public ImageView mIvNull;

    private int mCount;
    private QRequest mQRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notice);

        // ToolBar
        setToolBar(this);

        // View 初始化
        mXListView = (XListView) findViewById(R.id.notice_lv);
        mIvNull = (ImageView) findViewById(R.id.quizbank_null);

        // 成员变量初始化
        mOffset = 0;
        mCount = 10;
        mQRequest = new QRequest(this, this);

        // XListView 配置
        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);

        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        mQRequest.getNotifications(mOffset, mCount);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mOffset = 0;
        mQRequest.getNotifications(mOffset, mCount);
    }

    @Override
    public void onLoadMore() {
        mOffset = mOffset + mCount;
        mQRequest.getNotifications(mOffset, mCount);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("notifications".equals(apiName))
            SystemNoticeModel.dealNotificationsResp(this, response);

        setLoadFinish();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        setLoadFinish();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        setLoadFinish();
    }

    /**
     * 加载结束
     */
    private void setLoadFinish() {
        onLoadFinish();
        SystemNoticeModel.showNullImg(this);
        ProgressDialogManager.closeProgressDialog();
    }

    /**
     * 刷新&加载结束时执行的操作
     */
    @SuppressLint("SimpleDateFormat")
    public void onLoadFinish() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
        mXListView.setRefreshTime(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
