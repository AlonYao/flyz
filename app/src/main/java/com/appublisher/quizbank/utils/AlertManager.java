package com.appublisher.quizbank.utils;

import android.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;

/**
 * Alert管理
 */
public class AlertManager {

    public static void pauseAlert(final MeasureActivity activity) {
        // 小奖励
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
                alertDialog.dismiss();
            }
        });
    }
}
