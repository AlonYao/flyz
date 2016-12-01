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

import com.appublisher.lib_basic.ToastManager;
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

    public ViewPager mViewPager;
    public MeasureModel mModel;
    public MeasureAdapter mAdapter;
    public int mMins;
    public int mSec;

    private Handler mHandler;
    private Timer mTimer;

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
                        String mins = String.valueOf(activity.mMins);
                        String sec = String.valueOf(activity.mSec);
                        if (mins.length() == 1) mins = "0" + mins;
                        if (sec.length() == 1) sec = "0" + sec;
                        String time = mins + ":" + sec;
                        activity.setTitle(time);
                        break;
                }
            }
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
        mModel.mCurTimestamp = System.currentTimeMillis();

        showLoading();
        mModel.getData();

        mHandler = new MsgHandler(this);
        mMins = 0;
        mSec = 0;

        setModel(mModel);
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
                mModel.saveSubmitDuration(position);
                scrollTabLayout(position);
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

        int titleId = R.string.alert_savetest_title;
        int msgId;
        int pId;
        int nId;

        if (MOCK.equals(mModel.mPaperType)) {
            msgId = R.string.alert_mock_back;
            pId = R.string.alert_mock_p;
            nId = R.string.alert_mock_n;
        } else {
            msgId = R.string.alert_savetest_msg;
            pId = R.string.alert_p;
            nId = R.string.alert_n;
        }

        new AlertDialog.Builder(this)
                .setTitle(titleId)
                .setMessage(msgId)
                .setNegativeButton(nId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(pId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 更新当前页面的时长
                                mModel.saveSubmitDuration(mModel.mCurPagePosition);

                                // 保存至本地
//                                PaperDAO.save(activity.mPaperId, activity.mCurPosition);

                                // 提交数据
                                if(MOCK.equals(mModel.mPaperType)){
                                    mModel.submit(true);
                                }else{
                                    mModel.submit(false);
                                }

                                // 保存练习
                                ToastManager.showToast(MeasureActivity.this, "保存成功");
                                dialog.dismiss();

                                finish();
                            }
                        }).show();
    }

    /**
     * 倒计时启动
     */
    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
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
}
