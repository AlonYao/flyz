package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.DrawerAdapter;
import com.appublisher.quizbank.common.login.activity.LoginActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.login.model.netdata.IsUserMergedResp;
import com.appublisher.quizbank.common.offline.activity.OfflineActivity;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassItem;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.fragment.FavoriteFragment;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.fragment.WholePageFragment;
import com.appublisher.quizbank.fragment.WrongQuestionsFragment;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.course.RateCourseResp;
import com.appublisher.quizbank.network.ApiConstants;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.LocationManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements RequestCallback {

    /**
     * Fragment
     **/
    private FragmentManager mFragmentManager;
    private HomePageFragment mHomePageFragment;
    private WholePageFragment mWholePageFragment;
    private CourseFragment mCourseFragment;
    private WrongQuestionsFragment mWrongQuestionsFragment;
    private FavoriteFragment mFavoriteFragment;
    private StudyRecordFragment mStudyRecordFragment;
    private SettingFragment mSettingFragment;
    private static Fragment mCurFragment;
    private static int mCurFragmentPosition;

    private DrawerLayout mDrawerLayout;
    private boolean mDoubleBackToExit;
    private Request mRequest;
    private String mFrom;
    public TextView mTvOpenCourseNumNotice;

    public static ListView mDrawerList;
    public static ImageView mIvDrawerRedPoint;
    public static DrawerAdapter mDrawerAdapter;
    private static final String HOMEPAGE = "Home";
    private static final String WHOLEPAGE = "Whole";
    private static final String COURSE = "Course";
    private static final String WRONGQUESTIONS = "Wrong";
    private static final String FAVORITE = "Favorite";
    private static final String STUDYRECORD = "Study";
    private static final String SETTING = "Setting";
    public ArrayList<OpenCourseUnrateClassItem> mUnRateClasses;

    /**
     * 国考推广
     */
    public static final String INTENT_PROMOTE = "intent_promote";
    private String promote_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mIvDrawerRedPoint = (ImageView) findViewById(R.id.drawer_redpoint);
        mTvOpenCourseNumNotice = (TextView) findViewById(R.id.opencourse_num_notice);

        // 成员变量初始化
        mFragmentManager = getSupportFragmentManager();
        mRequest = new Request(this, this);
        promote_data = getIntent().getStringExtra(INTENT_PROMOTE);

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

        // 页面切换，默认首页
        changeFragment(mCurFragmentPosition);

        // 记录用户评价行为
        if (GradeDAO.isShowGradeAlert(Globals.appVersion)) {
            // 提前获取评价课程数据
            mRequest.getRateCourse(ParamBuilder.getRateCourse("getCourse", ""));
        }

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查登录状态
        checkLoginStatus();
        // Umeng
        MobclickAgent.onResume(this);
        // TalkingData
        TCAgent.onResume(this);
        // 检测账号是否被合并
        mRequest.isUserMerged(LoginModel.getUserId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭定位
        LocationManager.stopBaiduLocation();

        // Umeng
        MobclickAgent.onPause(this);

        // TalkingData
        TCAgent.onPause(this);

        ProgressDialogManager.closeProgressDialog();

        // 关闭评价窗口
        AlertManager.dismissGradeAlert();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        // 测试服Flag
        if ("dev".equals(ApiConstants.base)) {
            MenuItemCompat.setShowAsAction(menu.add("测试服"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        // 恢复课程评论小红点的显示状态
        mTvOpenCourseNumNotice.setVisibility(View.GONE);

        if (mCurFragment instanceof HomePageFragment
                && GradeDAO.isOpenGradeSys(Globals.appVersion)) {
            MenuItemCompat.setShowAsAction(menu.add("评价").setIcon(R.drawable.homepage_grade),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        } else if (mCurFragment instanceof CourseFragment) {
            MenuItemCompat.setShowAsAction(menu.add("下载"),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("评分"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("评价".equals(item.getTitle())) {
            ProgressDialogManager.showProgressDialog(this, true);
            mRequest.getRateCourse(ParamBuilder.getRateCourse("getCourse", ""));
            mFrom = "menu";
        } else if ("下载".equals(item.getTitle())) {
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);
        } else if ("评分".equals(item.getTitle())) {
            OpenCourseModel.skipToMyGrade(this, mUnRateClasses, "false");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
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
     * 检查登录状态
     */
    private void checkLoginStatus() {
        if (!LoginModel.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("from", "splash");
            startActivity(intent);
        } else if (!LoginModel.hasExamInfo()) {
            Intent intent = new Intent(this, ExamChangeActivity.class);
            intent.putExtra("from", "splash");
            startActivity(intent);
            // Umeng
            UmengManager.sendCountEvent(this, "Home", "Entry", "Launch");
        }
    }

    /**
     * 侧边栏按钮点击事件
     */
    private AdapterView.OnItemClickListener drawerListOnClick =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 点击版本号，直接返回
                    if (position == DrawerAdapter.mItemNames.length) return;

                    changeFragment(position);

                    // 侧边栏顶部红点消失
                    if (position == 5) {
                        mIvDrawerRedPoint.setVisibility(View.GONE);
                    }

                    // Umeng统计
                    if (position == 2)
                        UmengManager.sendCountEvent(MainActivity.this, "CourseCenter", "Entry", "Drawer");
                }
            };

    /**
     * 切换Fragment
     *
     * @param position fragment在侧边栏上的位置
     */
    public void changeFragment(int position) {
        mCurFragmentPosition = position;

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
                    transaction.add(R.id.drawer_frame, mHomePageFragment, HOMEPAGE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mHomePageFragment);
                }

                getSupportActionBar().setTitle(" ");

                mCurFragment = mHomePageFragment;

                break;

            case 1:
                // 整卷练习
                if (mWholePageFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mWholePageFragment = new WholePageFragment();
                    transaction.add(R.id.drawer_frame, mWholePageFragment, WHOLEPAGE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mWholePageFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_wholepage);

                mCurFragment = mWholePageFragment;

                break;

            case 2:
                // 课程中心
                if (mCourseFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mCourseFragment = new CourseFragment();
                    transaction.add(R.id.drawer_frame, mCourseFragment, COURSE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mCourseFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_course);

                mCurFragment = mCourseFragment;

                break;

            case 3:
                // 错题本
                if (mWrongQuestionsFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mWrongQuestionsFragment = new WrongQuestionsFragment();
                    transaction.add(R.id.drawer_frame, mWrongQuestionsFragment, WRONGQUESTIONS);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mWrongQuestionsFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_wrong);

                mCurFragment = mWrongQuestionsFragment;

                break;

            case 4:
                // 收藏夹
                if (mFavoriteFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mFavoriteFragment = new FavoriteFragment();
                    transaction.add(R.id.drawer_frame, mFavoriteFragment, FAVORITE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mFavoriteFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_store);

                mCurFragment = mFavoriteFragment;

                break;

            case 5:
                // 学习记录
                if (mStudyRecordFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mStudyRecordFragment = new StudyRecordFragment();
                    transaction.add(R.id.drawer_frame, mStudyRecordFragment, STUDYRECORD);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mStudyRecordFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_record);

                mCurFragment = mStudyRecordFragment;

                break;

            case 6:
                // 设置
                if (mSettingFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.drawer_frame, mSettingFragment, SETTING);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mSettingFragment);
                }

                getSupportActionBar().setTitle(R.string.drawer_setting);

                mCurFragment = mSettingFragment;

                break;

            default:
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);
        transaction.commit();

        // 更新Menu
        Utils.updateMenu(this);
    }

    /**
     * 将所有的Fragment都置为隐藏状态
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        // 首页
        mHomePageFragment = (HomePageFragment) mFragmentManager.findFragmentByTag(HOMEPAGE);
        if (mHomePageFragment != null) transaction.hide(mHomePageFragment);

        // 真题演练
        mWholePageFragment = (WholePageFragment) mFragmentManager.findFragmentByTag(WHOLEPAGE);
        if (mWholePageFragment != null) transaction.hide(mWholePageFragment);

        // 课程中心
        mCourseFragment = (CourseFragment) mFragmentManager.findFragmentByTag(COURSE);
        if (mCourseFragment != null) transaction.hide(mCourseFragment);

        // 错题本
        mWrongQuestionsFragment =
                (WrongQuestionsFragment) mFragmentManager.findFragmentByTag(WRONGQUESTIONS);
        if (mWrongQuestionsFragment != null) transaction.hide(mWrongQuestionsFragment);

        // 收藏夹
        mFavoriteFragment = (FavoriteFragment) mFragmentManager.findFragmentByTag(FAVORITE);
        if (mFavoriteFragment != null) transaction.hide(mFavoriteFragment);

        // 学习记录
        mStudyRecordFragment =
                (StudyRecordFragment) mFragmentManager.findFragmentByTag(STUDYRECORD);
        if (mStudyRecordFragment != null) {
            transaction.hide(mStudyRecordFragment);
            transaction.remove(mStudyRecordFragment);
            mStudyRecordFragment = null;
        }

        // 设置
        mSettingFragment = (SettingFragment) mFragmentManager.findFragmentByTag(SETTING);
        if (mSettingFragment != null) transaction.hide(mSettingFragment);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        switch (apiName) {
            case "get_rate_course":
                Globals.rateCourseResp = GsonManager.getModel(response, RateCourseResp.class);
                if ("menu".equals(mFrom)) AlertManager.showGradeAlert(this, "Click");
                ProgressDialogManager.closeProgressDialog();
                break;

            case "is_user_merged":
                IsUserMergedResp resp = GsonManager.getModel(response, IsUserMergedResp.class);
                if (resp == null || resp.getResponse_code() != 1 || !resp.is_merged()) break;
                LoginModel.userMergedAlert(this);
                break;

            default:
                ProgressDialogManager.closeProgressDialog();
                break;
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
