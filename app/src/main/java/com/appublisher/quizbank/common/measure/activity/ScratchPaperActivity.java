package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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

        showHintAlert();
    }

    @SuppressWarnings("deprecation")
    private void initToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themecolor));
    }

    private void initPaintView(PaintView paintView) {
        if (paintView == null) return;
        paintView.setColor(Color.parseColor("#262B2D"));
        paintView.setStrokeWidth(4);
    }

    private void showHintAlert() {
        final SharedPreferences sharedPreferences =
                getSharedPreferences("yaoguo_measure", MODE_PRIVATE);
        boolean isShowScratchHint = sharedPreferences.getBoolean("is_show_scratch_hint", true);
        if (!isShowScratchHint) return;

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.measure_scratch_hint);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView textView = (TextView) window.findViewById(R.id.measure_scratch_hint_btn);

        textView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_show_scratch_hint", false);
                editor.commit();
                alertDialog.dismiss();
            }
        });
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
