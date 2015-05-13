package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.OpenCourseModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 公开课即将开始
 */
public class OpenCourseUnstartActivity extends ActionBarActivity implements RequestCallback{

    public Request mRequest;
    public ImageView mIvPic;
    public TextView mTvName;
    public TextView mTvTime;
    public TextView mTvLector;
    public TextView mTvNotice;
    public String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_unstart);

        // Toolbar
        CommonModel.setToolBar(this);

        // 成员变量初始化
        mRequest = new Request(this, this);

        // View 初始化
        mIvPic = (ImageView) findViewById(R.id.opencourse_img);
        mTvName = (TextView) findViewById(R.id.opencourse_name);
        mTvTime = (TextView) findViewById(R.id.opencourse_time);
        mTvLector = (TextView) findViewById(R.id.opencourse_lector);
        mTvNotice = (TextView) findViewById(R.id.opencourse_notice);

        // 获取数据
        mContent = getIntent().getStringExtra("content");
        ProgressDialogManager.showProgressDialog(this, true);
        mRequest.getOpenCourseDetail(mContent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("AnswerSheetActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("AnswerSheetActivity");
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
