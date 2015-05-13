package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.model.MeasureAnalysisModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.login.activity.RegisterActivity;
import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;

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
                                // 提交数据
                                MeasureModel.submitPaper(activity);

                                // 保存练习
                                ToastManager.showToast(activity, "保存成功");
                                dialog.dismiss();
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

                Logger.i(activity.mAnalysisType == null ? "null" : activity.mAnalysisType);
                Logger.i(String.valueOf(activity.mHierarchyId));
                Logger.i(String.valueOf(activity.mHierarchyLevel));

                if ("auto".equals(activity.mAnalysisType)) {
                    Intent intent = new Intent(activity, PracticeDescriptionActivity.class);
                    intent.putExtra("paper_type", activity.mAnalysisType);
                    intent.putExtra("paper_name", activity.getString(R.string.paper_type_auto));
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
                    activity.startActivity(intent);

                    finishActivity(activity);

                } else {
                    mAlertLastPage.dismiss();
                }
            }
        });

        // 返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(activity);
            }
        });

        // 看个直播
        tvZhibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastManager.showToast(activity, "直播课 施工中……");
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
     * @param activity RegisterActivity
     */
    public static void openCourseUserChangeAlert(final RegisterActivity activity) {
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
}
