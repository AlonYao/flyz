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
import com.appublisher.lib_basic.LocationManager;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.coursecenter.CourseFragment;
import com.appublisher.lib_course.offline.activity.OfflineActivity;
import com.appublisher.lib_course.opencourse.model.OpenCourseModel;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseUnrateClassItem;
import com.appublisher.lib_course.promote.PromoteModel;
import com.appublisher.lib_course.promote.PromoteResp;
import com.appublisher.lib_login.activity.ExamChangeActivity;
import com.appublisher.lib_login.activity.LoginActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.IsUserMergedResp;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.DrawerAdapter;
import com.appublisher.quizbank.common.vip.activity.VipIndexActivity;
import com.appublisher.quizbank.common.vip.fragment.VipIndexFragment;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.fragment.FavoriteFragment;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.fragment.SettingFragment;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
import com.appublisher.quizbank.fragment.WholePageFragment;
import com.appublisher.quizbank.fragment.WrongQuestionsFragment;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.MeasureModel;
import com.appublisher.quizbank.model.business.PromoteQuizBankModel;
import com.appublisher.quizbank.model.netdata.course.RateCourseResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QApiConstants;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity implements RequestCallback {

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
    private VipIndexFragment mVipIndexFragment;
    private static Fragment mCurFragment;
    private static int mCurFragmentPosition;
    private DrawerLayout mDrawerLayout;
    private boolean mDoubleBackToExit;
    private QRequest mQRequest;
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
    private static final String VIP = "Vip";
    public ArrayList<OpenCourseUnrateClassItem> mUnRateClasses;

    /**
     * 国考推广
     */
    public static final String INTENT_PROMOTE = "intent_promote";
    private String mPromoteData;
    private PromoteQuizBankModel mPromoteQuizBankModel;

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
        mQRequest = new QRequest(this, this);
        mPromoteData = getIntent().getStringExtra(INTENT_PROMOTE);
        mPromoteQuizBankModel = new PromoteQuizBankModel(this);

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
            mQRequest.getRateCourse(ParamBuilder.getRateCourse("getCourse", ""));
        }

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查登录&考试项目状态
        if (checkLoginStatus()) {
            // 国考推广Alert
            if (mPromoteData == null) {
                mPromoteQuizBankModel.getPromoteData(new PromoteModel.PromoteDataListener() {
                    @Override
                    public void onComplete(boolean success, PromoteResp resp) {
                        if (success) {
                            mPromoteQuizBankModel.showPromoteAlert(GsonManager.modelToString(resp));
                        }
                    }
                });
            } else {
                mPromoteQuizBankModel.showPromoteAlert(mPromoteData);
            }
            // 检测账号是否被合并
            mQRequest.isUserMerged(LoginModel.getUserId());
            // 做题缓存提交
            new MeasureModel(this).checkCache();
        }
    }


    /**
     * 检查登录状态
     */
    private boolean checkLoginStatus() {
        if (!LoginModel.isLogin()) {
            // 未登录
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("from", "splash");
            startActivity(intent);
            return false;

        } else if (!LoginModel.hasExamInfo()) {
            // 没有考试项目
            Intent intent = new Intent(this, ExamChangeActivity.class);
            intent.putExtra("from", "splash");
            startActivity(intent);
            // Umeng
//            UmengManager.sendCountEvent(this, "Home", "Entry", "Launch");
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭定位
        LocationManager.stop();

        ProgressDialogManager.closeProgressDialog();

        // 关闭评价窗口
        AlertManager.dismissGradeAlert();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        // 测试服Flag
        if (QApiConstants.baseUrl.contains("dev")) {
            MenuItemCompat.setShowAsAction(menu.add("测试服"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        // 恢复课程评论小红点的显示状态
        mTvOpenCourseNumNotice.setVisibility(View.GONE);

        if (mCurFragment instanceof HomePageFragment
                && GradeDAO.isOpenGradeSys(Globals.appVersion)) {
            MenuItemCompat.setShowAsAction(menu.add("小班").setIcon(R.drawable.vip_entry),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        } else if (mCurFragment instanceof CourseFragment) {
            MenuItemCompat.setShowAsAction(menu.add("下载"),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("评分"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else if (mCurFragment instanceof VipIndexFragment) {
            MenuItemCompat.setShowAsAction(menu.add("首页").setIcon(R.drawable.examindex),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("小班".equals(item.getTitle())) {
            final Intent intent = new Intent(this, VipIndexActivity.class);
            startActivity(intent);

            //um
            Map<String, String> umMap = new HashMap<>();
            umMap.put("Action", "Entry");
            UmengManager.onEvent(this, "VipHome", umMap);

        } else if ("下载".equals(item.getTitle())) {
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);
        } else if ("评分".equals(item.getTitle())) {
            OpenCourseModel.skipToMyGrade(this, mUnRateClasses, "false");
        } else if ("首页".equals(item.getTitle())) {
            final Intent intent = new Intent(this, CommonFragmentActivity.class);
            intent.putExtra("from", "homepage");
            startActivity(intent);
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
                    if (position == 2) {
                        final Map<String, String> um_map = new HashMap<String, String>();
                        um_map.put("Entry", "Drawer");
                        UmengManager.onEvent(MainActivity.this, "CourseCenter", um_map);
                        ;
                    }
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
                if (Globals.sharedPreferences.getBoolean("vip" + LoginModel.getUserId(), false)) {
                    if (mVipIndexFragment == null) {
                        mVipIndexFragment = new VipIndexFragment();
                        transaction.add(R.id.drawer_frame, mVipIndexFragment, VIP);
                    } else {
                        transaction.show(mVipIndexFragment);
                    }

                    getSupportActionBar().setTitle(" ");

                    mCurFragment = mVipIndexFragment;
                } else {
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
                }
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

        //Vip
        mVipIndexFragment = (VipIndexFragment) mFragmentManager.findFragmentByTag(VIP);
        if (mVipIndexFragment != null) transaction.hide(mVipIndexFragment);
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
