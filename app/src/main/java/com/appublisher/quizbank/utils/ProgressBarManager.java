package com.appublisher.quizbank.utils;

import android.view.View;
import android.widget.RelativeLayout;

import com.appublisher.quizbank.R;

/**
 * ProgressBar管理
 */
public class ProgressBarManager {

    private static RelativeLayout mRlProgressBar;

    /**
     * 显示ProgressBar
     * @param view 需要显示PB的view
     */
    public static void showProgressBar(View view) {
        mRlProgressBar = (RelativeLayout) view.findViewById(R.id.progressbar);
        mRlProgressBar.setVisibility(View.VISIBLE);
        mRlProgressBar.setEnabled(false);
    }

    /**
     * 隐藏ProgressBar
     */
    public static void hideProgressBar() {
        if (mRlProgressBar == null) return;
        mRlProgressBar.setVisibility(View.GONE);
    }
}
