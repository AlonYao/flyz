package com.appublisher.quizbank.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.ExamListAdapter;
import com.appublisher.quizbank.adapter.NoticeListAdapter;
import com.appublisher.quizbank.model.CommonModel;

/**
 * 系统通知
 */
public class SystemNoticeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notice);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        ListView lv = (ListView) findViewById(R.id.notice_lv);
        NoticeListAdapter noticeListAdapter = new NoticeListAdapter(this);
        lv.setAdapter(noticeListAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
