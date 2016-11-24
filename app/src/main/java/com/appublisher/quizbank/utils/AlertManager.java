package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_course.opencourse.activity.OpenCourseActivity;
import com.appublisher.lib_login.activity.UserInfoActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.LegacyMeasureActivity;
import com.appublisher.quizbank.activity.LegacyMeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.dao.PaperDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.LegacyMeasureAnalysisModel;
import com.appublisher.quizbank.model.business.LegacyMeasureModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Alert管理
 */
public class AlertManager {

    private static AlertDialog mAlertLastPage;
    private static AlertDialog mAlertGrade;

    /**
     * 暂停Alert
     *
     * @param activity LegacyMeasureActivity
     */
    public static void pauseAlert(final LegacyMeasureActivity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_pause);
        window.setBackgroundDrawableResource(R.color.transparency);

        TextView textView = (TextView) window.findViewById(R.id.alert_pause_tv);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.mCurTimestamp = System.currentTimeMillis();
                activity.startTimer();
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 保存测验Alert
     *
     * @param activity LegacyMeasureActivity
     */
    public static void saveTestAlert(final LegacyMeasureActivity activity) {
        if (LegacyMeasureActivity.mockpre) {
            new AlertDialog.Builder(activity)
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
                                    // 如果在第一页退出，更新第一页的时长
                                    if (activity.mCurPosition == 0)
                                        activity.mModel.saveQuestionTime();

                                    // 保存至本地
                                    PaperDAO.save(activity.mPaperId, activity.mCurPosition);
                                    // 提交数据
                                    if("mockpre".equals(activity.mFrom)){
                                        LegacyMeasureModel.autoSubmitPaper(activity);
                                    }else{
                                        LegacyMeasureModel.submitPaper(activity);
                                    }
                                    // 保存练习
                                    ToastManager.showToast(activity, "保存成功");
                                    dialog.dismiss();

                                    // Umeng 练习统计
                                    UmengManager.onEvent(activity, "0");

                                    activity.finish();
                                }
                            }).show();
        } else {
            new AlertDialog.Builder(activity)
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
                                    // 如果在第一页退出，更新第一页的时长
                                    if (activity.mCurPosition == 0)
                                        activity.mModel.saveQuestionTime();

                                    // 保存至本地
                                    PaperDAO.save(activity.mPaperId, activity.mCurPosition);

                                    // 提交数据
                                    LegacyMeasureModel.submitPaper(activity);

                                    // 保存练习
                                    ToastManager.showToast(activity, "保存成功");
                                    dialog.dismiss();

                                    // Umeng 练习统计
                                    UmengManager.onEvent(activity, "0");

                                    activity.finish();
                                }
                            }).show();
        }
    }

    /**
     * 末题引导Alert
     *
     * @param activity LegacyMeasureAnalysisActivity
     */
    public static void lastPageAlert(final LegacyMeasureAnalysisActivity activity) {
        if (mAlertLastPage != null && mAlertLastPage.isShowing()) return;

        mAlertLastPage = new AlertDialog.Builder(activity).create();
        mAlertLastPage.setCancelable(true);
        mAlertLastPage.show();

        Window window = mAlertLastPage.getWindow();
        window.setContentView(R.layout.alert_item_lastpage);

        TextView tvAnother = (TextView) window.findViewById(R.id.alert_lastpage_another);
        TextView tvBack = (TextView) window.findViewById(R.id.alert_lastpage_back);
        TextView tvZhibo = (TextView) window.findViewById(R.id.alert_lastpage_zhibo);

        // 再来一发
        if ("mokao".equals(activity.mAnalysisType) || "entire".equals(activity.mAnalysisType) || "mock".equals(activity.mAnalysisType)) {
            tvAnother.setVisibility(View.GONE);
        } else {
            tvAnother.setVisibility(View.VISIBLE);
        }

        mAlertLastPage.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LegacyMeasureAnalysisModel.mIsShowAlert = false;
            }
        });

        // 再来一发点击事件
        tvAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //noinspection IfCanBeSwitch
                if ("auto".equals(activity.mAnalysisType)) {
                    Intent intent = new Intent(activity, PracticeDescriptionActivity.class);
                    intent.putExtra("paper_type", activity.mAnalysisType);
                    intent.putExtra("paper_name", activity.getString(R.string.paper_type_auto));
                    intent.putExtra("umeng_entry", activity.mUmengEntry);
                    activity.startActivity(intent);

                    finishActivity(activity);

                } else if ("note".equals(activity.mAnalysisType)
                        || "collect".equals(activity.mAnalysisType)
                        || "error".equals(activity.mAnalysisType)) {
                    Intent intent = new Intent(activity, PracticeDescriptionActivity.class);
                    intent.putExtra("paper_type", activity.mAnalysisType);
                    intent.putExtra("paper_name", activity.mPaperName);
                    intent.putExtra("hierarchy_id", activity.mHierarchyId);
                    intent.putExtra("hierarchy_level", activity.mHierarchyLevel);
                    intent.putExtra("umeng_entry", activity.mUmengEntry);
                    activity.startActivity(intent);

                    finishActivity(activity);

                } else {
                    mAlertLastPage.dismiss();
                }

                // Umeng 练习类型统计
                UmengManager.onEvent(activity, "Again");
            }
        });

        // 返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Umeng 练习类型统计
                UmengManager.onEvent(activity, "Back");

                finishActivity(activity);
            }
        });

        // 看个直播
//        StudyIndexModel.setOpenCourseBtn(activity, tvZhibo);
    }

    /**
     * 显示评分Alert
     *
     * @param activity Activity
     */
    public static void showGradeAlert(final Activity activity, final String umengEntry) {
        if (activity.isFinishing()) return;

        mAlertGrade = new AlertDialog.Builder(activity).create();
        mAlertGrade.setCancelable(false);
        mAlertGrade.show();

        Window window = mAlertGrade.getWindow();
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
                GradeDAO.updateTimestamp(Globals.appVersion, System.currentTimeMillis());

                mAlertGrade.dismiss();

                // Umeng统计
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", umengEntry);
                map.put("Action", "0");
                MobclickAgent.onEvent(activity, "Rating", map);
            }
        });

        // 评价
        tvGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 评价
                CommonModel.skipToGrade(activity);
                GradeDAO.saveGradeTimestamp(Globals.appVersion, System.currentTimeMillis());

                mAlertGrade.dismiss();

                // Umeng统计
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", umengEntry);
                map.put("Action", "2");
                MobclickAgent.onEvent(activity, "Rating", map);
            }
        });

        // 吐槽
        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存时间戳
                GradeDAO.updateTimestamp(Globals.appVersion, System.currentTimeMillis());

                // 进入反馈
                CommonModel.skipToUmengFeedback(activity);

                mAlertGrade.dismiss();

                // Umeng统计
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", umengEntry);
                map.put("Action", "1");
                MobclickAgent.onEvent(activity, "Rating", map);
            }
        });
    }

    public static void dismissGradeAlert() {
        if (mAlertGrade != null && mAlertGrade.isShowing()) {
            mAlertGrade.dismiss();
            mAlertGrade = null;
        }
    }

    /**
     * 显示评价模块赠送课程信息
     *
     * @param tvCourse Textview
     */
    private static void setGradeCourse(TextView tvCourse) {
        if (Globals.rateCourseResp == null || Globals.rateCourseResp.getResponse_code() != 1)
            return;

        String price = String.valueOf(Globals.rateCourseResp.getCourse_price());
        String name = Globals.rateCourseResp.getCourse_name();

        String text = "卖个萌，求好评\n你将获赠价值" + price + "元的\n\"" + name + "\"\n直播课一套";
        tvCourse.setText(text);
    }

    /**
     * 显示评价成功Alert
     *
     * @param activity Activity
     * @param jump_url 跳转地址（课程详情页面）
     */
    public static void showGradeSuccessAlert(final Activity activity, final String jump_url) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_grade_success);
        window.setBackgroundDrawableResource(R.color.transparency);

        ImageView ivClose = (ImageView) window.findViewById(R.id.grade_close);
        TextView tvLearn = (TextView) window.findViewById(R.id.grade_learn);

        // 关闭
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // 去学习
        tvLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CourseWebViewActivity.class);
                intent.putExtra("url", LoginParamBuilder.finalUrl(jump_url));
                activity.startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 显示评价失败Alert
     *
     * @param activity Activity
     */
    public static void showGradeFailAlert(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
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
                GradeDAO.updateTimestamp(Globals.appVersion, System.currentTimeMillis());
                alertDialog.dismiss();
            }
        });

        // 重新评价
        tvReGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 评价
                CommonModel.skipToGrade(activity);
                GradeDAO.saveGradeTimestamp(Globals.appVersion, System.currentTimeMillis());

                alertDialog.dismiss();
            }
        });

        // 送课也不要
        tvAbandon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 关闭Activity
     *
     * @param activity Activity
     */
    private static void finishActivity(Activity activity) {
        if (mAlertLastPage == null) return;

        mAlertLastPage.dismiss();
        mAlertLastPage = null;
        activity.finish();
    }

    /**
     * 显示登出Alert
     *
     * @param userInfoActivity 用户个人信息Activity
     */
    public static void showLogoutAlert(final UserInfoActivity userInfoActivity) {
        new AlertDialog.Builder(userInfoActivity)
                .setMessage(R.string.alert_logout_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_logout_positive,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginModel.setLogout(userInfoActivity);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_logout_negative,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    /**
     * 显示登出Alert
     *
     * @param activity LegacyMeasureAnalysisActivity
     */
    public static void deleteErrorQuestionAlert(final LegacyMeasureAnalysisActivity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_delete_error_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_logout_positive,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 提交登出信息至服务器
                                new QRequest(activity, activity).deleteErrorQuestion(
                                        ParamBuilder.deleteErrorQuestion(
                                                String.valueOf(activity.mCurQuestionId)));

                                // 保存已删除的错题
                                if (activity.mDeleteErrorQuestions != null) {
                                    activity.mDeleteErrorQuestions.add(activity.mCurQuestionId);
                                }

                                // 更新Menu
                                Utils.updateMenu(activity);

                                ToastManager.showToast(activity, "删除成功");

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_logout_negative,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    /**
     * 如果有未完成题目时的提示
     *
     * @param activity       AnswerSheetActivity
     * @param redoSubmit     是否是重做
     * @param duration_total 总做题时间
     * @param questions      用户答案信息
     */
    public static void answerSheetNoticeAlert(final AnswerSheetActivity activity,
                                              final String redoSubmit,
                                              final int duration_total,
                                              final JSONArray questions) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_answersheet_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_answersheet_p,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialogManager.showProgressDialog(activity, false);
                                new QRequest(activity, activity).submitPaper(
                                        ParamBuilder.submitPaper(
                                                String.valueOf(activity.mPaperId),
                                                String.valueOf(activity.mPaperType),
                                                redoSubmit,
                                                String.valueOf(duration_total),
                                                questions.toString(),
                                                "done")
                                );
                                // 清除做题缓存
                                LegacyMeasureModel.clearUserAnswerCache(activity);
                                dialog.dismiss();
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

    /**
     * 预约直播课
     *
     * @param activity OpenCourseUnstartActivity
     */
    public static void bookOpenCourseAlert(final OpenCourseActivity activity,
                                           String mobileNum,
                                           final String courseId) {
        new AlertDialog.Builder(activity)
                .setMessage("提醒短信将发送到你的手机：" + "\n" + mobileNum)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_opencourse_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialogManager.showProgressDialog(activity, false);
                                new QRequest(activity, activity).bookOpenCourse(
                                        ParamBuilder.bookOpenCourse(courseId));

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    /**
     * 公开课模块提示用户切换账号Alert
     *
     * @param activity Activity
     */
    public static void openCourseUserChangeAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_userexist_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_userexist_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginModel.setLogout(activity);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_userexist_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    /**
     * 报告错题Alert
     *
     * @param activity LegacyMeasureAnalysisActivity
     * @param type     类型
     * @deprecated
     */
    public static void reportErrorAlert(final LegacyMeasureAnalysisActivity activity,
                                        final String type) {
        new AlertDialog.Builder(QuizBankApp.getInstance().getApplicationContext())
                .setMessage(R.string.alert_report_error_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_report_error_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new QRequest(activity).reportErrorQuestion(
                                        ParamBuilder.reportErrorQuestion(
                                                String.valueOf(activity.mCurQuestionId), type, ""));

                                ToastManager.showToast(activity, "提交成功");

                                dialog.dismiss();
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

    /**
     * 模考时间到Alert
     * @param activity LegacyMeasureActivity
     */
    public static void mockTimeOutAlert(final LegacyMeasureActivity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("时间到了要交卷啦！")
                .setTitle("提示")
                .setPositiveButton("好的",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }
}
