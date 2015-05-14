package com.appublisher.quizbank.model;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.RegisterActivity;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseConsultResp;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseDetailResp;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseM;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseUrlResp;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * OpenCourse Model
 */
public class OpenCourseModel {

    /**
     * 处理公开课详情回调
     * @param activity OpenCourseUnstartActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseDetailResp(final OpenCourseUnstartActivity activity,
                                                JSONObject response) {
        if (response == null) return;

        final Gson gson = GsonManager.initGson();
        OpenCourseDetailResp openCourseDetailResp = gson.fromJson(response.toString(),
                OpenCourseDetailResp.class);

        if (openCourseDetailResp == null || openCourseDetailResp.getResponse_code() != 1) return;

        OpenCourseM openCourse = openCourseDetailResp.getCourse();

        if (openCourse == null) return;

        // 公开课封面
        activity.mRequest.loadImage(openCourse.getCover_pic(), activity.mIvPic);

        // 公开课名字
        activity.mTvName.setText("名字：" + openCourse.getName());

        // 公开课时间
        String startTime = openCourse.getStart_time();
        String endTime = openCourse.getEnd_time();

        try {
            if (startTime != null) startTime = startTime.substring(0, 16);
            if (endTime != null) endTime = endTime.substring(11, 16);
        } catch (Exception e) {
            activity.mTvTime.setText("时间：" + openCourse.getStart_time()
                    + " - " + openCourse.getEnd_time());
        }

        activity.mTvTime.setText("时间：" + startTime + " - " + endTime);

        // 公开课讲师
        activity.mTvLector.setText("主讲：" + openCourse.getLector());

        // 预约状态
        boolean booked = openCourseDetailResp.isBooked();

        if (booked) {
            setBooked(activity);

        } else {
            activity.mTvNotice.setText(R.string.opencourse_notice_false);
            activity.mTvNotice.setTextColor(
                    activity.getResources().getColor(R.color.white));
            activity.mTvNotice.setBackgroundColor(
                    activity.getResources().getColor(R.color.answer_sheet_btn));

            activity.mTvNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 判断用户是否有手机号
                    User user = UserDAO.findById();

                    if (user == null) return;

                    UserInfoModel userInfo = gson.fromJson(user.user, UserInfoModel.class);

                    if (userInfo == null) return;

                    String mobileNum = userInfo.getMobile_num();

                    if (mobileNum == null || mobileNum.length() == 0) {
                        // 没有手机号
                        Intent intent = new Intent(activity, RegisterActivity.class);
                        intent.putExtra("from", "book_opencourse");
                        activity.startActivityForResult(intent,
                                ActivitySkipConstants.BOOK_OPENCOURSE);
                    } else {
                        // 有手机号
                        AlertManager.bookOpenCourseAlert(activity, mobileNum, activity.mContent);
                    }
                }
            });
        }
    }

    /**
     * 设置已预约状态
     * @param activity OpenCourseUnstartActivity
     */
    public static void setBooked(OpenCourseUnstartActivity activity) {
        activity.mTvNotice.setText(R.string.opencourse_notice_true);
        activity.mTvNotice.setTextColor(
                activity.getResources().getColor(R.color.setting_text));
        activity.mTvNotice.setBackgroundColor(
                activity.getResources().getColor(R.color.transparency));

        activity.mTvNotice.setOnClickListener(null);
    }

    /**
     * 处理预定公开课回调
     * @param activity OpenCourseUnstartActivity
     * @param response 数据回调
     */
    public static void dealBookOpenCourseResp(OpenCourseUnstartActivity activity,
                                              JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        CommonResp commonResp = gson.fromJson(response.toString(), CommonResp.class);

        if (commonResp != null && commonResp.getResponse_code() == 1) {
            setBooked(activity);

            ToastManager.showToast(activity, "预约成功");
        }
    }

    /**
     * 处理获取公开课连接回调
     * @param activity WebViewActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseUrlResp(WebViewActivity activity, JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        OpenCourseUrlResp openCourseUrlResp =
                gson.fromJson(response.toString(), OpenCourseUrlResp.class);

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
        Gson gson = GsonManager.initGson();
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        if (globalSetting == null) return;

        GlobalSettingsResp globalSettingsResp = gson.fromJson(
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

    /**
     * 处理轮询回调
     * @param activity WebViewActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseConsultResp(final WebViewActivity activity,
                                                 JSONObject response) {
        if (response == null || activity.mHasShowOpenCourseConsult) return;

        final Gson gson = GsonManager.initGson();
        OpenCourseConsultResp openCourseConsultResp =
                gson.fromJson(response.toString(), OpenCourseConsultResp.class);

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
                    // 获取营销QQ
                    setMarketQQ(activity);
                }
            });
        }
    }

    /**
     * 设置营销QQ
     * @param activity Activity
     */
    public static void setMarketQQ(Activity activity) {
        GlobalSetting globalSetting = GlobalSettingDAO.findById();
        if (globalSetting == null) return;

        Gson gson = GsonManager.initGson();
        GlobalSettingsResp globalSettingsResp =
                gson.fromJson(globalSetting.content, GlobalSettingsResp.class);

        if (globalSettingsResp == null || globalSettingsResp.getResponse_code() != 1)
            return;

        String qq = globalSettingsResp.getMarket_qq();
        String url="mqqwpa://im/chat?chat_type=wpa&uin=" + qq;

        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            ToastManager.showToast(activity, "您未安装手机QQ，请到应用市场下载……");
        }
    }
}
