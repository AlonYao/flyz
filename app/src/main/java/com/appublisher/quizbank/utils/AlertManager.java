package com.appublisher.quizbank.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.model.MeasureModel;

/**
 * Alert管理
 */
public class AlertManager {

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
}
