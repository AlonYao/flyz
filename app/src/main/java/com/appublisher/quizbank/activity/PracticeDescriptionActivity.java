package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;

/**
 * 练习说明
 */
public class PracticeDescriptionActivity extends ActionBarActivity {

    private String mPaperType;
    private String mPaperName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_description);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        TextView tvDesc = (TextView) findViewById(R.id.practicedesc_content);
        TextView tvStart = (TextView) findViewById(R.id.practicedesc_start);
        LinearLayout llHide = (LinearLayout) findViewById(R.id.practicedesc_hide_ll);
        final CheckBox cbHide = (CheckBox) findViewById(R.id.practicedesc_hide_cb);

        // 获取数据
        String desc = getIntent().getStringExtra("desc");
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");

        if (desc == null) desc = "";
        tvDesc.setText(desc.replaceAll("，", "/n"));

        if (mPaperType == null || mPaperType.length() == 0) finish();

        boolean isHide = Globals.sharedPreferences.getBoolean(mPaperType, false);
        if (isHide) {
            skipToMeasureActivity();
        }

        // 不再显示
        llHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbHide.performClick();
            }
        });

        cbHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateLocal(true);
                } else {
                    updateLocal(false);
                }
            }
        });

        // 开始答题
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToMeasureActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 跳转到MeasureActivity
     */
    private void skipToMeasureActivity() {
        Intent intent = new Intent(this, MeasureActivity.class);
        intent.putExtra("paper_type", mPaperType);
        intent.putExtra("paper_name", mPaperName);
        startActivity(intent);
        finish();
    }

    /**
     * 更新本地状态
     * @param isHide 是否隐藏
     */
    @SuppressLint("CommitPrefEdits")
    private void updateLocal(boolean isHide) {
        if (mPaperType == null || mPaperType.length() == 0) return;

        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putBoolean(mPaperType, isHide);
        editor.commit();
    }
}
