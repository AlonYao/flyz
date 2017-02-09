package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;

public class TeacherRemarkGuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_remark_guide);
        setToolBar(this);
        setTitle("");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        setDisplayHomeAsUpEnabled(this, false);
        // 名师点评引导页
        MenuItemCompat.setShowAsAction(
                menu.add("关闭"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("关闭".equals(item.getTitle())) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
