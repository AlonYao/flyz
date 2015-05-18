package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.OpenCourseModel;
import com.appublisher.quizbank.utils.UmengManager;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 公开课模块：没有公开课（查看往期）
 */
public class OpenCourseNoneActivity extends ActionBarActivity {

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
        ImageView ivNone = (ImageView) findViewById(R.id.opencourse_none_img);

        // 成员变量初始化
        mUmengTimestamp = System.currentTimeMillis();
        mUmengQQ = "0";
        mUmengVideoPlay = "0";

        // 获取数据
        final String content = getIntent().getStringExtra("content");
        mUmengEntry = getIntent().getStringExtra("umeng_entry");

        // 视频点击
        ivNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenCourseNoneActivity.this, WebViewActivity.class);
                intent.putExtra("url", content);
                startActivity(intent);

                // Umeng
                mUmengVideoPlay = "1";
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("OpenCourseNoneActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("OpenCourseNoneActivity");
        MobclickAgent.onPause(this);
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
            // Umeng
            mUmengQQ = "1";

            OpenCourseModel.setMarketQQ(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
