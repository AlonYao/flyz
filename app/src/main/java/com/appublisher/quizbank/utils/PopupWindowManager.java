package com.appublisher.quizbank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;

/**
 * Created on 15/12/18.
 */
public class PopupWindowManager {
    /**
     * 1.5版本，能力评估页加更详细的分类信息
     *
     * @param view
     * @param context
     */
    public static void showUpdateEvaluation(View view, Context context) {
        View contentView = LayoutInflater.from(QuizBankApp.getInstance().getApplicationContext()).inflate(R.layout.update_evaluation, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        //弹出后记录
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putBoolean("detailCategory", false);
        editor.commit();
    }

    /**
     * 1.5版本，联系报告页排名
     *
     * @param view
     * @param context
     */
    public static void showUpdatePracticeReport(View view, Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.update_practice_report, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        //弹出后记录
        SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
        editor.putBoolean("rankInfo", false);
        editor.commit();
    }

}
