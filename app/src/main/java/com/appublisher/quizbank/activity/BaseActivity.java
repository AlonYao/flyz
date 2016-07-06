package com.appublisher.quizbank.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onResume(this);
        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPause(this);
        // TalkingData
        TCAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        ProgressDialogManager.closeProgressDialog();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置Toolbar
     * @param activity Activity
     */
    public void setToolBar(AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置Toolbar Title
     * @param activity Activity
     */
    public void setToolBarTitle(AppCompatActivity activity, String title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    /**
     * 设置返回键
     * @param activity AppCompatActivity
     */
    public void setDisplayHomeAsUpEnabled(AppCompatActivity activity, boolean display) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(display);
    }
}
