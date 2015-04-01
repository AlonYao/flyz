package com.appublisher.quizbank.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Loading控件
 */
public class ProgressDialogManager {

    private static ProgressDialog progressDialog;

    /**
     * 显示ProgressDialog
     * @param context  上下文
     */
    public static void showProgressDialog(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        progressDialog = ProgressDialog.show(context, null, "加载中，请稍候......");
    }

    /**
     * 关闭ProgressDialog
     */
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
