package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.ExamChangeActivity;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;

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

        return view;
    }
}
