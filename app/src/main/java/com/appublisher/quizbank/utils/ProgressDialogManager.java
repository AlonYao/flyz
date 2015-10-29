package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Loading控件
 */
public class ProgressDialogManager {

    private static ProgressDialog progressDialog;

    /**
     * 显示ProgressDialog
     * @param context  上下文
     */
    public static void showProgressDialog(final Context context, boolean cancelable) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        progressDialog = ProgressDialog.show(context, null, "加载中，请稍候......");

        if (cancelable) {
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressDialog.dismiss();
                    ((Activity) context).finish();
                }
            });
        }
    }

    /**
     * 显示ProgressDialog
     * @param context  上下文
     */
    public static void showProgressDialog(final Context context) {
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
