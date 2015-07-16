package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
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
import com.appublisher.quizbank.model.business.HomePageModel;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.images.DiskLruImageCache;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.network.ApiConstants;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import java.util.HashMap;

/**
 * 设置
 */
public class SettingFragment extends Fragment implements ApiConstants{

    private Activity mActivity;
    private TextView mTvSno;
    private TextView mTvExam;
    private TextView mTvCacheSize;
    private ImageView mIvRedPoint;
    private RelativeLayout mRlSno;
    private DiskLruImageCache mDiskLruImageCache;

    /** Umeng */
    private String mUmengAccount;
    private String mUmengMyTest;
    private String mUmengRemind;
    private String mUmengInforms;
    private String mUmengFeedback;
    private String mUmengFAQ;

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
        RelativeLayout rlDeleteCache =
                (RelativeLayout) view.findViewById(R.id.setting_delete_cache);
        CheckBox cbPush = (CheckBox) view.findViewById(R.id.setting_push_cb);
        mTvCacheSize = (TextView) view.findViewById(R.id.setting_delete_cache_size);
        mIvRedPoint = (ImageView) view.findViewById(R.id.setting_redpoint);
        mTvSno = (TextView) view.findViewById(R.id.setting_sno);
        mTvExam = (TextView) view.findViewById(R.id.setting_exam);
        mRlSno = (RelativeLayout) view.findViewById(R.id.setting_sno_rl);

        // 成员变量初始化
        mUmengAccount = "0";
        mUmengMyTest = "0";
        mUmengRemind = "0";
        mUmengInforms = "0";
        mUmengFeedback = "0";
        mUmengFAQ = "0";
        mDiskLruImageCache = new DiskLruImageCache(
                mActivity,
                DISK_IMAGECACHE_FOLDER,
                DISK_IMAGECACHE_SIZE,
                DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY);

        // 账号设置
        rlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UserInfoActivity.class);
                startActivity(intent);

                // Umeng
                mUmengAccount = "1";
            }
        });

        // 我的考试
        rlMyExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ExamChangeActivity.class);
                startActivity(intent);

                // Umeng
                mUmengMyTest = "1";
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
                // Umeng
                mUmengRemind = "1";
            }
        });

        // 系统通知
        rlNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SystemNoticeActivity.class);
                startActivity(intent);

                mIvRedPoint.setVisibility(View.GONE);

                // 侧边栏设置按钮红点消失
                ImageView ivSettingRedPoint = HomePageModel.getSettingRedPointView();

                if (ivSettingRedPoint == null) return;
                ivSettingRedPoint.setVisibility(View.GONE);

                // Umeng
                mUmengInforms = "1";
            }
        });

        // 用户反馈
        rlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAgent agent = new FeedbackAgent(mActivity);
                agent.startFeedbackActivity();

                // Umeng
                mUmengFeedback = "1";
            }
        });

        // 常见问题
        rlQa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, QaActivity.class);
                startActivity(intent);

                // Umeng
                mUmengFAQ = "1";
            }
        });

        // 手动清理缓存
        rlDeleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogManager.showProgressDialog(mActivity, false);
                mDiskLruImageCache.clearCache();
                ProgressDialogManager.closeProgressDialog();
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
            final UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);
            ExamItemModel examInfo = gson.fromJson(user.exam, ExamItemModel.class);

            // 学号
            if (userInfo != null) {
                mTvSno.setText(String.valueOf(userInfo.getSno()));
                mRlSno.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cm = (ClipboardManager)
                                mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(String.valueOf(userInfo.getSno()));

                        new AlertDialog.Builder(mActivity).setTitle("提示")
                                .setMessage("您的学号(" + cm.getText() + ")已经复制到剪切板~")
                                .setPositiveButton("好哒", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                });
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
        } else if (Globals.last_notice_id == 0){
            mIvRedPoint.setVisibility(View.GONE);
        } else {
            mIvRedPoint.setVisibility(View.VISIBLE);
        }

        // 获取缓存大小
        mTvCacheSize.setText(
                String.valueOf(mDiskLruImageCache.getCacheSize() / (1024*1024)) + "MB");

        // Umeng
        MobclickAgent.onPageStart("SettingFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "SettingFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("SettingFragment");

        // TalkingData
        TCAgent.onPageEnd(mActivity, "SettingFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Account", mUmengAccount);
        map.put("MyTest", mUmengMyTest);
        map.put("Remind", mUmengRemind);
        map.put("Informs", mUmengInforms);
        map.put("Feedback", mUmengFeedback);
        map.put("FAQ", mUmengFAQ);
        MobclickAgent.onEvent(mActivity, "Setting", map);
    }
}
