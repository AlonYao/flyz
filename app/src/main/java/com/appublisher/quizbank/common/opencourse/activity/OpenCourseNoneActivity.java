package com.appublisher.quizbank.common.opencourse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.utils.UmengManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 公开课模块：没有公开课（查看往期）
 */
public class OpenCourseNoneActivity extends ActionBarActivity {

    private ImageView mIvNone;

    /** Umeng **/
    private long mUmengTimestamp;
    private String mUmengEntry;
    private String mUmengQQ;
    private String mUmengVideoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_none);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mIvNone = (ImageView) findViewById(R.id.opencourse_none_img);

        // 成员变量初始化
        mUmengTimestamp = System.currentTimeMillis();
        mUmengQQ = "0";
        mUmengVideoPlay = "0";

        // 获取数据
        mUmengEntry = getIntent().getStringExtra("umeng_entry");

        // 视频点击
        mIvNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = LoginModel.getUserMobile();
                if (mobileNum == null || mobileNum.length() == 0) {
                    // 没有手机号
                    Intent intent =
                            new Intent(OpenCourseNoneActivity.this, BindingMobileActivity.class);
                    intent.putExtra("from", "opencourse_pre");
                    startActivityForResult(intent, ActivitySkipConstants.OPENCOURSE_PRE);

                } else {
                    // 跳转
                    String content = getIntent().getStringExtra("content");
                    OpenCourseModel.skipToPreOpenCourse(OpenCourseNoneActivity.this, content, "");

                    // Umeng
                    mUmengVideoPlay = "1";
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("OpenCourseNoneActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("OpenCourseNoneActivity");
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
        map.put("VideoPlay", mUmengVideoPlay);
        map.put("QQ", mUmengQQ);
        long dur = System.currentTimeMillis() - mUmengTimestamp;
        UmengManager.sendComputeEvent(this, "Playback", map, (int) (dur/1000));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ActivitySkipConstants.OPENCOURSE_PRE:
                mIvNone.performClick();
                break;
        }
    }
}
