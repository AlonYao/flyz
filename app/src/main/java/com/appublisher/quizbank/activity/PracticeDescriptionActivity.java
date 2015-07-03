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
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.ExerciseIntroM;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 练习说明
 */
public class PracticeDescriptionActivity extends ActionBarActivity {

    private String mPaperType;
    private String mPaperName;
    private TextView mTvDesc;
    private TextView mTvName;
    private int mPaperId;
    private boolean mRedo;
    private int mHierarchyId;
    private int mHierarchyLevel;
    private String mNoteType;
    private String mUmengEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_description);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        mTvDesc = (TextView) findViewById(R.id.practicedesc_content);
        mTvName = (TextView) findViewById(R.id.practicedesc_name);
        TextView tvStart = (TextView) findViewById(R.id.practicedesc_start);
        LinearLayout llHide = (LinearLayout) findViewById(R.id.practicedesc_hide_ll);
        final CheckBox cbHide = (CheckBox) findViewById(R.id.practicedesc_hide_cb);

        // 获取数据
        mPaperType = getIntent().getStringExtra("paper_type");
        mPaperName = getIntent().getStringExtra("paper_name");
        mPaperId = getIntent().getIntExtra("paper_id", 0);
        mRedo = getIntent().getBooleanExtra("redo", false);
        mHierarchyId = getIntent().getIntExtra("hierarchy_id", 0);
        mHierarchyLevel = getIntent().getIntExtra("hierarchy_level", 0);
        mNoteType = getIntent().getStringExtra("note_type");
        mUmengEntry = getIntent().getStringExtra("umeng_entry");

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
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("PracticeDescriptionActivity");
        MobclickAgent.onResume(this);

        // TalkingData
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("PracticeDescriptionActivity");
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);
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
        intent.putExtra("hierarchy_id", mHierarchyId);
        intent.putExtra("hierarchy_level", mHierarchyLevel);
        intent.putExtra("note_type", mNoteType);
        intent.putExtra("umeng_entry", mUmengEntry);
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
            String name = exerciseIntro.getName();

            if (type == null || desc == null) continue;

            if (type.equals(mPaperType)) {
                mTvDesc.setText(desc.replaceAll("，", "\n")
                        .replaceAll("!", "\n")
                        .replaceAll("！", "\n")
                        .replaceAll(";", "\n")
                        .replaceAll("；", "\n"));
                mTvName.setText(name);
            }
        }
    }
}
