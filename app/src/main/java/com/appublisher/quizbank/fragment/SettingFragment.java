package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.ExamChangeActivity;
import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.parse.ParsePush;

/**
 * 设置
 */
public class SettingFragment extends Fragment{

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        RelativeLayout rlAccount = (RelativeLayout) view.findViewById(R.id.setting_account);
        RelativeLayout rlMyExam = (RelativeLayout) view.findViewById(R.id.setting_myexam);
        RelativeLayout rlNotice = (RelativeLayout) view.findViewById(R.id.setting_notice);
        CheckBox cbPush = (CheckBox) view.findViewById(R.id.setting_push_cb);

        // 账号设置
        rlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        // 我的考试
        rlMyExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ExamChangeActivity.class);
                startActivity(intent);
            }
        });

        // 学习提醒
        boolean isPushOpen = Globals.sharedPreferences.getBoolean("isPushOpen", true);
        if (isPushOpen) {
            cbPush.setChecked(true);
        } else {
            cbPush.setChecked(false);
        }

        cbPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                if (isChecked) {
                    // 执行选中动作
                    ParsePush.subscribeInBackground("");
                    editor.putBoolean("isPushOpen", true);
                } else {
                    // 执行取消选中动作
                    ParsePush.unsubscribeInBackground("");
                    editor.putBoolean("isPushOpen", false);
                }
                editor.commit();
            }
        });

        // 系统通知
        rlNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SystemNoticeActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
