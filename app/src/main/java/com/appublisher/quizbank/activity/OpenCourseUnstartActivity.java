package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 公开课即将开始
 */
public class OpenCourseUnstartActivity extends ActionBarActivity implements RequestCallback{

    public Request mRequest;
    public TextView mTvName;
    public TextView mTvTime;
    public TextView mTvLector;
    public TextView mTvNotice;
    public String mContent;
    public ImageView mIvOldtimey;

    /** Umeng **/
    private long mUmengTimestamp;
    private String mUmengEntry;
    private String mUmengQQ;
    public String mUmengPreSit;
    public String mUmengVideoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_unstart);

        // Toolbar
        CommonModel.setToolBar(this);

        // 成员变量初始化
        mRequest = new Request(this, this);
        mUmengTimestamp = System.currentTimeMillis();
        mUmengQQ = "0";
        mUmengPreSit = "0";
        mUmengVideoPlay = "0";

        // View 初始化
        mTvName = (TextView) findViewById(R.id.opencourse_name);
        mTvTime = (TextView) findViewById(R.id.opencourse_time);
        mTvLector = (TextView) findViewById(R.id.opencourse_lector);
        mTvNotice = (TextView) findViewById(R.id.opencourse_notice);
        mIvOldtimey = (ImageView) findViewById(R.id.opencourse_oldtimey);

        // 获取数据
        mContent = getIntent().getStringExtra("content");
        ProgressDialogManager.showProgressDialog(this, true);
        mRequest.getOpenCourseDetail(mContent);
        mUmengEntry = getIntent().getStringExtra("umeng_entry");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("AnswerSheetActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("AnswerSheetActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Entry", mUmengEntry);
        map.put("PreSit", mUmengPreSit);
        map.put("QQ", mUmengQQ);
        map.put("VideoPlay", mUmengVideoPlay);
        long dur = System.currentTimeMillis() - mUmengTimestamp;
        UmengManager.sendComputeEvent(this, "Reserve", map, (int) (dur/1000));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ActivitySkipConstants.BOOK_OPENCOURSE:
                // 预约公开课回调
                ProgressDialogManager.showProgressDialog(this, false);
                mRequest.bookOpenCourse(ParamBuilder.bookOpenCourse(mContent));
                break;

            case ActivitySkipConstants.OPENCOURSE_PRE:
                // 公开课回放
                mIvOldtimey.performClick();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("咨询"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if ("咨询".equals(item.getTitle())) {
            OpenCourseModel.setMarketQQ(this);
            mUmengQQ = "1"; // Umeng
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("open_course_detail".equals(apiName))
            OpenCourseModel.dealOpenCourseDetailResp(this, response);

        if ("book_open_course".equals(apiName))
            OpenCourseModel.dealBookOpenCourseResp(this, response);

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
