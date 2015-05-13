package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.umeng.analytics.MobclickAgent;

/**
 * 公开课模块：没有公开课（查看往期）
 */
public class OpenCourseNoneActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_none);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        ImageView ivNone = (ImageView) findViewById(R.id.opencourse_none_img);

        // 获取数据
        final String content = getIntent().getStringExtra("content");

        // 视频点击
        ivNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenCourseNoneActivity.this, WebViewActivity.class);
                intent.putExtra("url", content);
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
