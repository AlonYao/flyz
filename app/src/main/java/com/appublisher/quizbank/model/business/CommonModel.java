package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;

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
        activity.startActivity(marketIntent);
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
}
