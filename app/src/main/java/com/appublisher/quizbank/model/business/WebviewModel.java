package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseConsultResp;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseUrlResp;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jinbao on 2016/7/22.
 */
public class WebviewModel {

    /**
     * 设置营销QQ
     * @param activity Activity
     */
    public static void setMarketQQ(Activity activity) {
        GlobalSetting globalSetting = GlobalSettingDAO.findById();
        if (globalSetting == null) return;


        GlobalSettingsResp globalSettingsResp =
                GsonManager.getModel(globalSetting.content, GlobalSettingsResp.class);

        if (globalSettingsResp == null || globalSettingsResp.getResponse_code() != 1)
            return;

        String qq = globalSettingsResp.getMarket_qq();
        String url="mqqwpa://im/chat?chat_type=wpa&uin=" + qq;

        try {
            if (activity instanceof WebViewActivity) ((WebViewActivity) activity).mIsFromQQ = true;
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            ToastManager.showToast(activity, "您未安装手机QQ，请到应用市场下载……");
        }
    }

    /**
     * 处理轮询回调
     * @param activity WebViewActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseConsultResp(final WebViewActivity activity,
                                                 JSONObject response) {
        if (response == null || activity.mHasShowOpenCourseConsult) return;


        OpenCourseConsultResp openCourseConsultResp =
                GsonManager.getModel(response.toString(), OpenCourseConsultResp.class);

        if (openCourseConsultResp == null || openCourseConsultResp.getResponse_code() != 1) return;

        boolean alertStatus = openCourseConsultResp.isAlert_status();

        if (alertStatus) {
            // 暂停计时器
            if (activity.mTimer != null) {
                activity.mTimer.cancel();
                activity.mTimer = null;
            }

            activity.mLlOpenCourseConsult.setVisibility(View.VISIBLE);
            activity.mHasShowOpenCourseConsult = true;

            activity.mTvOpenCourseConsult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Umeng
                    activity.mUmengQQ = "2";

                    // 获取营销QQ
                    setMarketQQ(activity);
                }
            });
        }
    }

    /**
     * 处理获取公开课连接回调
     * @param activity WebViewActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseUrlResp(WebViewActivity activity, JSONObject response) {
        if (response == null) return;


        OpenCourseUrlResp openCourseUrlResp =
                GsonManager.getModel(response.toString(), OpenCourseUrlResp.class);

        if (openCourseUrlResp == null || openCourseUrlResp.getResponse_code() != 1) return;

        String url = openCourseUrlResp.getUrl();

        // 展示WebView
        activity.showWebView(url);

        // 获取轮询
        setHeartbeat(activity);
    }

    /**
     * 设置轮询
     * @param activity WebViewActivity
     */
    private static void setHeartbeat(final WebViewActivity activity) {
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        if (globalSetting == null) return;

        GlobalSettingsResp globalSettingsResp = GsonManager.getModel(
                globalSetting.content, GlobalSettingsResp.class);

        if (globalSettingsResp == null || globalSettingsResp.getResponse_code() != 1) return;

        int heartbeat = globalSettingsResp.getOpen_course_heartbeat();

        // 设置轮询
        if (activity.mTimer != null) {
            activity.mTimer.cancel();
            activity.mTimer = null;
        }

        activity.mTimer = new Timer();
        activity.mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                activity.mHandler.sendEmptyMessage(WebViewActivity.TIME_ON);
            }
        }, 2000, heartbeat * 1000);
    }
}
