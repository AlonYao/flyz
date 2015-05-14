package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.DrawerAdapter;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.fragment.FavoriteFragment;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.fragment.WholePageFragment;
import com.appublisher.quizbank.fragment.WrongQuestionsFragment;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.LocationManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements RequestCallback{

    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    private HomePageFragment mHomePageFragment;
    private WholePageFragment mWholePageFragment;
    private WrongQuestionsFragment mWrongQuestionsFragment;
    private FavoriteFragment mFavoriteFragment;
    private StudyRecordFragment mStudyRecordFragment;
    private SettingFragment mSettingFragment;
    private boolean mDoubleBackToExit;

    public static ListView mDrawerList;
    public static ImageView mIvDrawerRedPoint;
    public static DrawerAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mIvDrawerRedPoint = (ImageView) findViewById(R.id.drawer_redpoint);

        // 成员变量初始化
        mFragmentManager = getSupportFragmentManager();

        /** 侧边栏设置 */
        // 侧边栏按钮列表
        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(drawerListOnClick);

        // 侧边栏样式
        CommonModel.setToolBar(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerToggle.syncState();
        mDrawerLayout.setDrawerListener(drawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // 默认选中首页
        changeFragment(0);

        // 获取全局配置
        new Request(this, this).getGlobalSettings();

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭定位
        LocationManager.stopBaiduLocation();

        // Umeng
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }

        // 两次Back退出
        if (mDoubleBackToExit) {
            QuizBankApp.getInstance().exit();
            super.onBackPressed();
            return;
        }

        mDoubleBackToExit = true;
        ToastManager.showToast(this, "再按一次退出");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDoubleBackToExit = false;
            }
        }, 2000);
    }

    /**
     * 侧边栏按钮点击事件
     */
    private AdapterView.OnItemClickListener drawerListOnClick =
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changeFragment(position);

            // 侧边栏顶部红点消失
            if (position == 5) {
                mIvDrawerRedPoint.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 切换Fragment
     * @param position fragment在侧边栏上的位置
     */
    private void changeFragment(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        switch (position) {
            case 0:
                // 首页
                if (mHomePageFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mHomePageFragment = new HomePageFragment();
                    transaction.add(R.id.drawer_frame, mHomePageFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mHomePageFragment);
                }

                getSupportActionBar().setTitle(" ");

                break;

            case 1:
                // 整卷练习
                if (mWholePageFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mWholePageFragment = new WholePageFragment();
                    transaction.add(R.id.drawer_frame, mWholePageFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mWholePageFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_wholepage);

                break;

            case 2:
                // 错题本
                if (mWrongQuestionsFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mWrongQuestionsFragment = new WrongQuestionsFragment();
                    transaction.add(R.id.drawer_frame, mWrongQuestionsFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mWrongQuestionsFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_wrong);

                break;

            case 3:
                // 收藏夹
                if (mFavoriteFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mFavoriteFragment = new FavoriteFragment();
                    transaction.add(R.id.drawer_frame, mFavoriteFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mFavoriteFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_store);

                break;

            case 4:
                // 学习记录
                if (mStudyRecordFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mStudyRecordFragment = new StudyRecordFragment();
                    transaction.add(R.id.drawer_frame, mStudyRecordFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mStudyRecordFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_record);

                break;

            case 5:
                // 设置
                if (mSettingFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.drawer_frame, mSettingFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mSettingFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_setting);

                break;

            default:
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mHomePageFragment != null) {
            transaction.hide(mHomePageFragment);
            transaction.remove(mHomePageFragment);
            mHomePageFragment = null;
        }

        if (mWholePageFragment != null) {
            transaction.hide(mWholePageFragment);
        }

        if (mWrongQuestionsFragment != null) {
            transaction.hide(mWrongQuestionsFragment);
        }

        if (mFavoriteFragment != null) {
            transaction.hide(mFavoriteFragment);
        }

        if (mStudyRecordFragment != null) {
            transaction.hide(mStudyRecordFragment);
            transaction.remove(mStudyRecordFragment);
            mStudyRecordFragment = null;
        }

        if (mSettingFragment != null) {
            transaction.hide(mSettingFragment);
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("global_settings".equals(apiName) && response != null)
            GlobalSettingDAO.save(response.toString());
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        // Nothing
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        // Nothing
    }
}
