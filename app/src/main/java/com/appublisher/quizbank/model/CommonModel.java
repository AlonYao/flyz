package com.appublisher.quizbank.model;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.appublisher.quizbank.R;

/**
 * 通用模型
 */
public class CommonModel {

    /**
     * 设置Toolbar
     * @param activity Activity
     */
    public static void setToolBar(ActionBarActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setBackgroundDrawable(
                activity.getResources().getDrawable(R.drawable.actionbar_bg));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置文字长按复制
     * @param textView textView
     */
    public static void setTextLongClickCopy(TextView textView) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            textView.setTextIsSelectable(true);
        }
    }
}
