package com.appublisher.quizbank.common.opencourse.model;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.common.login.activity.BindingMobileActivity;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseGradeActivity;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseMyGradeActivity;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseNoneActivity;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.common.opencourse.adapter.GridOpencourseGradeAdapter;
import com.appublisher.quizbank.common.opencourse.adapter.ListMyGradeAdapter;
import com.appublisher.quizbank.common.opencourse.adapter.ListOpencourseAdapter;
import com.appublisher.quizbank.common.opencourse.adapter.ListOthersRateAdapter;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseConsultResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseListItem;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseListResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCoursePlaybackItem;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateListResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateTagItem;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseStatusResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUrlResp;
import com.appublisher.quizbank.common.opencourse.netdata.RateListSelfItem;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * OpenCourse Model
 */
public class OpenCourseModel {

    private static final int SECTION = 3;
    private static List<OpenCourseListItem> mShowList;
    private static EditText mEditText;
    private static TextView mTvCurNum;
    private static Dialog mRateDialog;
    private static ListOthersRateAdapter mOthersRateAdapter;
    private static ArrayList<HashMap<String, Object>> mTags;

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

        ArrayList<OpenCourseListItem> courses = resp.getCourses();
        ArrayList<OpenCoursePlaybackItem> playbacks = resp.getPlaybacks();

        // 公开课列表
        showOpenCourseList(courses, activity);

        // 回放列表
        showPlayBackList(playbacks, activity);
    }

    /**
     * 显示回放列表
     * @param playbacks 回放列表数据
     * @param activity OpenCourseUnstartActivity
     */
    private static void showPlayBackList(ArrayList<OpenCoursePlaybackItem> playbacks,
                                         final OpenCourseUnstartActivity activity) {
        if (playbacks == null) return;

        activity.mTvPlayback.setVisibility(View.VISIBLE);
        activity.mLlPlayback.removeAllViews();

        int size = playbacks.size();
        for (int i = 0; i < size; i++) {
            final OpenCoursePlaybackItem playback = playbacks.get(i);
            if (playback == null) continue;

            View child = LayoutInflater.from(activity).inflate(
                    R.layout.item_playback, activity.mLlPlayback, false);

            TextView tvDesc = (TextView) child.findViewById(R.id.playback_desc);
            String desc = playback.getLector() + "-" + playback.getName();
            tvDesc.setText(desc);

            TextView tvPerson = (TextView) child.findViewById(R.id.playback_person_tv);
            tvPerson.setText(String.valueOf(playback.getPersons_num()));

            TextView tvGrade = (TextView) child.findViewById(R.id.playback_grade_tv);
            tvGrade.setText(String.valueOf(playback.getRate_num()));

            RatingBar rb = (RatingBar) child.findViewById(R.id.playback_ratingbar);
            rb.setRating(playback.getScore());

            RelativeLayout rlGrade = (RelativeLayout) child.findViewById(R.id.playback_grade);
            rlGrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, OpenCourseGradeActivity.class);
                    intent.putExtra("course_id", playback.getCourse_id());
                    intent.putExtra("class_id", playback.getClass_id());
                    intent.putExtra("bar_title", playback.getName());
                    intent.putExtra("url", playback.getUrl());
                    intent.putExtra("entry", "opencourse");
                    activity.startActivity(intent);
                }
            });

            activity.mLlPlayback.addView(child);
        }
    }

    /**
     * 显示公开课列表
     * @param courses 公开课列表
     * @param activity OpenCourseUnstartActivity
     */
    private static void showOpenCourseList(final ArrayList<OpenCourseListItem> courses,
                                           final OpenCourseUnstartActivity activity) {
        if (courses == null) return;

        mShowList = new ArrayList<>();

        if (courses.size() > SECTION) {
            mShowList = courses.subList(0, SECTION);
        } else {
            mShowList = courses;
        }

        ListOpencourseAdapter adapter = new ListOpencourseAdapter(activity, mShowList);
        activity.mLvOpencourse.setAdapter(adapter);
        activity.mLvOpencourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mShowList.size()) return;

                OpenCourseListItem item = mShowList.get(position);
                if (item == null) return;

                if (item.is_onair()) {
                    // 正在直播，验证手机号
                    skipToOpenCoursePage(activity, "Home");
                } else {
                    // 即将开始
                    ToastManager.showToast(activity, "直播时间还没到哦");
                }
            }
        });

        // 加载更多
        activity.mTvMore.setVisibility(View.VISIBLE);
        activity.mTvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int all = courses.size();
                int cur = mShowList.size();

                if (cur < all) {
                    // 如果有未显示的公开课
                    if ((all - cur) > SECTION) {
                        // 如果未显示的公开课，超过区间长度，则再显示下一个区间
                        mShowList = courses.subList(0, cur + SECTION);
                    } else {
                        mShowList = courses;
                    }
                    ListOpencourseAdapter adapter = new ListOpencourseAdapter(activity, mShowList);
                    activity.mLvOpencourse.setAdapter(adapter);

                } else {
                    ToastManager.showToast(activity, "暂无更多公开课");
                }
            }
        });
    }

    /**
     * 评价Alert
     * @param context Context
     * @param entity OpenCourseRateEntity
     * @param request OpenCourseRequest
     */
    public static void showGradeAlert(final Context context,
                                      final OpenCourseRateEntity entity,
                                      final OpenCourseRequest request) {
        if (entity == null) return;

        mRateDialog = new Dialog(context);
        mRateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRateDialog.show();

        Window window = mRateDialog.getWindow();
        window.setContentView(R.layout.alert_opencourse_grade);
        window.setBackgroundDrawableResource(R.color.transparency);

        // 描述
        TextView tvDesc = (TextView) window.findViewById(R.id.alert_opencourse_grade_desc);
        tvDesc.setText(entity.desc);

        // 评语标签
        final GridView gridView = (GridView) window.findViewById(R.id.alert_opencourse_grade_gv);
        showTagsByRating(5, gridView, context);

        // 星星
        final RatingBar ratingBar = (RatingBar) window.findViewById(R.id.alert_opencourse_grade_rb);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor("#FFD000"));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }

                showTagsByRating((int) rating, gridView, context);
            }
        });

        // 评价文本框
        mTvCurNum = (TextView) window.findViewById(R.id.alert_opencourse_grade_num);
        String text = "0/140";
        mTvCurNum.setText(text);

        mEditText = (EditText) window.findViewById(R.id.alert_opencourse_grade_edt);
        mEditText.addTextChangedListener(mTextWatcher);

        // 提交
        Button btnSubmit = (Button) window.findViewById(R.id.alert_opencourse_grade_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entity.score = (int) ratingBar.getRating();

                String edit = mEditText.getText().toString();
                String tag = "";
                if (mTags != null) {
                    for (HashMap<String, Object> mTag : mTags) {
                        String item = (String) mTag.get("comment");
                        if (item == null || item.length() == 0) continue;
                        tag = tag + item + "，";
                    }
                }

                entity.comment = tag + edit;

                ProgressDialogManager.showProgressDialog(context);
                request.rateClass(entity);
            }
        });
    }

    /**
     * 通过评分显示标签
     * @param rating 评分
     * @param gridView GridView
     * @param context Context
     */
    private static void showTagsByRating(int rating, GridView gridView, final Context context) {
        GlobalSettingsResp resp = GlobalSettingDAO.getGlobalSettingsResp();
        if (resp == null || resp.getResponse_code() != 1) return;

        ArrayList<OpenCourseRateTagItem> rateTagItems = resp.getRate_tags();
        if (rateTagItems == null) return;

        ArrayList<String> tags = new ArrayList<>();
        for (OpenCourseRateTagItem rateTagItem : rateTagItems) {
            if (rateTagItem == null) continue;

            if (rating == rateTagItem.getStar()) {
                tags = rateTagItem.getTags();
                break;
            }
        }

        GridOpencourseGradeAdapter adapter = new GridOpencourseGradeAdapter(context, tags);
        gridView.setAdapter(adapter);
        mTags = initTags(tags);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mTags.size()) return;

                HashMap<String, Object> map = mTags.get(position);
                boolean is_selected = (boolean) map.get("is_selected");
                if (is_selected) {
                    setUnSelected(context, view);
                    map.put("is_selected", false);
                } else {
                    setSelected(view);
                    map.put("is_selected", true);
                }
                mTags.set(position, map);
            }
        });
    }

    /**
     * 初始化标签列表
     * @param tags 标签列表
     */
    private static ArrayList<HashMap<String, Object>> initTags(ArrayList<String> tags) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        if (tags == null || tags.size() == 0) return list;

        for (String tag : tags) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("comment", tag);
            map.put("is_selected", false);
            list.add(map);
        }

        return list;
    }

    /**
     * 设置未选中状态
     * @param context Context
     */
    private static void setUnSelected(Context context, View view) {
        if (view == null) return;
        view.setBackgroundResource(R.drawable.alert_grade_unpress);
        TextView textView = (TextView) view.findViewById(R.id.item_alert_tv);
        textView.setTextColor(context.getResources().getColor(R.color.apptheme));
    }

    /**
     * 设置选中状态
     */
    private static void setSelected(View view) {
        if (view == null) return;
        view.setBackgroundResource(R.drawable.alert_grade_press);
        TextView textView = (TextView) view.findViewById(R.id.item_alert_tv);
        textView.setTextColor(Color.WHITE);
    }

    /**
     * TextWatcher
     */
    private static TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // Empty
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = String.valueOf(temp.length()) + "/100";
            mTvCurNum.setText(text);
        }
    };

    /**
     * 处理未评价课堂列表
     * @param activity OpenCourseUnstartActivity
     * @param resp 接口数据
     */
    public static void dealUnrateClassResp(OpenCourseUnstartActivity activity,
                                           OpenCourseUnrateClassResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        activity.mUnrateClasses = resp.getList();
        if (activity.mUnrateClasses == null || activity.mUnrateClasses.size() == 0) {
            activity.mTvNumNotice.setVisibility(View.GONE);
        } else {
            activity.mTvNumNotice.setVisibility(View.VISIBLE);
            activity.mTvNumNotice.setText(String.valueOf(activity.mUnrateClasses.size()));
        }
    }

    /**
     * 处理未评价课堂列表
     * @param activity OpenCourseMyGradeActivity
     * @param resp 接口数据
     */
    public static void dealUnrateClassResp(OpenCourseMyGradeActivity activity,
                                           OpenCourseUnrateClassResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        activity.mUnRateClasses = resp.getList();
        if (activity.mUnRateClasses == null || activity.mUnRateClasses.size() == 0) {
            activity.mListView.setVisibility(View.GONE);
        } else {
            activity.mListView.setVisibility(View.VISIBLE);
            activity.mAdapter = new ListMyGradeAdapter(activity, activity.mUnRateClasses);
            activity.mListView.setAdapter(activity.mAdapter);
        }
    }

    /**
     * 关闭评价Dialog
     */
    public static void closeRateDialog() {
        if (mRateDialog != null) mRateDialog.dismiss();
    }

    /**
     * 评价列表接口回调
     * @param resp OpenCourseRateListResp
     */
    public static void dealOpenCourseRateListResp(final OpenCourseGradeActivity activity,
                                                  OpenCourseRateListResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        // 我的评价
        RateListSelfItem self = resp.getSelf();
        if (self != null && self.getId() != 0) {
            activity.mLlMine.setVisibility(View.VISIBLE);
            activity.mRbMineRating.setRating(self.getScore());
            activity.mTvMineName.setText(LoginModel.getNickName());
            activity.mTvMineComment.setText(self.getComment());
            activity.mTvMineDate.setText(self.getRate_time());
            activity.mRequest.loadImage(self.getAvatar(), activity.mIvMineAvatar);

        } else {
            activity.mLlMine.setVisibility(View.GONE);
        }

        // 其他评价
        if (resp.getOthers() != null && resp.getOthers().size() != 0) {
            if (activity.mCurPage == 1) {
                activity.mOthers = resp.getOthers();
                mOthersRateAdapter = new ListOthersRateAdapter(activity, activity.mOthers);
                activity.mXlv.setAdapter(mOthersRateAdapter);

            } else {
                activity.mOthers.addAll(resp.getOthers());
                mOthersRateAdapter.notifyDataSetChanged();
            }
        } else {
            ToastManager.showToast(activity, "暂无更多评论");
        }

        // 评价按钮
        switch (resp.getStatus()) {
            case 0:
                // 未看过
                activity.mBtn.setVisibility(View.VISIBLE);
                activity.mBtn.setText(R.string.opencourse_listen);
                activity.mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mobileNum = LoginModel.getUserMobile();
                        if (mobileNum == null || mobileNum.length() == 0) {
                            // 没有手机号
                            Intent intent = new Intent(activity, BindingMobileActivity.class);
                            intent.putExtra("from", "opencourse");
                            activity.startActivity(intent);

                        } else {
                            // 跳转
                            String url = activity.mUrl
                                    + "&user_id=" + LoginModel.getUserId()
                                    + "&user_token=" + LoginModel.getUserToken();
                            OpenCourseModel.skipToPreOpenCourse(
                                    activity, url, activity.mCourseName);
                        }
                    }
                });
                break;

            case 1:
                // 未评价
                activity.mBtn.setVisibility(View.VISIBLE);
                activity.mBtn.setText(R.string.opencourse_grade);
                activity.mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCourseRateEntity entity = new OpenCourseRateEntity();
                        entity.class_id = activity.mClassId;
                        entity.course_id = activity.mCourseId;
                        entity.is_open = activity.mIsOpen;
                        entity.desc = activity.mCourseName;

                        showGradeAlert(activity, entity, activity.mRequest);
                    }
                });
                break;

            case 2:
                // 评价过
                activity.mBtn.setVisibility(View.GONE);
                break;
        }
    }
}
