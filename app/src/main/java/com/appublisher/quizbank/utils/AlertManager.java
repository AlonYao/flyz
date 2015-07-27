package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.dao.PaperDAO;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.business.MeasureAnalysisModel;
import com.appublisher.quizbank.model.business.MeasureModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.model.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.umeng.fb.FeedbackAgent;

import org.json.JSONArray;

/**
 * Alert管理
 */
public class AlertManager {

    private static AlertDialog mAlertLastPage;

    /**
     * 暂停Alert
     * @param activity MeasureActivity
     */
    public static void pauseAlert(final MeasureActivity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_pause);

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
     * @param activity MeasureActivity
     */
    public static void saveTestAlert(final MeasureActivity activity) {
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
                                    MeasureModel.saveQuestionTime(activity);

                                // 保存至本地
                                PaperDAO.save(activity.mPaperId, activity.mCurPosition);

                                // 提交数据
                                MeasureModel.submitPaper(activity);

                                // 保存练习
                                ToastManager.showToast(activity, "保存成功");
                                dialog.dismiss();

                                // Umeng 练习统计
                                UmengManager.sendToUmeng(activity, "0");

                                activity.finish();
                            }
                        }).show();
    }

    /**
     * 末题引导Alert
     * @param activity MeasureAnalysisActivity
     */
    public static void lastPageAlert(final MeasureAnalysisActivity activity) {
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
        if ("mokao".equals(activity.mAnalysisType) || "entire".equals(activity.mAnalysisType)) {
            tvAnother.setVisibility(View.GONE);
        } else {
            tvAnother.setVisibility(View.VISIBLE);
        }

        mAlertLastPage.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MeasureAnalysisModel.mIsShowAlert = false;
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
                UmengManager.sendToUmeng(activity, "Again");
            }
        });

        // 返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Umeng 练习类型统计
                UmengManager.sendToUmeng(activity, "Back");

                finishActivity(activity);
            }
        });

        // 看个直播
        OpenCourseModel.setOpenCourseBtn(activity, tvZhibo);
    }

    /**
     * 显示评分Alert
     * @param activity Activity
     */
    public static void showGradeAlert(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_grade);

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

                alertDialog.dismiss();

                // Umeng
                UmengManager.sendCountEvent(activity, "Rating", "Done", "No");
            }
        });

        // 评价
        tvGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 评价
                CommonModel.skipToGrade(activity);

                alertDialog.dismiss();

                // Umeng
                UmengManager.sendCountEvent(activity, "Rating", "Done", "Yes");
            }
        });

        // 吐槽
        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存时间戳
                GradeDAO.updateTimestamp(Globals.appVersion, System.currentTimeMillis());

                // 进入反馈
                FeedbackAgent agent = new FeedbackAgent(activity);
                agent.startFeedbackActivity();

                alertDialog.dismiss();

                // Umeng
                UmengManager.sendCountEvent(activity, "Rating", "Done", "No");
            }
        });
    }

    /**
     * 显示评价模块赠送课程信息
     * @param tvCourse Textview
     */
    private static void setGradeCourse(TextView tvCourse) {
        if (Globals.rateCourseResp == null || Globals.rateCourseResp.getResponse_code() != 1)
            return;

        String price = String.valueOf(Globals.rateCourseResp.getCourse_price());
        String name = Globals.rateCourseResp.getCourse_name();

        tvCourse.setText("卖个萌，求好评\n你将获赠价值" + price + "元的\n\"" + name + "\"\n直播课一套");
    }

    /**
     * 显示评价成功Alert
     * @param activity Activity
     */
    public static void showGradeSuccessAlert(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_grade_success);

        ImageView ivClose = (ImageView) window.findViewById(R.id.grade_close);
        TextView tvLearn = (TextView) window.findViewById(R.id.grade_learn);

        // 关闭
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存本地存储
                alertDialog.dismiss();
            }
        });

        // 去学习
        tvLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ToastManager.showToast(activity, "跳转到课程详情页");
            }
        });
    }

    /**
     * 显示评价失败Alert
     * @param activity Activity
     */
    public static void showGradeFailAlert(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_item_grade);

        ImageView ivClose = (ImageView) window.findViewById(R.id.alert_grade_close);
        TextView tvReGrade = (TextView) window.findViewById(R.id.grade_regrade);
        TextView tvAbandon = (TextView) window.findViewById(R.id.grade_abandon);

        // 关闭
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存本地存储
                alertDialog.dismiss();
            }
        });

        // 重新评价
        tvReGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 评价
                CommonModel.skipToGrade(activity);
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
     * @param activity MeasureAnalysisActivity
     */
    public static void deleteErrorQuestionAlert(final MeasureAnalysisActivity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_delete_error_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_logout_positive,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 提交登出信息至服务器
                                new Request(activity, activity).deleteErrorQuestion(
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
     * @param activity AnswerSheetActivity
     * @param redoSubmit 是否是重做
     * @param duration_total 总做题时间
     * @param questions 用户答案信息
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
                                new Request(activity, activity).submitPaper(
                                        ParamBuilder.submitPaper(
                                                String.valueOf(activity.mPaperId),
                                                String.valueOf(activity.mPaperType),
                                                redoSubmit,
                                                String.valueOf(duration_total),
                                                questions.toString(),
                                                "done")
                                );

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
     * @param activity OpenCourseUnstartActivity
     */
    public static void bookOpenCourseAlert(final OpenCourseUnstartActivity activity,
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
                                new Request(activity, activity).bookOpenCourse(
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
     * @param activity BindingMobileActivity
     */
    public static void openCourseUserChangeAlert(final BindingMobileActivity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_userchange_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_logout_positive,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginModel.setLogout(activity);
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
     * 报告错题Alert
     * @param activity MeasureAnalysisActivity
     * @param type 类型
     */
    public static void reportErrorAlert(final MeasureAnalysisActivity activity,
                                        final String type) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_report_error_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_report_error_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Request(activity).reportErrorQuestion(
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
}
