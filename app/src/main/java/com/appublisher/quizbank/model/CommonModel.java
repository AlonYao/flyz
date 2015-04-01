package com.appublisher.quizbank.model;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

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
                new ColorDrawable(activity.getResources().getColor(R.color.toolbar_bg)));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
