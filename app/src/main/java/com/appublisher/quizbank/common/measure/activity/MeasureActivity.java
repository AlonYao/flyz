package com.appublisher.quizbank.common.measure.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.adapter.MeasureAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureSheetFragment;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends MeasureBaseActivity implements MeasureConstants {

    private static final String MENU_SCRATCH = "草稿纸";
    private static final String MENU_ANSWERSHEET = "答题卡";
    private static final String MENU_PAUSE = "暂停";
    private static final int TIME_ON = 0;
    private static final int TIME_MOCK_ON = 1;
    private static final int TIME_MOCK_OUT = 2;

    public ViewPager mViewPager;
    public MeasureModel mModel;
    public MeasureAdapter mAdapter;
    public int mMins;
    public int mSec;
    public Timer mTimer;

    private Handler mHandler;

    // Umeng
    private long mUMTimeStamp;

    public static class MsgHandler extends Handler {
        private WeakReference<MeasureActivity> mActivity;

        MsgHandler(MeasureActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @SuppressLint("CommitPrefEdits")
        @Override
        public void handleMessage(Message msg) {
            final MeasureActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case TIME_ON:
                        // 显示时间
                        activity.setTitle(getTimeText(activity.mMins, activity.mSec));
                        break;

                    case TIME_MOCK_ON:
                        // 模考
                        activity.mModel.mMockDuration--;
                        activity.setTitle(getTimeText(activity.mMins, activity.mSec));
                        if (activity.mMins == 15 && activity.mSec == 0) {
                            Toast.makeText(
                                    activity,
                                    "距离考试结束还有15分钟",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case TIME_MOCK_OUT:
                        activity.mModel.mMockDuration = 0;
                        activity.showLoading();
                        activity.mModel.getServerTimeStamp(
                                new MeasureModel.ServerTimeListener() {
                                    @Override
                                    public void onTimeOut() {
                                        activity.showMockTimeOutAlert();
                                        activity.mModel.submitPaperDone();
                                    }

                                    @Override
                                    public void canSubmit() {
                                        // Empty
                                    }
                                });
                        break;
                }
            }
        }

        private String getTimeText(int min, int sec) {
            String minString = String.valueOf(min);
            String secString = String.valueOf(sec);
            if (minString.length() == 1) minString = "0" + minString;
            if (secString.length() == 1) secString = "0" + secString;
            return minString + ":" + secString;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        setToolBar(this);
        setTitle("");
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        if (VIP.equals(mModel.mPaperType)) {
            int dur = (int) ((System.currentTimeMillis() - mUMTimeStamp) / 1000);
            HashMap<String, String> map = new HashMap<>();
            UmengManager.onEventValue(this, "Zhineng", map, dur);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        MenuItemCompat.setShowAsAction(menu.add(MENU_SCRATCH).setIcon(
                R.drawable.measure_icon_scratch_paper), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setShowAsAction(menu.add(MENU_ANSWERSHEET).setIcon(
                R.drawable.measure_icon_answersheet), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if (!MOCK.equals(mModel.mPaperType)) {
            MenuItemCompat.setShowAsAction(menu.add(MENU_PAUSE).setIcon(
                    R.drawable.measure_icon_pause), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回键
            mModel.checkRecord();
        } else if (item.getTitle().equals(MENU_PAUSE)) {
            // 暂停
            showPauseAlert();
            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Pause");
            UmengManager.onEvent(this, "Quiz", map);

        } else if (item.getTitle().equals(MENU_ANSWERSHEET)) {
            // 答题卡
            skipToSheet();
            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "AnswerSheet");
            UmengManager.onEvent(this, "Quiz", map);

        } else if (item.getTitle().equals(MENU_SCRATCH)) {
            // 草稿纸
            Intent intent = new Intent(this, ScratchPaperActivity.class);
            startActivity(intent);
            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Draft");
            UmengManager.onEvent(this, "Quiz", map);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        mModel.checkRecord();
    }

    private void initData() {
        mModel = new MeasureModel(this);
        mModel.mPaperType = getIntent().getStringExtra(INTENT_PAPER_TYPE);
        mModel.mHierarchyId = getIntent().getIntExtra(INTENT_HIERARCHY_ID, 0);
        mModel.mPaperId = getIntent().getIntExtra(INTENT_PAPER_ID, 0);
        mModel.mRedo = getIntent().getBooleanExtra(INTENT_REDO, false);
        mModel.mMockTime = getIntent().getStringExtra(INTENT_MOCK_TIME);
        mModel.mCurTimestamp = System.currentTimeMillis();
        mModel.mVipXCData = getIntent().getStringExtra(INTENT_VIP_XC_DATA);

        mHandler = new MsgHandler(this);
        mMins = 0;
        mSec = 0;
        setModel(mModel);

        showLoading();
        mModel.getData();

        // Umeng
        mUMTimeStamp = System.currentTimeMillis();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
        setViewPager(mViewPager);
    }

    public void showViewPager(List<MeasureQuestionBean> questions) {
        mAdapter = new MeasureAdapter(getSupportFragmentManager(), questions);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                mModel.saveSubmitDuration();
                scrollTabLayout(position);
                mModel.mCurPagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }
        });

        startTimer();
    }

    /**
     * 跳转至答题卡
     */
    public void skipToSheet() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("MeasureSheetFragment");
        if (fragment != null) {
            transaction.remove(fragment);
        }
        MeasureSheetFragment sheetFragment =
                MeasureSheetFragment.newInstance(mModel.mCurPagePosition);
        sheetFragment.show(transaction, "MeasureSheetFragment");
    }

    /**
     * 保存测验Alert
     */
    public void showSaveTestAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_savetest_title)
                .setMessage(R.string.alert_savetest_msg)
                .setNegativeButton(R.string.alert_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.alert_p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 更新当前页面的时长
                                mModel.saveSubmitDuration();
                                // 提交数据
                                mModel.submit(false);
                                // 清除缓存
                                MeasureModel.clearUserAnswerCache(MeasureActivity.this);
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
    }

    /**
     * 保存测验Alert
     */
    public void showMockSaveTestAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_savetest_title)
                .setMessage(R.string.alert_mock_back)
                .setNegativeButton(R.string.alert_mock_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.alert_mock_p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 更新当前页面的时长
                                mModel.saveSubmitDuration();
                                // 提交数据
                                mModel.submit(true);
                                // 清除缓存
                                MeasureModel.clearUserAnswerCache(MeasureActivity.this);
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
    }

    /**
     * 倒计时启动
     */
    public void startTimer() {
        stopTimer();
        mTimer = new Timer();
        if (MOCK.equals(mModel.mPaperType)) {
            // 模考特殊处理
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    mSec--;
                    if (mSec < 0) {
                        mMins--;
                        mSec = 59;
                        mHandler.sendEmptyMessage(TIME_MOCK_ON);
                        if (mMins < 0) {
                            stopTimer();
                            mHandler.sendEmptyMessage(TIME_MOCK_OUT);
                        }
                    } else {
                        mHandler.sendEmptyMessage(TIME_MOCK_ON);
                    }
                }
            }, 0, 1000);
        } else {
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    mSec++;
                    if (mSec > 59) {
                        mMins++;
                        mSec = 0;
                    }
                    mHandler.sendEmptyMessage(TIME_ON);
                }
            }, 0, 1000);
        }
    }

    /**
     * 倒计时停止
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 暂停Alert
     */
    private void showPauseAlert() {
        stopTimer();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.alert_item_pause);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView textView = (TextView) window.findViewById(R.id.alert_pause_tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 模考时间到Alert
     */
    public void showMockTimeOutAlert() {
        new AlertDialog.Builder(this)
                .setMessage("时间到了要交卷啦！")
                .setTitle("提示")
                .setCancelable(false)
                .setPositiveButton("好的",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public void showSubmitErrorToast() {
        Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
    }

    public void showMockTime30Toast() {
        Toast.makeText(this, "开考30分钟后才可以交卷", Toast.LENGTH_SHORT).show();
    }

    /**
     * 答题卡未完成提示
     */
    public void showMeasureSheetUndoneAlert() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_answersheet_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_answersheet_p,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showLoading();
                                mModel.submitPaperDone();
                            }
                        })
                .setNegativeButton(R.string.alert_answersheet_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    public void setMinAndSec(int duration) {
        if (duration == 0) return;
        mSec = duration % 60;
        mMins = duration / 60;
    }

}