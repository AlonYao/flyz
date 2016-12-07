package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;

/**
 * 练习说明
 */
public class MeasureDescriptionActivity extends BaseActivity implements
        View.OnClickListener, MeasureConstants{

    private String mPaperType;
    private TextView mTvDesc;
    private TextView mTvName;
    private int mPaperId;
    private boolean mRedo;
    private int mHierarchyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_description);

        // Toolbar
        setToolBarWithoutBackBtn(this);

        // View 初始化
        mTvDesc = (TextView) findViewById(R.id.practicedesc_content);
        mTvName = (TextView) findViewById(R.id.practicedesc_name);
        Button btnStart = (Button) findViewById(R.id.practicedesc_start);
        LinearLayout llHide = (LinearLayout) findViewById(R.id.practicedesc_hide_ll);
        final CheckBox cbHide = (CheckBox) findViewById(R.id.practicedesc_hide_cb);

        // 获取数据
        mPaperType = getIntent().getStringExtra(INTENT_PAPER_TYPE);
        mPaperId = getIntent().getIntExtra(INTENT_PAPER_ID, 0);
        mRedo = getIntent().getBooleanExtra(INTENT_REDO, false);
        mHierarchyId = getIntent().getIntExtra(INTENT_HIERARCHY_ID, 0);

        if (mPaperType == null || mPaperType.length() == 0) finish();

        // 设置描述文字
        setDesc();

        boolean isHide = Globals.sharedPreferences.getBoolean(mPaperType, false);

        if (isHide) {
            skipToMeasureActivity();
        }

        // 不再显示
        if (llHide != null) {
            llHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cbHide != null) cbHide.performClick();
                }
            });
        }

        if (cbHide != null) {
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
        }

        // 开始答题
        if (btnStart != null) {
            btnStart.setOnClickListener(this);
        }
    }

    /**
     * 跳转到MeasureActivity
     */
    private void skipToMeasureActivity() {
        Intent intent = new Intent(this, MeasureActivity.class);
        intent.putExtra(INTENT_PAPER_TYPE, mPaperType);
        intent.putExtra(INTENT_PAPER_ID, mPaperId);
        intent.putExtra(INTENT_REDO, mRedo);
        intent.putExtra(INTENT_HIERARCHY_ID, mHierarchyId);
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
        if ("mokao".equals(mPaperType)) {
            mTvDesc.setText("每天15道精选练习\n老师人工挑选\n经典有代表");
            mTvName.setText("天天模考");
        } else if ("note".equals(mPaperType)) {
            mTvDesc.setText("专注练习专项\n集中突破难点\n针对性攻克弱项");
            mTvName.setText("专项练习");
        } else if ("auto".equals(mPaperType)) {
            mTvDesc.setText("智能组卷随手测\n有空就来刷一组");
            mTvName.setText("快速练习");
        } else if ("entire".equals(mPaperType)) {
            mTvDesc.setText("来一套真题测试下吧！\n要从头到尾认真做完哦！");
            mTvName.setText("整卷练习");
        } else if ("error".equals(mPaperType)) {
            mTvDesc.setText("抽取错题本试题\n看看这次能不能搞定");
            mTvName.setText("错题练习");
        } else if ("collect".equals(mPaperType)) {
            mTvDesc.setText("抽取收藏夹试题\n藏起来的重点题目\n当然要多练练");
            mTvName.setText("收藏练习");
        } else if ("evaluate".equals(mPaperType)) {
            String text = "考场上选什么这里就选什么";
            mTvDesc.setText(text);
            mTvName.setText("估分");
        } else if ("mock".equals(mPaperType)) {
            mTvDesc.setText("把模考当成正式考\n让正式考变成模考");
            mTvName.setText("模考");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practicedesc_start:
                skipToMeasureActivity();
                break;
        }
    }
}
