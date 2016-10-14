package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;

public class VipExerciseDescriptionActivity extends BaseActivity {

    private TextView descriptionText;
    private View descriptionHideView;
    private CheckBox descriptionCheckBox;
    private Button beginExercise;
    //考虑from和exerciseType是否重复
    private String mFrom;
    private int exerciseId;
    private int exerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_exercise_description);

        setToolBar(this);

        mFrom = getIntent().getStringExtra("from");
        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        exerciseType = getIntent().getIntExtra("exerciseType", -1);

        descriptionText = (TextView) findViewById(R.id.vip_description_text);
        descriptionHideView = findViewById(R.id.vip_description_hide_view);
        descriptionCheckBox = (CheckBox) findViewById(R.id.vip_description_hide_cb);
        beginExercise = (Button) findViewById(R.id.begin_exercise);

        setValue();
    }

    public void setValue() {
        Logger.i("exerciseType=" + exerciseType);
        if (exerciseType == 5) {
            descriptionText.setText(getResources().getString(R.string.vip_description_bdgx));
        } else if (exerciseType == 6) {
            descriptionText.setText(getResources().getString(R.string.vip_description_yytl));
        } else if (exerciseType == 7) {
            descriptionText.setText(getResources().getString(R.string.vip_description_yddk));
        }

        descriptionHideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionCheckBox.isChecked()) {
                    descriptionCheckBox.setChecked(false);
                } else {
                    descriptionCheckBox.setChecked(true);
                }
            }
        });

        beginExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipExercise();
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    public void skipExercise() {
        Class<?> cls = null;
        switch (exerciseType) {
            case 1:
                // 名师精批
                if (descriptionCheckBox.isChecked()) {
                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putBoolean("vip_description_msjp", true);
                    editor.commit();
                }
                cls = VipMSJPActivity.class;
                break;
            case 2:
                // 单题突破
                break;
            case 3:
                // 字迹诊断
                break;
            case 4:
                // 词句摘抄
                break;
            case 5:
                // 表达改写
                if (descriptionCheckBox.isChecked()) {
                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putBoolean("vip_description_bdgx", true);
                    editor.commit();
                }
                cls = VipBDGXActivity.class;
                break;
            case 6:
                // 语义提炼
                if (descriptionCheckBox.isChecked()) {
                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putBoolean("vip_description_yytl", true);
                    editor.commit();
                }
                cls = VipBDGXActivity.class;
                break;
            case 7:
                // 阅读打卡
                if (descriptionCheckBox.isChecked()) {
                    SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                    editor.putBoolean("vip_description_yddk", true);
                    editor.commit();
                }
                cls = VipYDDKActivity.class;
                break;
            case 8:
                // 行测_智能组卷
                break;
            case 9:
                // 互评提升
                break;
            default:
                break;
        }
        if (cls != null) {
            final Intent intent = new Intent(this, cls);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("exerciseType", exerciseType);
            startActivity(intent);
            finish();
        }
    }
}
