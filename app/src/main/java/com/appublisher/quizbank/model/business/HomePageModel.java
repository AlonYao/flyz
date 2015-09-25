package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.netdata.course.GradeCourseResp;
import com.appublisher.quizbank.model.netdata.course.PromoteLiveCourseResp;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.mock.MockListResp;
import com.appublisher.quizbank.model.netdata.mock.MockPaperM;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.AppDownload;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * HomePageFragment Model
 */
public class HomePageModel {

    private static Activity mActivity;

    /**
     * 设置考试项目倒计时
     * @param textView textView
     */
    public static void setExamCountDown(TextView textView) {
        User user = UserDAO.findById();

        if (user == null) return;

        Gson gson = GsonManager.initGson();
        ExamItemModel examItemModel = gson.fromJson(user.exam, ExamItemModel.class);

        if (examItemModel == null) return;

        String name = examItemModel.getName();
        String date = examItemModel.getDate();

        long day = Utils.dateMinusNow(date);

        textView.setText("距离" + name + "还有" + String.valueOf(day) + "天");
    }

    /**
     * 设置侧边栏红点
     */
    public static void setDrawerRedPoint() {
        if (MainActivity.mIvDrawerRedPoint == null || Globals.last_notice_id == 0) return;

        GlobalSetting globalSetting = GlobalSettingDAO.findById();
        if (globalSetting != null && globalSetting.latest_notify == Globals.last_notice_id) {
            MainActivity.mIvDrawerRedPoint.setVisibility(View.GONE);
        } else {
            MainActivity.mIvDrawerRedPoint.setVisibility(View.VISIBLE);
        }

        if (MainActivity.mDrawerAdapter != null) MainActivity.mDrawerAdapter.notifyDataSetChanged();
    }

    /**
     * 获取侧边栏设置Button View
     * @return view
     */
    public static ImageView getSettingRedPointView() {
        if (MainActivity.mDrawerList == null) return null;

        View setting = CommonModel.getViewByPosition(5, MainActivity.mDrawerList);
        return (ImageView) setting.findViewById(R.id.drawer_item_redpoint);
    }

    /**
     * 设置快讯模块
     * @param activity 上下文
     * @param view 控件
     */
    public static void setPromoteLiveCourse(final Activity activity, View view) {
        if (Globals.promoteLiveCourseResp == null
                || Globals.promoteLiveCourseResp.getResponse_code() != 1) return;

        LinearLayout llPromote = (LinearLayout) view.findViewById(R.id.course_promote);
        TextView tvPromote = (TextView) view.findViewById(R.id.course_promote_text);
        final ImageView ivPromote = (ImageView) view.findViewById(R.id.course_promote_img);

        String displayType = Globals.promoteLiveCourseResp.getDisplay_type();
        String displayContent = Globals.promoteLiveCourseResp.getDisplay_content() == null
                ? "" : Globals.promoteLiveCourseResp.getDisplay_content();

        // 设置内容
        if (displayType == null) {
            llPromote.setVisibility(View.GONE);
        } else if ("image".equals(displayType)) {
            llPromote.setVisibility(View.VISIBLE);
            ivPromote.setVisibility(View.VISIBLE);

            ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap data = imageContainer.getBitmap();

                    if (data == null) return;

                    // 对小于指定尺寸的图片进行放大(2倍)
                    int width = data.getWidth();
                    int height = data.getHeight();
                    int newWidth = Utils.dip2px(activity, 300);
                    if (width < newWidth) {
                        float m = (float) newWidth / width;
                        Matrix matrix = new Matrix();
                        matrix.postScale(m, m);
                        data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                    }

                    ivPromote.setImageBitmap(data);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            };

            new Request(activity).loadImage(displayContent, imageListener);

        } else if ("text".equals(displayType)) {
            llPromote.setVisibility(View.VISIBLE);
            tvPromote.setVisibility(View.VISIBLE);

            String targetContent = Globals.promoteLiveCourseResp.getTarget_content();
            if (targetContent == null || targetContent.length() == 0) {
                // 没有跳转信息
                tvPromote.setText(displayContent);

            } else {
                // 有跳转信息
                SpannableStringBuilder text = new SpannableStringBuilder();
                displayContent = displayContent + "\n点击看详情";
                text.append(displayContent);
                int start = displayContent.lastIndexOf("点");
                int end = displayContent.length();
                text.setSpan(new URLSpan(displayContent), start, end, 0);
                text.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, 0);
                tvPromote.setText(text);
            }
        }

        // 设置跳转
        mActivity = activity;
        llPromote.setVisibility(View.VISIBLE);
        llPromote.setOnClickListener(onClickListener);
    }

    /**
     * 公告栏课程推广点击事件
     */
    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_promote:
                    if (Globals.promoteLiveCourseResp == null
                            || Globals.promoteLiveCourseResp.getResponse_code() != 1) return;

                    String targetType = Globals.promoteLiveCourseResp.getTarget_type();
                    String targetContent = Globals.promoteLiveCourseResp.getTarget_content();

                    if (targetContent == null || targetContent.length() == 0) return;

                    if ("url".equals(targetType)) {
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("url", targetContent);
                        mActivity.startActivity(intent);

                    } else if ("app".equals(targetType)) {
                        if (targetContent.contains("market@")) {
                            // 跳转到市场
                            CommonModel.skipToGrade(
                                    mActivity, targetContent.replace("market@", ""));

                        } else if (targetContent.contains("courselist")) {
                            // 跳转到课程中心模块
                            if (mActivity instanceof MainActivity)
                                ((MainActivity) mActivity).changeFragment(2);

                        } else if (targetContent.contains("zhiboke@")) {
                            // 跳转至课程详情页面
                            Intent intent = new Intent(mActivity, WebViewActivity.class);
                            intent.putExtra("url", targetContent.replace("zhiboke@", "")
                                    + "&user_id=" + LoginModel.getUserId()
                                    + "&user_token=" + LoginModel.getUserToken());
                            mActivity.startActivity(intent);

                            // Umeng统计
                            try {
                                String courseId =
                                        targetContent.substring(
                                                targetContent.indexOf("course_id=") + 10,
                                                targetContent.indexOf("&user_id="));
                                HashMap<String, String> map = new HashMap<>();
                                map.put("CourseID", courseId);
                                map.put("Entry", "KX");
                                map.put("Status", "");
                                MobclickAgent.onEvent(mActivity, "EnterCourse", map);
                            } catch (Exception e) {
                                // Empty
                            }
                        }

                    } else if ("apk".equals(targetType)) {
                        AppDownload.downloadApk(mActivity, targetContent);
                    }

                    break;
            }
        }
    };

    /**
     * 处理快讯模块数据回调
     * @param response 快讯数据
     * @param homePageFragment 首页
     */
    public static void dealPromoteResp(JSONObject response, HomePageFragment homePageFragment) {
        if (response == null) return;
        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        Globals.promoteLiveCourseResp =
                Globals.gson.fromJson(response.toString(), PromoteLiveCourseResp.class);

        setPromoteLiveCourse(homePageFragment.mActivity, homePageFragment.mView);
    }
    /*
    获取mock_id
     */
    public static void dealMockListResp(JSONObject jsonObject, HomePageFragment homePageFragment) {
        MockListResp mockListResp = GsonManager.getObejctFromJSON(jsonObject.toString(), MockListResp.class);
        ArrayList<MockPaperM> mockPaperMs = mockListResp.getPaper_list();
        if(mockPaperMs != null && mockPaperMs.size() != 0){
            MockPaperM mockPaperM = mockPaperMs.get(0);
            homePageFragment.mock_id = mockPaperM.getId();
        }
    }
    /**
     * 开通课程
     * @param homePageFragment 首页
     */
    public static void openupCourse(HomePageFragment homePageFragment) {
        if (Globals.rateCourseResp == null || Globals.rateCourseResp.getResponse_code() != 1)
            return;
        ProgressDialogManager.showProgressDialog(homePageFragment.mActivity, false);
        homePageFragment.mRequest.getRateCourse(ParamBuilder.getRateCourse(
                "enroll", String.valueOf(Globals.rateCourseResp.getCourse_id())));
    }

    /**
     * 处理开通评价课程回调
     * @param response 回调数据
     * @param homePageFragment 首页
     */
    public static void dealOpenupCourseResp(JSONObject response,
                                            HomePageFragment homePageFragment) {
        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        GradeCourseResp gradeCourseResp =
                Globals.gson.fromJson(response.toString(), GradeCourseResp.class);

        if (gradeCourseResp == null || gradeCourseResp.getResponse_code() != 1) return;

        AlertManager.showGradeSuccessAlert(
                homePageFragment.mActivity, gradeCourseResp.getJump_url());
        GradeDAO.setGrade(Globals.appVersion);
    }
}
