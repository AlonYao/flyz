package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.model.business.EvaluationModel;
import com.appublisher.quizbank.model.business.PracticeReportModel;

/**
 * Created by jinbao on 2016/7/22.
 */
public class UMengManager {

    /**
     * 检查当天是否进行友盟分享
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    public static void checkUmengShare(Activity activity) {
        // 获取上次记录的离开日期
        String firstLeaveDate = GradeDAO.getFirstLeaveDate(Globals.appVersion, activity);

        if (firstLeaveDate != null && firstLeaveDate.equals(Utils.getCurDate())) {
            // 如果是同一天发生的
            if (activity instanceof PracticeReportActivity)
                // 如果是PracticeReportActivity，额外需要传送Umeng统计的数据
                UmengManager.onEvent((PracticeReportActivity) activity, "Back");

            activity.finish();

        } else {
            // 如果是当前的第一次
            showEveryDayShareAlert(activity);
        }

        // 更新离开日期
        GradeDAO.updateFirstLeaveDate(Globals.appVersion, Utils.getCurDateString(), activity);
    }

    /**
     * 展示每天友盟分享提醒Alert
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    private static void showEveryDayShareAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.grade_everydayshare_alert_msg)
                .setTitle(R.string.alert_title)
                .setPositiveButton(R.string.grade_everydayshare_alert_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activity instanceof EvaluationActivity) {
                                    EvaluationModel.setUmengShare((EvaluationActivity) activity);
                                } else if (activity instanceof PracticeReportActivity) {
                                    PracticeReportModel.setUmengShare(
                                            (PracticeReportActivity) activity);
                                }

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.grade_everydayshare_alert_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (activity instanceof PracticeReportActivity)
                                    // 如果是PracticeReportActivity，额外需要传送Umeng统计的数据
                                    UmengManager.onEvent(
                                            (PracticeReportActivity) activity, "Back");

                                activity.finish();
                            }
                        })
                .create().show();
    }
}
