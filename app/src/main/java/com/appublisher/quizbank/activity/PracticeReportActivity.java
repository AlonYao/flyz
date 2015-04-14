package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;

/**
 * 练习报告Activity
 */
public class PracticeReportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_report);

        // Toolbar
        CommonModel.setToolBar(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
