package com.appublisher.quizbank.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.LocationManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.coursecenter.CourseFragment;
import com.appublisher.lib_course.offline.activity.OfflineActivity;
import com.appublisher.lib_course.opencourse.fragment.OpenCourseFragment;
import com.appublisher.lib_course.opencourse.model.OpenCourseModel;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseUnrateClassItem;
import com.appublisher.lib_course.promote.PromoteModel;
import com.appublisher.lib_course.promote.PromoteResp;
import com.appublisher.lib_login.activity.BindingMobileActivity;
import com.appublisher.lib_login.activity.ExamChangeActivity;
import com.appublisher.lib_login.activity.LoginActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.netdata.IsUserMergedResp;
import com.appublisher.lib_login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.fragment.InterviewIndexFragment;
import com.appublisher.quizbank.common.vip.fragment.VipIndexFragment;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.fragment.StudyIndexFragment;
import com.appublisher.quizbank.fragment.StudyRecordFragment;
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

public class MainActivity extends BaseActivity implements RequestCallback {
    /**
     * Fragment
     **/
    private FragmentManager mFragmentManager;
    private CourseFragment mCourseFragment;
    private StudyRecordFragment mStudyRecordFragment;
    private VipIndexFragment mVipIndexFragment;
    private StudyIndexFragment mStudyIndexFragment;
    private InterviewIndexFragment mInterviewIndexFragment;
    private OpenCourseFragment mOpenCourseFragment;
    private static Fragment mCurFragment;
    private boolean mDoubleBackToExit;
    private QRequest mQRequest;
    private static final String OPENCOURSE = "Opencourse";
    private static final String COURSE = "Course";
    private static final String RECORD = "Record";
    private static final String VIP = "Vip";
    private static final String STUDY = "Study";
    private static final String INTERVIEW = "Interview";
    public ArrayList<OpenCourseUnrateClassItem> mUnRateClasses;

    private TextView rateCourseCountTv;

    private RadioButton studyRadioButton;
    public RadioButton courseRadioButton;
    private RadioButton opencourseRadioButton;
    private RadioButton recordRadioButton;
    private RadioButton vipRadioButton;

    private String indexString = "study";//study or interview

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
        setToolBar(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // 成员变量初始化
        mFragmentManager = getSupportFragmentManager();
        mQRequest = new QRequest(this, this);
        mPromoteData = getIntent().getStringExtra(INTENT_PROMOTE);
        mPromoteQuizBankModel = new PromoteQuizBankModel(this);

        rateCourseCountTv = (TextView) findViewById(R.id.opencourse_num_notice);

        studyRadioButton = (RadioButton) findViewById(R.id.study);
        courseRadioButton = (RadioButton) findViewById(R.id.course);
        opencourseRadioButton = (RadioButton) findViewById(R.id.opencourse);
        recordRadioButton = (RadioButton) findViewById(R.id.record);
        vipRadioButton = (RadioButton) findViewById(R.id.vip);

        // 记录用户评价行为
        if (GradeDAO.isShowGradeAlert(Globals.appVersion)) {
            // 提前获取评价课程数据
            mQRequest.getRateCourse(ParamBuilder.getRateCourse("getCourse", ""));
        }

        setValue();

        // Add Activity
        QuizBankApp.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //隐藏评分个数
        rateCourseCountTv.setVisibility(View.GONE);
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
            new LoginModel(this).commonCheck(new LoginModel.ObtainUserInfoListener() {
                @Override
                public void isSuccess(boolean isSuccess) {
                    if (isSuccess) {
                        final UserInfoModel userInfoModel = LoginModel.getUserInfoM();
                        if (userInfoModel == null) return;
                        if (userInfoModel.getMobile_num() == null || "".equals(userInfoModel.getMobile_num())) {
                            final Intent intent = new Intent(MainActivity.this, BindingMobileActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
            // 做题缓存提交
//            new LegacyMeasureModel(this).checkCache();
        }

        if (!Utils.isConnectingToInternet(QuizBankApp.getInstance().getApplicationContext())) {
            ToastManager.showToast(this, "当前无可用网络");
            courseRadioButton.setChecked(true);
        }
    }

    public void setValue() {
        studyRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (indexString.equals("study")) {
                        changeFragment(0);
                    } else {
                        changeFragment(5);
                    }
                    studyRadioButton.setTextColor(getResources().getColor(R.color.apptheme));
                } else {
                    studyRadioButton.setTextColor(getResources().getColor(R.color.common_text));
                }
            }
        });

        courseRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeFragment(1);
                    courseRadioButton.setTextColor(getResources().getColor(R.color.apptheme));
                } else {
                    courseRadioButton.setTextColor(getResources().getColor(R.color.common_text));
                }
            }
        });

        opencourseRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeFragment(2);
                    opencourseRadioButton.setTextColor(getResources().getColor(R.color.apptheme));
                } else {
                    opencourseRadioButton.setTextColor(getResources().getColor(R.color.common_text));
                }
            }
        });

        recordRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeFragment(3);
                    recordRadioButton.setTextColor(getResources().getColor(R.color.apptheme));
                } else {
                    recordRadioButton.setTextColor(getResources().getColor(R.color.common_text));
                }
            }
        });

        vipRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeFragment(4);
                    vipRadioButton.setTextColor(getResources().getColor(R.color.apptheme));
                } else {
                    vipRadioButton.setTextColor(getResources().getColor(R.color.common_text));
                }
            }
        });

        // 页面切换，默认首页
        studyRadioButton.setChecked(true);

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

        if (mCurFragment instanceof StudyIndexFragment) {
            MenuItemCompat.setShowAsAction(menu.add("面试").setIcon(R.drawable.actionbar_interview),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else if (mCurFragment instanceof CourseFragment) {
            MenuItemCompat.setShowAsAction(menu.add("下载").setIcon(R.drawable.actionbar_download),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setShowAsAction(menu.add("评分").setIcon(R.drawable.actionbar_rate), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else if (mCurFragment instanceof StudyRecordFragment) {
            MenuItemCompat.setShowAsAction(menu.add("设置").setIcon(R.drawable.actionbar_setting),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else if (mCurFragment instanceof InterviewIndexFragment) {
            MenuItemCompat.setShowAsAction(menu.add("笔试").setIcon(R.drawable.actionbar_study),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        } else if (mCurFragment instanceof OpenCourseFragment) {
            MenuItemCompat.setShowAsAction(
                    menu.add("公开课评分").setIcon(R.drawable.actionbar_rate),
                    MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("下载".equals(item.getTitle())) {
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Download");
            UmengManager.onEvent(this, "CourseCenter", map);

        } else if ("评分".equals(item.getTitle())) {
            OpenCourseModel.skipToMyGrade(this, CourseFragment.mUnRateClasses, "false");

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Score");
            UmengManager.onEvent(this, "CourseCenter", map);

        } else if ("面试".equals(item.getTitle())) {
            changeFragment(5);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "InterviewPage");
            UmengManager.onEvent(this, "Home", map);

        } else if ("设置".equals(item.getTitle())) {
            final Intent intent = new Intent(this, CommonFragmentActivity.class);
            intent.putExtra("from", "setting");
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Setting");
            UmengManager.onEvent(this, "Record", map);

        } else if ("笔试".equals(item.getTitle())) {
            changeFragment(0);

        } else if ("公开课评分".equals(item.getTitle())) {
            OpenCourseModel.skipToMyGrade(this, "true");

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Score", "1");
            UmengManager.onEvent(this, "OpenCourse", map);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {

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
     * 切换Fragment
     *
     * @param position fragment在侧边栏上的位置
     */
    public void changeFragment(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        switch (position) {
            case 0:
                if (mStudyIndexFragment == null) {
                    mStudyIndexFragment = new StudyIndexFragment();
                    transaction.add(R.id.container_view, mStudyIndexFragment, STUDY);
                    if (mCurFragment instanceof InterviewIndexFragment) {
                        transaction.setCustomAnimations(R.anim.translate_right_out, R.anim.translate_left_out);
                    }
                } else {
                    if (mCurFragment instanceof InterviewIndexFragment) {
                        transaction.setCustomAnimations(R.anim.translate_right_out, R.anim.translate_left_out);
                    }
                    transaction.show(mStudyIndexFragment);
                }

                setTitle(R.string.study_index);

                mCurFragment = mStudyIndexFragment;
                indexString = "study";

                Drawable drawable = getResources().getDrawable(R.drawable.tab_study_selector);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                studyRadioButton.setCompoundDrawables(null, drawable, null, null);
                studyRadioButton.setText("学习");

                break;

            case 1:
                // 课程中心
                if (mCourseFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mCourseFragment = new CourseFragment();
                    transaction.add(R.id.container_view, mCourseFragment, COURSE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mCourseFragment);
                }

                setTitle(R.string.course_center);

                mCurFragment = mCourseFragment;

                break;

            case 2:
                // 公开课
                if (mOpenCourseFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mOpenCourseFragment = new OpenCourseFragment();
                    transaction.add(R.id.container_view, mOpenCourseFragment, OPENCOURSE);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mOpenCourseFragment);
                }

                setTitle(R.string.opencourse);

                mCurFragment = mOpenCourseFragment;

                break;

            case 3:
                // 记录
                if (mStudyRecordFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mStudyRecordFragment = new StudyRecordFragment();
                    transaction.add(R.id.container_view, mStudyRecordFragment, RECORD);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mStudyRecordFragment);
                }

                setTitle(R.string.record_index);

                mCurFragment = mStudyRecordFragment;

                break;

            case 4:
                //vip
                if (mVipIndexFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mVipIndexFragment = new VipIndexFragment();
                    transaction.add(R.id.container_view, mVipIndexFragment, VIP);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mVipIndexFragment);
                }

                setTitle(R.string.vip_index);

                mCurFragment = mVipIndexFragment;

                break;
            case 5:
                //interview
                if (mInterviewIndexFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mInterviewIndexFragment = new InterviewIndexFragment();
                    transaction.setCustomAnimations(R.anim.translate_right_in, R.anim.translate_left_in);
                    transaction.add(R.id.container_view, mInterviewIndexFragment, INTERVIEW);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.setCustomAnimations(R.anim.translate_right_in, R.anim.translate_left_in);
                    transaction.show(mInterviewIndexFragment);
                }

                setTitle(R.string.interview_index);

                mCurFragment = mInterviewIndexFragment;
                indexString = "interview";

                Drawable interDrawable = getResources().getDrawable(R.drawable.tab_interview_selector);
                interDrawable.setBounds(0, 0, interDrawable.getMinimumWidth(), interDrawable.getMinimumHeight());
                studyRadioButton.setCompoundDrawables(null, interDrawable, null, null);
                studyRadioButton.setText("面试");

                break;
            default:
                break;
        }

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
        mStudyIndexFragment = (StudyIndexFragment) mFragmentManager.findFragmentByTag(STUDY);
        if (mStudyIndexFragment != null) transaction.hide(mStudyIndexFragment);

        // 课程中心
        mCourseFragment = (CourseFragment) mFragmentManager.findFragmentByTag(COURSE);
        if (mCourseFragment != null) transaction.hide(mCourseFragment);

        // 记录
        mStudyRecordFragment = (StudyRecordFragment) mFragmentManager.findFragmentByTag(RECORD);
        if (mStudyRecordFragment != null) transaction.hide(mStudyRecordFragment);

        //Vip
        mVipIndexFragment = (VipIndexFragment) mFragmentManager.findFragmentByTag(VIP);
        if (mVipIndexFragment != null) transaction.hide(mVipIndexFragment);

        //interview
        mInterviewIndexFragment = (InterviewIndexFragment) mFragmentManager.findFragmentByTag(INTERVIEW);
        if (mInterviewIndexFragment != null) transaction.hide(mInterviewIndexFragment);

        // 公开课
        mOpenCourseFragment = (OpenCourseFragment) mFragmentManager.findFragmentByTag(OPENCOURSE);
        if (mOpenCourseFragment != null) transaction.hide(mOpenCourseFragment);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            return;
        }

        switch (apiName) {
            case "get_rate_course":
                //处理邀请评价送课逻辑，待修改
                Globals.rateCourseResp = GsonManager.getModel(response, RateCourseResp.class);
//                if ("menu".equals(mFrom)) AlertManager.showGradeAlert(this, "Click");
                break;

            case "is_user_merged":
                IsUserMergedResp resp = GsonManager.getModel(response, IsUserMergedResp.class);
                if (resp == null || resp.getResponse_code() != 1 || !resp.is_merged()) break;
                LoginModel.userMergedAlert(this);
                break;

            default:
                break;
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
    }
}
