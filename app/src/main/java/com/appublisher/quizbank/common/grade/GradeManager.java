package com.appublisher.quizbank.common.grade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_course.coursecenter.netdata.GradeCourseResp;
import com.appublisher.lib_course.coursecenter.netdata.RateCourseResp;
import com.appublisher.lib_login.activity.LoginActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by bihaitian on 16/4/15.
 */
public class GradeManager implements RequestCallback{

    private SharedPreferences mSharedPreferences;
    private static AlertDialog mAlertDialog;
    private QRequest mRequest;
    private Context mContext;
    private RateCourseResp mRateCourseResp;

    public GradeManager(Context context) {
        mSharedPreferences = context.getSharedPreferences("grade", Context.MODE_PRIVATE);
        mRequest = new QRequest(context, this);
        mContext = context;
    }

    /**
     * 处理邀请评论逻辑
     */
    public void dealGrade() {
        /** 邀请评论 **/
        boolean isGrade = mSharedPreferences.getBoolean("isGrade", false);
        if (isGrade) return;

        if (isGradeAlert()) {//未点击过评价按钮
            mRequest.getRateCourse(ParamBuilder.getRateCourse("getCourse", ""));
        } else if (isShowGradeFail()) {
            // 视为未完成评价
            showGradeFailAlert();
        } else if (isShowGradeSuccess()) {
            //判断是否是登录状态
            if (LoginModel.isLogin()) {
                // 视为评价完成，开通课程
                openupCourse();
            } else {
                boolean isNotice = mSharedPreferences.getBoolean("unlogin_grade_success", true);
                if (isNotice)
                    showGetCourseAlert();
            }
        }
    }

    /**
     * 开通课程
     */
    private void openupCourse() {
        int course_id_from = mSharedPreferences.getInt("course_id", -1);
        if (course_id_from != -1 && LoginModel.isLogin()) {
            mRequest.getRateCourse(ParamBuilder.getRateCourse(
                    "enroll", String.valueOf(course_id_from)));
        } else {
            mRequest.getRateCourse(ParamBuilder.getRateCourse(
                    "enroll", String.valueOf(course_id_from)));
        }
    }

    /**
     * 提示用户登录获取课程
     */
    @SuppressLint("CommitPrefEdits")
    private void showGetCourseAlert() {
        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.setCancelable(false);

        try {
            if (!mAlertDialog.isShowing())
                mAlertDialog.show();

            Window window = mAlertDialog.getWindow();
            if (window == null) return;
            window.setContentView(R.layout.alert_item_grade_unlogin);
            window.setBackgroundDrawableResource(R.color.transparency);

            ImageView ivClose = (ImageView) window.findViewById(R.id.alert_grade_close);
            TextView tvReGrade = (TextView) window.findViewById(R.id.grade_regrade);
            TextView tvAbandon = (TextView) window.findViewById(R.id.grade_abandon);

            // 关闭
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 不再提示
                    clearLoginAlertNotice();
                    mAlertDialog.dismiss();

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "0");
                    UmengManager.onEvent(mContext, "Rating", map);
                }
            });

            // 登录获取课程
            tvReGrade.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    mAlertDialog.dismiss();
                }
            });

            // 不要课了
            tvAbandon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gradeSuccessAfter();
                    mAlertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示评价失败Alert
     */
    private void showGradeFailAlert() {
        try {
            if (mAlertDialog == null)
                mAlertDialog = new AlertDialog.Builder(mContext).create();
            mAlertDialog.setCancelable(false);

            if (!mAlertDialog.isShowing())
                mAlertDialog.show();

            Window window = mAlertDialog.getWindow();
            if (window == null) return;
            window.setContentView(R.layout.alert_item_grade_fail);
            window.setBackgroundDrawableResource(R.color.transparency);

            ImageView ivClose = (ImageView) window.findViewById(R.id.alert_grade_close);
            TextView tvReGrade = (TextView) window.findViewById(R.id.grade_regrade);
            TextView tvAbandon = (TextView) window.findViewById(R.id.grade_abandon);

            // 关闭
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更新时间戳，下次再提醒
                    clearFirstTime();
                    mAlertDialog.dismiss();
                }
            });

            // 重新评价
            tvReGrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonModel.skipToGrade(mContext,
                            mContext.getPackageName(),
                            new ICommonCallback() {
                        @Override
                        public void callback(boolean status) {
                            // 评价
                            if (status) {
                                setGradeTime();
                            } else {
                                clearFirstTime();
                            }
                        }
                    });
                    mAlertDialog.dismiss();

                    // Umeng统计
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "2");
                    UmengManager.onEvent(mContext, "Rating", map);
                }
            });

            // 送课也不要
            tvAbandon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearFirstTime();
                    mAlertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 是否显示评价alert
     *
     * @return
     */
    @SuppressLint("CommitPrefEdits")
    private boolean isGradeAlert() {
        Long firstUserTime = mSharedPreferences.getLong("grade_first_time", -1);
        if (firstUserTime == -1) {
            mSharedPreferences.edit()
                    .putLong("grade_first_time", System.currentTimeMillis()).commit();
            return false;
        }
        Long gradeTime = mSharedPreferences.getLong("user_grade_time", -1);
        if (gradeTime != -1) return false;
        Long dif = System.currentTimeMillis() - firstUserTime;
        return (dif / (1000 * 60 * 60)) > 72;
    }

    /**
     * 保存用户评价时间
     */
    @SuppressLint("CommitPrefEdits")
    private void setGradeTime() {
        mSharedPreferences.edit().putLong("user_grade_time", System.currentTimeMillis()).commit();
    }

    /**
     * 重置时间
     */
    @SuppressLint("CommitPrefEdits")
    private void clearFirstTime() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("grade_first_time", System.currentTimeMillis());
        editor.remove("user_grade_time");
        editor.commit();
    }

    /**
     * 是否显示评价失败
     *
     * @return
     */
    private boolean isShowGradeFail() {
        Long gradeTime = mSharedPreferences.getLong("user_grade_time", -1);
        if (gradeTime == -1) return false;
        Long dif = System.currentTimeMillis() - gradeTime;
        return dif / 1000 < 10;
    }

    /**
     * 是否显示评价成功
     *
     * @return
     */
    private boolean isShowGradeSuccess() {
        Long gradeTime = mSharedPreferences.getLong("user_grade_time", -1);
        int course_id = mSharedPreferences.getInt("course_id", -1);
        return gradeTime != -1 && course_id != -1;
    }

    /**
     * 不再提示登录获取课程
     */
    @SuppressLint("CommitPrefEdits")
    private void clearLoginAlertNotice() {
        mSharedPreferences.edit().putBoolean("unlogin_grade_success", false).commit();
    }

    /**
     * 邀请评价成功获取课程后操作
     */
    @SuppressLint("CommitPrefEdits")
    private void gradeSuccessAfter() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("isGrade", true);
        editor.remove("unlogin_grade_success");
        editor.remove("user_grade_time");
        editor.remove("grade_first_time");
        editor.remove("course_id");
        editor.commit();
    }

    /**
     * 显示评价模块赠送课程信息
     *
     * @param tvCourse Textview
     */
    private void setGradeCourse(TextView tvCourse) {
        if (mRateCourseResp == null || mRateCourseResp.getResponse_code() != 1)
            return;

        String price = String.valueOf(mRateCourseResp.getCourse_price());
        String name = mRateCourseResp.getCourse_name();

        String text = "卖个萌，求好评\n你将获赠价值" + price + "元的\n\"" + name + "\"\n直播课一套";
        tvCourse.setText(text);
    }

    /**
     * 显示评分Alert
     */
    private void showGradeAlert(final String umengEntry) {
        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.setCancelable(false);
        try {
            if (!mAlertDialog.isShowing())
                mAlertDialog.show();

            Window window = mAlertDialog.getWindow();
            if (window == null) return;
            window.setContentView(R.layout.alert_item_grade);
            window.setBackgroundDrawableResource(R.color.transparency);

            ImageView ivClose = (ImageView) window.findViewById(R.id.alert_grade_close);
            TextView tvGrade = (TextView) window.findViewById(R.id.alert_grade_grade);
            TextView tvFeedback = (TextView) window.findViewById(R.id.alert_grade_feedback);
            TextView tvCourse = (TextView) window.findViewById(R.id.grade_course);

            // 显示赠送课程详情
            setGradeCourse(tvCourse);

            // 关闭
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 保存时间戳
                    clearFirstTime();
                    mAlertDialog.dismiss();

                    // Umeng统计
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Type", umengEntry);
                    map.put("Action", "0");
                    UmengManager.onEvent(mContext, "Rating", map);
                }
            });

            // 评价
            tvGrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 评价
                    CommonModel.skipToGrade(
                            mContext,
                            mContext.getPackageName(),
                            new ICommonCallback() {
                        @Override
                        public void callback(boolean status) {
                            if (status) {
                                setGradeTime();
                            } else {
                                clearFirstTime();
                            }
                        }
                    });
                    mAlertDialog.dismiss();

                    // Umeng统计
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "2");
                    UmengManager.onEvent(mContext, "Rating", map);
                }
            });

            // 吐槽
            tvFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 保存时间戳
                    clearFirstTime();

                    // 进入反馈
                    CommonModel.skipToFeedback();

                    mAlertDialog.dismiss();

                    // Umeng统计
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "1");
                    UmengManager.onEvent(mContext, "Rating", map);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示评价成功Alert
     *
     * @param jump_url 跳转地址（课程详情页面）
     */
    private void showGradeSuccessAlert(final String jump_url) {
        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.setCancelable(false);
        try {
            if (!mAlertDialog.isShowing())
                mAlertDialog.show();
            Window window = mAlertDialog.getWindow();
            if (window == null) return;
            window.setContentView(R.layout.alert_item_grade_success);
            window.setBackgroundDrawableResource(R.color.transparency);

            ImageView ivClose = (ImageView) window.findViewById(R.id.grade_close);
            TextView tvLearn = (TextView) window.findViewById(R.id.grade_learn);

            // 关闭
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mAlertDialog.dismiss();
                }
            });

            // 去学习
            tvLearn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CourseWebViewActivity.class);
                    intent.putExtra("url", LoginParamBuilder.finalUrl(jump_url));
                    mContext.startActivity(intent);
                    mAlertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 邀请评价数据回调处理
     */
    @SuppressLint("CommitPrefEdits")
    private void dealRateCourseResp(JSONObject response) {
        int course_id = mSharedPreferences.getInt("course_id", -1);

        mRateCourseResp =
                GsonManager.getModel(response.toString(), RateCourseResp.class);
        if (mRateCourseResp == null) return;

        if (course_id != -1 && isShowGradeSuccess()) {//开通成功的
            ProgressDialogManager.closeProgressDialog();
            dealOpenupCourseResp(response);
        } else {//邀请评论alert
            mSharedPreferences.edit()
                    .putInt("course_id", mRateCourseResp.getCourse_id()).commit();
            showGradeAlert("Click");
        }
    }

    /**
     * 处理开通评价课程回调
     *
     * @param response 回调数据
     */
    private void dealOpenupCourseResp(JSONObject response) {
        GradeCourseResp gradeCourseResp =
                GsonManager.getModel(response.toString(), GradeCourseResp.class);
        gradeSuccessAfter();
        if (gradeCourseResp == null || gradeCourseResp.getResponse_code() != 1) return;
        showGradeSuccessAlert(gradeCourseResp.getJump_url());
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("get_rate_course".equals(apiName)) {
            dealRateCourseResp(response);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
