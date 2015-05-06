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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.ExamChangeActivity;
import com.appublisher.quizbank.activity.QaActivity;
import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;
import com.parse.ParsePush;
import com.umeng.fb.FeedbackAgent;

/**
 * 设置
 */
public class SettingFragment extends Fragment{

    private Activity mActivity;
    private TextView mTvSno;
    private TextView mTvExam;
    private ImageView mIvRedPoint;

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
        RelativeLayout rlFeedback = (RelativeLayout) view.findViewById(R.id.setting_feedback);
        RelativeLayout rlQa = (RelativeLayout) view.findViewById(R.id.setting_qa);
        CheckBox cbPush = (CheckBox) view.findViewById(R.id.setting_push_cb);
        mIvRedPoint = (ImageView) view.findViewById(R.id.setting_redpoint);
        mTvSno = (TextView) view.findViewById(R.id.setting_sno);
        mTvExam = (TextView) view.findViewById(R.id.setting_exam);

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

                mIvRedPoint.setVisibility(View.GONE);
            }
        });

        // 用户反馈
        rlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAgent agent = new FeedbackAgent(mActivity);
                agent.startFeedbackActivity();
            }
        });

        // 常见问题
        rlQa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, QaActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 显示学号&考试项目
        User user = UserDAO.findById();
        if (user != null) {
            Gson gson = GsonManager.initGson();
            UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);
            ExamItemModel examInfo = gson.fromJson(user.exam, ExamItemModel.class);

            // 学号
            if (userInfo != null) {
                mTvSno.setText(String.valueOf(userInfo.getSno()));
            }

            // 考试项目
            if (examInfo != null) {
                mTvExam.setText(examInfo.getName());
            }
        }

        // 显示系统通知红点
        GlobalSetting globalSetting = GlobalSettingDAO.findById();
        if (globalSetting != null && globalSetting.latest_notify == Globals.last_notice_id) {
            mIvRedPoint.setVisibility(View.GONE);
        } else {
            mIvRedPoint.setVisibility(View.VISIBLE);
        }
    }
}
