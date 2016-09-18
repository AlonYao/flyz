package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.customui.PaintView;
import com.appublisher.quizbank.model.business.CommonModel;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

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
            mPaintView.removeAllPaint();

        } else if (item.getTitle().equals("撤销")) {
            mPaintView.undo();

        } else if (item.getTitle().equals("恢复")) {
            mPaintView.redo();

        }

        return super.onOptionsItemSelected(item);
    }
}
