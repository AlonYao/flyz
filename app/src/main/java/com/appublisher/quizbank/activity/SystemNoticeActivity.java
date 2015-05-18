package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.customui.XListView;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.SystemNoticeModel;
import com.appublisher.quizbank.model.netdata.notice.NoticeM;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 系统通知
 */
public class SystemNoticeActivity extends ActionBarActivity implements
        XListView.IXListViewListener, RequestCallback{

    public XListView mXListView;
    public ArrayList<NoticeM> mNotices;
    public int mOffset;
    public ImageView mCurRedPoint;
    public ImageView mIvNull;

    private int mCount;
    private Request mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notice);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        mXListView = (XListView) findViewById(R.id.notice_lv);
        mIvNull = (ImageView) findViewById(R.id.quizbank_null);

        // 成员变量初始化
        mOffset = 0;
        mCount = 10;
        mRequest = new Request(this, this);

        // XListView 配置
        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);

        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        mRequest.getNotifications(mOffset, mCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("SystemNoticeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("SystemNoticeActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivitySkipConstants.NOTICE_READ) {
            mCurRedPoint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mOffset = 0;
        mRequest.getNotifications(mOffset, mCount);
    }

    @Override
    public void onLoadMore() {
        mOffset = mOffset + mCount;
        mRequest.getNotifications(mOffset, mCount);
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
