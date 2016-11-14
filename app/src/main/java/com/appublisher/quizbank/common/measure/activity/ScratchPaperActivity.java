package com.appublisher.quizbank.common.measure.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.scratch.PaintView;

/**
 * 草稿纸
 */
public class ScratchPaperActivity extends BaseActivity {

    private PaintView mPaintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_paper);

        // Toolbar
        setToolBar(this);

        // View 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mPaintView = (PaintView) findViewById(R.id.scratch_paper_paintview);
        initPaintView(mPaintView);

        // 修改Toolbar icon
        initToolbar(toolbar);
    }

    private void initToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
    }

    private void initPaintView(PaintView paintView) {
        if (paintView == null) return;
        paintView.setColor(Color.parseColor("#262B2D"));
        paintView.setStrokeWidth(4);
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
            mPaintView.clear();

        } else if (item.getTitle().equals("撤销")) {
            mPaintView.undo();

        } else if (item.getTitle().equals("恢复")) {
            mPaintView.redo();

        }

        return super.onOptionsItemSelected(item);
    }
}
