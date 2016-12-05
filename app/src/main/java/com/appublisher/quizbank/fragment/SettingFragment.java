package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.activity.ExamChangeActivity;
import com.appublisher.lib_login.activity.UserInfoActivity;
import com.appublisher.lib_login.model.db.User;
import com.appublisher.lib_login.model.db.UserDAO;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.QaActivity;
import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.images.DiskLruImageCache;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.network.QApiConstants;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 设置
 */
public class SettingFragment extends Fragment implements QApiConstants {

    private Activity mActivity;
    private TextView mTvSno;
    private TextView mTvExam;
    private TextView mTvCacheSize;
    private TextView mTvVersion;
    private ImageView mIvRedPoint;
    private RelativeLayout mRlSno;
    private DiskLruImageCache mDiskLruImageCache;

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
        mTvVersion = (TextView) view.findViewById(R.id.setting_version_code);
        mRlSno = (RelativeLayout) view.findViewById(R.id.setting_sno_rl);

        // 成员变量初始化
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
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Account");
                UmengManager.onEvent(getContext(), "Setting", map);
            }
        });

        // 我的考试
        rlMyExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ExamChangeActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "MyTest");
                UmengManager.onEvent(getContext(), "Setting", map);
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
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Remind");
                UmengManager.onEvent(getContext(), "Setting", map);
            }
        });

        // 系统通知
        rlNotice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SystemNoticeActivity.class);
                startActivity(intent);

                mIvRedPoint.setVisibility(View.GONE);

                // 更新缓存
                SharedPreferences spf = getContext().getSharedPreferences(
                        "global_setting", Context.MODE_PRIVATE);
                String cache = spf.getString("global_setting", "");
                GlobalSettingsResp resp = GsonManager.getModel(cache, GlobalSettingsResp.class);
                if (resp != null && resp.getResponse_code() == 1) {
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putInt("last_notice_id", resp.getLatest_notify());
                    editor.commit();
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Informs");
                UmengManager.onEvent(getContext(), "Setting", map);
            }
        });

        // 用户反馈
        rlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonModel.skipToFeedback();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Feedback");
                UmengManager.onEvent(getContext(), "Setting", map);
            }
        });

        // 常见问题
        rlQa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, QaActivity.class);
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "FAQ");
                UmengManager.onEvent(getContext(), "Setting", map);
            }
        });

        // 手动清理缓存
        rlDeleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogManager.showProgressDialog(mActivity, false);
                mDiskLruImageCache.clearCache();
                ProgressDialogManager.closeProgressDialog();
                mTvCacheSize.setText("0MB");
            }
        });

        return view;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onResume() {
        super.onResume();
        // 显示学号&考试项目
        User user = UserDAO.findById();
        if (user != null) {

            final UserInfoModel userInfo = GsonManager.getModel(user.user, UserInfoModel.class);
            ExamItemModel examInfo = GsonManager.getModel(user.exam, ExamItemModel.class);

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
        SharedPreferences spf = getContext().getSharedPreferences(
                "global_setting", Context.MODE_PRIVATE);
        int lastNoticeId = spf.getInt("last_notice_id", 0);
        String cache = spf.getString("global_setting", "");
        GlobalSettingsResp resp = GsonManager.getModel(cache, GlobalSettingsResp.class);

        if (resp != null && resp.getResponse_code() == 1
                && resp.getLatest_notify() != lastNoticeId) {
            mIvRedPoint.setVisibility(View.VISIBLE);
        } else {
            mIvRedPoint.setVisibility(View.GONE);
        }

        // 获取缓存大小
        String cacheText = String.valueOf(mDiskLruImageCache.getCacheSize() / (1024 * 1024)) + "MB";
        mTvCacheSize.setText(cacheText);

        //版本号
        mTvVersion.setText(Globals.appVersion);

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

}
