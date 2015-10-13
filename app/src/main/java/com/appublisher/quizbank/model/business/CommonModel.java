package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.utils.ToastManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

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
     * 跳转到友盟反馈
     * @param activity Activity
     */
    public static void skipToUmengFeedback(Activity activity) {
        final FeedbackAgent agent = new FeedbackAgent(activity);
        agent.startFeedbackActivity();

        UserInfoModel userInfoModel = LoginModel.getUserInfoM();

        if (userInfoModel == null) return;

        UserInfo info = agent.getUserInfo();
        if (info == null)
            info = new UserInfo();
        Map<String, String> contact = info.getContact();
        if (contact == null)
            contact = new HashMap<>();

        contact.put("plain",
                "userId:" + userInfoModel.getUser_id()
                        + ",sno:" + userInfoModel.getSno()
                        + ",userMobile:" + userInfoModel.getMobile_num()
                        + ",userName:" + userInfoModel.getNickname());
        info.setContact(contact);

        agent.setUserInfo(info);

        new Thread(new Runnable() {
            @Override
            public void run() {
                agent.updateUserInfo();
            }
        }).start();
    }
}
