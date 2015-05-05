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
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.ExerciseIntroM;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 练习说明
 */
public class PracticeDescriptionActivity extends ActionBarActivity {

    private String mPaperType;
    private String mPaperName;
    private TextView mTvDesc;
    private int mPaperId;
    private boolean mRedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_description);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mTvDesc = (TextView) findViewById(R.id.practicedesc_content);
        TextView tvStart = (TextView) findViewById(R.id.practicedesc_start);
        LinearLayout llHide = (LinearLayout) findViewById(R.id.practicedesc_hide_ll);
        final CheckBox cbHide = (CheckBox) findViewById(R.id.practicedesc_hide_cb);

        // 获取数据
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mRedo = getIntent().getBooleanExtra("redo", false);

        // 设置描述文字
        setDesc();

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
        intent.putExtra("paper_id", mPaperId);
        intent.putExtra("redo", mRedo);
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

    /**
     * 设置描述文字
     */
    private void setDesc() {
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        if (globalSetting == null) return;

        String content = globalSetting.content;

        Gson gson = GsonManager.initGson();
        GlobalSettingsResp globalSettingsResp = gson.fromJson(content, GlobalSettingsResp.class);

        if (globalSettingsResp == null || globalSettingsResp.getResponse_code() != 1) return;

        ArrayList<ExerciseIntroM> exerciseIntros = globalSettingsResp.getExercise_intro();

        if (exerciseIntros == null || exerciseIntros.size() == 0) return;

        int size = exerciseIntros.size();
        for (int i = 0; i < size; i++) {
            ExerciseIntroM exerciseIntro = exerciseIntros.get(i);

            if (exerciseIntro == null) continue;

            String desc = exerciseIntro.getIntro();
            String type = exerciseIntro.getType();

            if (type != null && mPaperType.equals(type) && desc != null) {
                mTvDesc.setText(desc.replaceAll("，", "\n"));
            }
        }
    }
}
