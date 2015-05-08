package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeDescriptionActivity;
import com.appublisher.quizbank.model.MeasureModel;

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
}
