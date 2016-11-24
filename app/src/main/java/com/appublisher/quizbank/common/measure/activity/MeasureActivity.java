package com.appublisher.quizbank.common.measure.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.adapter.MeasureAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureSheetFragment;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

import java.util.List;

/**
 * 做题模块：主页面
 */
public class MeasureActivity extends BaseActivity implements MeasureConstants {

    private static final String MENU_SCRATCH = "草稿纸";
    private static final String MENU_ANSWERSHEET = "答题卡";
    private static final String MENU_PAUSE = "暂停";

    public ViewPager mViewPager;
    public MeasureModel mModel;
    public MeasureAdapter mAdapter;
    public TabLayout mTabLayout;

    private boolean mIsFromClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        setToolBar(this);
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

        MenuItemCompat.setShowAsAction(menu.add(MENU_PAUSE).setIcon(
                R.drawable.measure_icon_pause), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回键
            mModel.checkRecord();
        } else if (item.getTitle().equals(MENU_PAUSE)) {
            // 暂停
        } else if (item.getTitle().equals(MENU_ANSWERSHEET)) {
            // 答题卡
            skipToSheet();

        } else if (item.getTitle().equals(MENU_SCRATCH)) {
            Intent intent = new Intent(this, ScratchPaperActivity.class);
            startActivity(intent);
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
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.measure_viewpager);
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
    }

    private void scrollTabLayout(int position) {
        if (mTabLayout == null) return;
        int scrollToPositon = mModel.getTabPositionScrollTo(position);
        int curTabPotision = mTabLayout.getSelectedTabPosition();
        if (scrollToPositon != curTabPotision) {
            TabLayout.Tab tab = mTabLayout.getTabAt(scrollToPositon);
            if (tab == null) return;
            tab.select();
        }
    }

    public void showTabLayout(List<MeasureTabBean> tabs) {
        if (tabs == null) return;
        ViewStub vs = (ViewStub) findViewById(R.id.measure_tablayout_viewstub);
        if (vs == null) return;
        vs.inflate();

        mTabLayout = (TabLayout) findViewById(R.id.measure_tablayout);
        if (mTabLayout == null) return;
        for (MeasureTabBean tab : tabs) {
            if (tab == null) continue;
            String name = tab.getName();
            if (name.length() > 2) {
                name = name.substring(0, 2);
            }
            mTabLayout.addTab(mTabLayout.newTab().setText(name));
        }

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mIsFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    mIsFromClick = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mIsFromClick) {
                    pageSkipFromTabClick(tab.getPosition());
                    mIsFromClick = false;
                }
            }
        });

        // 设置click事件
        setTabLayoutOnItemClick();
    }

    private void setTabLayoutOnItemClick() {
        if (mTabLayout == null) return;

        ViewGroup root = (ViewGroup) mTabLayout.getChildAt(0);
        if (root == null) return;

        int size = mTabLayout.getTabCount();
        for (int i = 0; i < size; i++) {
            View tabView = root.getChildAt(i);
            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsFromClick = true;
                }
            });
        }
    }

    private void pageSkipFromTabClick(int tabPosition) {
        mViewPager.setCurrentItem(mModel.getPositionByTab(tabPosition));
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
        MeasureSheetFragment sheetFragment = new MeasureSheetFragment();
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

                                // Umeng
//                                UmengManager.onEvent(activity, "0");

                                finish();
                            }
                        }).show();
    }
}
