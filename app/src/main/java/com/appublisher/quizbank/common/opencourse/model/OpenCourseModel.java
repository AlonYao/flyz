package com.appublisher.quizbank.common.opencourse.model;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseNoneActivity;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.common.opencourse.adapter.ListOpencourseAdapter;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseConsultResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseListItem;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseListResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseStatusResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUrlResp;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * OpenCourse Model
 */
public class OpenCourseModel {

    private static final int SECTION = 3;
    private static List<OpenCourseListItem> mShowList;

//    /**
//     * 处理公开课详情回调
//     * @param activity OpenCourseUnstartActivity
//     * @param response 回调数据
//     */
//    public static void dealOpenCourseDetailResp(final OpenCourseUnstartActivity activity,
//                                                JSONObject response) {
//        if (response == null) return;
//
//        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
//        final OpenCourseDetailResp openCourseDetailResp =
//                Globals.gson.fromJson(response.toString(), OpenCourseDetailResp.class);
//
//        if (openCourseDetailResp == null || openCourseDetailResp.getResponse_code() != 1) return;
//
//        OpenCourseM openCourse = openCourseDetailResp.getCourse();
//
//        if (openCourse == null) return;
//
//        // 公开课图片
//        activity.mRequest.loadImage(openCourse.getCover_pic(), activity.mIvOpenCourse);
//
//        // 公开课名字
//        activity.mTvName.setText("公开课：" + openCourse.getName());
//
//        // 公开课时间
//        String startTime = openCourse.getStart_time();
//        String endTime = openCourse.getEnd_time();
//
//        try {
//            if (startTime != null) startTime = startTime.substring(0, 16);
//            if (endTime != null) endTime = endTime.substring(11, 16);
//        } catch (Exception e) {
//            activity.mTvTime.setText("时间：" + openCourse.getStart_time()
//                    + " - " + openCourse.getEnd_time());
//        }
//
//        activity.mTvTime.setText("时间：" + startTime + " - " + endTime);
//
//        // 公开课讲师
//        activity.mTvLector.setText("主讲：" + openCourse.getLector());
//
//        // 预约状态
//        boolean booked = openCourseDetailResp.isBooked();
//        if (booked) {
//            setBooked(activity);
//            // Umeng
//            activity.mUmengPreSit = "3";
//
//        } else {
//            activity.mTvNotice.setText(R.string.opencourse_notice_false);
//            activity.mTvNotice.setTextColor(
//                    activity.getResources().getColor(R.color.white));
//            activity.mTvNotice.setBackgroundColor(
//                    activity.getResources().getColor(R.color.answer_sheet_btn));
//
//            activity.mTvNotice.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 判断用户是否有手机号
//                    String mobileNum = LoginModel.getUserMobile();
//                    if (mobileNum == null || mobileNum.length() == 0) {
//                        // 没有手机号
//                        Intent intent = new Intent(activity, BindingMobileActivity.class);
//                        intent.putExtra("from", "book_opencourse");
//                        activity.startActivityForResult(intent,
//                                ActivitySkipConstants.BOOK_OPENCOURSE);
//                    } else {
//                        // 有手机号
//                        AlertManager.bookOpenCourseAlert(activity, mobileNum, activity.mContent);
//                    }
//
//                    // Umeng
//                    activity.mUmengPreSit = "2";
//                }
//            });
//        }
//
//        // 往期内容
//        final ArrayList<StaticCourseM> staticCourses = openCourseDetailResp.getStaticCourses();
//
//        if (staticCourses != null && staticCourses.size() != 0) {
//            activity.mLlOldtimey.setVisibility(View.VISIBLE);
//            OldtimeyListAdapter oldtimeyListAdapter =
//                    new OldtimeyListAdapter(activity, openCourseDetailResp.getStaticCourses());
//            activity.mLvOldtimey.setAdapter(oldtimeyListAdapter);
//
//            activity.mLvOldtimey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (position >= staticCourses.size()) return;
//
//                    activity.mCurOldtimeyPosition = position;
//
//                    String mobileNum = LoginModel.getUserMobile();
//                    if (mobileNum == null || mobileNum.length() == 0) {
//                        // 没有手机号
//                        Intent intent = new Intent(activity, BindingMobileActivity.class);
//                        intent.putExtra("from", "opencourse_pre");
//                        activity.startActivityForResult(
//                                intent, ActivitySkipConstants.OPENCOURSE_PRE);
//
//                    } else {
//                        // 跳转
//                        StaticCourseM staticCourse = staticCourses.get(position);
//                        String url = staticCourse.getCourse_url()
//                                + "&user_id=" + LoginModel.getUserId()
//                                + "&user_token=" + LoginModel.getUserToken();
//                        OpenCourseModel.skipToPreOpenCourse(activity, url, staticCourse.getName());
//                    }
//
//                    // Umeng
//                    activity.mUmengVideoPlay = "1";
//                }
//            });
//
//        } else {
//            activity.mLlOldtimey.setVisibility(View.GONE);
//        }
//    }

    /**
     * 跳转到查看往期页面
     * @param activity Activity
     * @param url url
     */
    public static void skipToPreOpenCourse(Activity activity, String url, String name) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("from", "opencourse_pre");
        intent.putExtra("url", url);
        intent.putExtra("bar_title", name);
        intent.putExtra("umeng_entry", "Home");
        activity.startActivity(intent);
    }

    /**
     * 设置已预约状态
     * @param activity OpenCourseUnstartActivity
     */
    public static void setBooked(OpenCourseUnstartActivity activity) {
//        activity.mTvNotice.setText(R.string.opencourse_notice_true);
//        activity.mTvNotice.setTextColor(
//                activity.getResources().getColor(R.color.common_text));
//        activity.mTvNotice.setBackgroundColor(
//                activity.getResources().getColor(R.color.transparency));
//
//        activity.mTvNotice.setOnClickListener(null);
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

            // Umeng
            activity.mUmengPreSit = "3";
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
                    // Umeng
                    activity.mUmengQQ = "2";

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
            if (activity instanceof WebViewActivity) ((WebViewActivity) activity).mIsFromQQ = true;
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            ToastManager.showToast(activity, "您未安装手机QQ，请到应用市场下载……");
        }
    }

    /**
     * 处理公开课状态回调
     * @param response 回调数据
     */
    public static void dealOpenCourseStatusResp(JSONObject response) {
        OpenCourseStatusResp openCourseStatusResp =
                GsonManager.getModel(response, OpenCourseStatusResp.class);
        if (openCourseStatusResp == null || openCourseStatusResp.getResponse_code() != 1) return;

        Globals.openCourseStatus = openCourseStatusResp;
    }

    /**
     * 设置公开课按钮
     * @param activity Activity
     * @param textView 公开课按钮控件
     */
    public static void setOpenCourseBtn(final Activity activity, TextView textView) {
        // 更新按钮文字
        String head = "免费公开课";
        if (Globals.openCourseStatus != null && Globals.openCourseStatus.getType() != 0) {
            String courseName = Globals.openCourseStatus.getCourse_name() == null
                    ? ""
                    : Globals.openCourseStatus.getCourse_name();

            if (Globals.openCourseStatus.getType() == 1) {
                head = "正在手机直播：\n" + courseName;
            } else if (Globals.openCourseStatus.getType() == 2) {
                head = "即将手机直播：\n" + courseName;
            }
        }
        textView.setText(head);

        // 跳转
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                skipToOpenCoursePage(activity, "Home");
                Intent intent = new Intent(activity, OpenCourseUnstartActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 跳转至公开课相关页面
     * @param activity Activity
     */
    public static void skipToOpenCoursePage(Activity activity, String umengEntry) {
        if (Globals.openCourseStatus == null) return;

        Class<?> cls = getOpenCourseClass(Globals.openCourseStatus.getType());

        if (cls == null) return;

        Intent intent = new Intent(activity, cls);

        if (Globals.openCourseStatus.getType() == 1) {
            intent.putExtra("from", "opencourse_started");
            intent.putExtra("bar_title", Globals.openCourseStatus.getCourse_name());
        }

        intent.putExtra("content", Globals.openCourseStatus.getContent());
        intent.putExtra("umeng_entry", umengEntry);
        activity.startActivity(intent);
    }

    /**
     * 获取公开课跳转Class
     * @param type 公开课状态
     * @return Class
     */
    public static Class<?> getOpenCourseClass(int type) {
        switch (type) {
            case 0:
                // 没有公开课
                return OpenCourseNoneActivity.class;

            case 1:
                // 正在上课
                String mobile = LoginModel.getUserMobile();

                if (mobile == null || mobile.length() == 0) {
                    // 没有手机号
                    return BindingMobileActivity.class;
                } else {
                    // 有手机号
                    return WebViewActivity.class;
                }

            case 2:
                // 即将上课
                return OpenCourseUnstartActivity.class;
        }

        return null;
    }

    /**
     * 处理公开课列表回调
     * @param resp 公开课列表数据模型
     * @param activity OpenCourseUnstartActivity
     */
    public static void dealOpenCourseListResp(OpenCourseListResp resp,
                                              final OpenCourseUnstartActivity activity) {
        if (resp == null || resp.getResponse_code() != 1) return;

        final ArrayList<OpenCourseListItem> courses = resp.getCourses();
        if (courses == null) return;

        mShowList = new ArrayList<>();

        if (courses.size() > SECTION) {
            mShowList = courses.subList(0, SECTION);
        } else {
            mShowList = courses;
        }

        final ListOpencourseAdapter adapter = new ListOpencourseAdapter(activity, mShowList);
        activity.mLvOpencourse.setAdapter(adapter);

        // 加载更多
        activity.mTvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int all = courses.size();
                int cur = mShowList.size();

                if (cur < all) {
                    // 如果有未显示的公开课
                    mShowList.clear();
                    if ((all - cur) > SECTION) {
                        // 如果未显示的公开课，超过区间长度，则再显示下一个区间
                        mShowList.addAll(courses.subList(0, cur + SECTION));
                    } else {
                        mShowList.addAll(courses);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    ToastManager.showToast(activity, "暂无更多公开课");
                }
            }
        });
    }
}
