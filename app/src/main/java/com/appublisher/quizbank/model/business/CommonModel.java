package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.common.grade.ICommonCallback;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通用模型
 */
public class CommonModel {

    public static ShareCheckListener mShareCheckListener;
    private static final String SHARE_EVALUATION_LATEDATE = "share_evaluation_latedate";
    private static final String SHARE_REPORT_LATEDATE = "share_report_latedate";

    /**
     * 设置Toolbar
     * @param activity Activity
     */
    public static void setToolBar(ActionBarActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setBackgroundDrawable(
                activity.getResources().getDrawable(R.drawable.actionbar_bg));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置Toolbar
     * @param activity Activity
     */
    public static void setToolBar(AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.actionbar_bg);
        activity.setSupportActionBar(toolbar);
        assert activity.getSupportActionBar() != null;
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

    /**
     * 根据位置获取Listview的View
     * @param pos 位置
     * @param listView Listview
     * @return View
     */
    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * 更新使用次数
     */
    public static void updateUseCount() {
        int count = GlobalSettingDAO.getUseCount();
        count++;
        GlobalSettingDAO.saveUseCount(count);
    }

    /**
     * 跳转至评价页面
     */
    public static void skipToGrade(Activity activity) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));

        if (marketIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(marketIntent);
        } else {
            ToastManager.showToast(activity, "请安装应用市场……");
        }
    }

    /**
     * 跳转至评价页面
     */
    public static void skipToGrade(Activity activity, String packageName) {
        if (packageName == null || packageName.length() == 0) return;

        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri.parse("market://details?id=" + packageName));

        if (marketIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(marketIntent);
        } else {
            ToastManager.showToast(activity, "请安装应用市场……");
        }
    }

    /**
     * 设置文字下划线
     * @param textView TextView
     */
    public static void setTextUnderLine(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * 设置Bar标题
     * @param activity ActionBarActivity
     * @param title 标题
     */
    public static void setBarTitle(ActionBarActivity activity, String title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    /**
     * 设置Bar标题
     * @param activity ActionBarActivity
     * @param title 标题
     */
    public static void setBarTitle(AppCompatActivity activity, String title) {
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setTitle(title);
    }

    /**
     * 设置EditText点击时提示文字隐藏
     * @param editText EditText
     * @param pre 之前显示的提示文字
     */
    public static void setEditTextHintHideOnFocus(final EditText editText, final String pre) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText.setHint("");
                } else {
                    editText.setHint(pre == null ? "" : pre);
                }
            }
        });
    }

    /**
     * 跳转到反馈
     */
    public static void skipToFeedback() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", LoginModel.getUserId());
            object.put("sno", LoginModel.getSno());
            object.put("userMobile", LoginModel.getUserMobile());
            object.put("userName", LoginModel.getNickName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackAPI.setAppExtInfo(object);

        FeedbackAPI.openFeedbackActivity();
    }

    /**
     * 初始化反馈
     * @param activity Activity
     */
    public static void initFeedback(Activity activity) {
        FeedbackAPI.init(activity.getApplication(), activity.getString(R.string.ali_bc_appkey));
    }

    public interface ShareCheckListener {
        void onShare();
    }

    public static SharedPreferences getQuizBankSPF(Context context) {
        return context.getSharedPreferences("quizbank_store", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getGlobalSettingSPF(Context context) {
        return context.getSharedPreferences("global_setting", Context.MODE_PRIVATE);
    }

    public static GlobalSettingsResp getGlobalSetting(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                "global_setting", Context.MODE_PRIVATE);
        if (spf == null) return null;
        String data = spf.getString("global_setting", "");
        return GsonManager.getModel(data, GlobalSettingsResp.class);
    }

    /**
     * 检查当天是否进行友盟分享
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    public static void checkUmengShare(Activity activity, ShareCheckListener listener) {
        mShareCheckListener = listener;
        // 获取上次记录的离开日期
        SharedPreferences spf = getQuizBankSPF(activity);
        if (spf == null) {
            activity.finish();
            return;
        }

        String lateDate;
        String curDate = Utils.getCurDateString();

        if (activity instanceof EvaluationActivity) {
            lateDate = spf.getString(SHARE_EVALUATION_LATEDATE, "");
        } else if (activity instanceof MeasureReportActivity) {
            lateDate = spf.getString(SHARE_REPORT_LATEDATE, "");
        } else {
            lateDate = curDate;
        }

        if (lateDate.equals(curDate)) {
            activity.finish();
        } else {
            // 如果是当前的第一次
            showEveryDayShareAlert(activity);
        }
    }

    /**
     * 展示每天友盟分享提醒Alert
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    @SuppressLint("CommitPrefEdits")
    private static void showEveryDayShareAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.grade_everydayshare_alert_msg)
                .setTitle(R.string.alert_title)
                .setPositiveButton(R.string.grade_everydayshare_alert_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mShareCheckListener != null) mShareCheckListener.onShare();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.grade_everydayshare_alert_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                                dialog.dismiss();
                            }
                        })
                .create().show();

        // 更新本地缓存
        SharedPreferences spf = getQuizBankSPF(activity);
        if (spf != null) {
            SharedPreferences.Editor editor = spf.edit();
            if (activity instanceof EvaluationActivity) {
                editor.putString(SHARE_EVALUATION_LATEDATE, Utils.getCurDateString());
            } else if (activity instanceof MeasureReportActivity) {
                editor.putString(SHARE_REPORT_LATEDATE, Utils.getCurDateString());
            }
            editor.commit();
        }
    }

    /**
     * 跳转至评价页面
     */
    public static void skipToGrade(Context context,
                                   String packageName,
                                   ICommonCallback iCommonCallback) {
        if (packageName == null || packageName.length() == 0) return;

        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        marketIntent.setData(Uri.parse("market://details?id=" + packageName));

        if (marketIntent.resolveActivity(context.getPackageManager()) != null) {
            if (iCommonCallback != null)
                iCommonCallback.callback(true);
            context.startActivity(marketIntent);
        } else {
            if (iCommonCallback != null)
                iCommonCallback.callback(false);
            ToastManager.showToast(context, "请安装应用市场，如应用宝、百度手机助手、360手机助手等……");
        }
    }

}
