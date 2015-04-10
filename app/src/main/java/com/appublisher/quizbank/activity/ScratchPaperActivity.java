package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;

public class ScratchPaperActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_paper);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 修改Toolbar icon
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add("清空").setIcon(
                R.drawable.scratch_paper_clear), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("撤销").setIcon(
                R.drawable.scratch_paper_undo), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add("恢复").setIcon(
                R.drawable.scratch_paper_redo), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        } else if (item.getTitle().equals("清空")) {


        } else if (item.getTitle().equals("撤销")) {


        } else if (item.getTitle().equals("恢复")) {


        }

        return super.onOptionsItemSelected(item);
    }
}
