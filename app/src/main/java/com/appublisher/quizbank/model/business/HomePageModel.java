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
import com.appublisher.lib_basic.AppDownload;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_course.opencourse.activity.OpenCourseActivity;
import com.appublisher.lib_course.opencourse.netdata.OpenCourseStatusResp;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_login.model.db.User;
import com.appublisher.lib_login.model.db.UserDAO;
import com.appublisher.lib_login.model.netdata.UserExamInfoModel;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.course.GradeCourseResp;
import com.appublisher.quizbank.model.netdata.course.PromoteLiveCourseResp;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;
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
    public static void setExamCountDown(TextView textView, QRequest QRequest) {
        User user = UserDAO.findById();

        if (user == null) return;


        ExamItemModel examItemModel = GsonManager.getModel(user.exam, ExamItemModel.class);

        if (examItemModel == null) return;

        String name = examItemModel.getName();
        String date = examItemModel.getDate();

        long day = Utils.dateMinusNow(date);
        if (day < 0) {
            day = 0;
            QRequest.getExamList();
        }

        String text = "距离" + name + "还有" + String.valueOf(day) + "天";
        textView.setText(text);
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
    public static void setPromoteLiveCourse(final Activity activity,
                                            View view,
                                            final PromoteLiveCourseResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        LinearLayout llPromote = (LinearLayout) view.findViewById(R.id.course_promote);
        TextView tvPromote = (TextView) view.findViewById(R.id.course_promote_text);
        final ImageView ivPromote = (ImageView) view.findViewById(R.id.course_promote_img);

        String displayType = resp.getDisplay_type();
        String displayContent = resp.getDisplay_content() == null ? "" : resp.getDisplay_content();

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

            new QRequest(activity).loadImage(displayContent, imageListener);

        } else if ("text".equals(displayType)) {
            llPromote.setVisibility(View.VISIBLE);
            tvPromote.setVisibility(View.VISIBLE);

            String targetContent = resp.getTarget_content();
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
        llPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.course_promote:
                        if (resp.getResponse_code() != 1) return;

                        String targetType = resp.getTarget_type();
                        String targetContent = resp.getTarget_content();

                        if (targetContent == null || targetContent.length() == 0) return;

                        if ("url".equals(targetType)) {
                            Intent intent = new Intent(mActivity, WebViewActivity.class);
                            intent.putExtra("url", LoginParamBuilder.finalUrl(targetContent));
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
                                intent.putExtra("url", LoginParamBuilder.finalUrl(
                                        targetContent.replace("zhiboke@", "")));
                                intent.putExtra("bar_title", "快讯");
                                intent.putExtra("from", "course");
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
        });
    }

    /**
     * 处理快讯模块数据回调
     * @param response 快讯数据
     * @param homePageFragment 首页
     */
    public static void dealPromoteResp(JSONObject response, HomePageFragment homePageFragment) {
        if (response == null) return;
        PromoteLiveCourseResp resp =
                GsonManager.getModel(response.toString(), PromoteLiveCourseResp.class);

        setPromoteLiveCourse(homePageFragment.mActivity, homePageFragment.mView, resp);
    }

//    /*
//    获取mock_id
//     */
//    public static void dealMockListResp(JSONObject jsonObject, HomePageFragment homePageFragment) {
//        MockListResp mockListResp = GsonManager.getObejctFromJSON(jsonObject.toString(), MockListResp.class);
//        ArrayList<MockPaperM> mockPaperMs = mockListResp.getPaper_list();
//        if(mockPaperMs != null && mockPaperMs.size() != 0){
//            MockPaperM mockPaperM = mockPaperMs.get(0);
//            homePageFragment.mock_id = mockPaperM.getId();
//        }
//    }

    /**
     * 开通课程
     * @param homePageFragment 首页
     */
    public static void openupCourse(HomePageFragment homePageFragment) {
        if (Globals.rateCourseResp == null || Globals.rateCourseResp.getResponse_code() != 1)
            return;
        ProgressDialogManager.showProgressDialog(homePageFragment.mActivity, false);
        homePageFragment.mQRequest.getRateCourse(ParamBuilder.getRateCourse(
                "enroll", String.valueOf(Globals.rateCourseResp.getCourse_id())));
    }

    /**
     * 处理开通评价课程回调
     * @param response 回调数据
     * @param homePageFragment 首页
     */
    public static void dealOpenupCourseResp(JSONObject response,
                                            HomePageFragment homePageFragment) {

        GradeCourseResp gradeCourseResp =
               GsonManager.getModel(response.toString(), GradeCourseResp.class);

        if (gradeCourseResp == null || gradeCourseResp.getResponse_code() != 1) return;

        AlertManager.showGradeSuccessAlert(
                homePageFragment.mActivity, gradeCourseResp.getJump_url());
        GradeDAO.setGrade(Globals.appVersion);
    }

    /**
     * 更新考试时间
     * @param examDetailModel 考试列表数据模型
     * @param textView 考试项目控件
     */
    public static void updateExam(ExamDetailModel examDetailModel, TextView textView) {
        if (examDetailModel == null || examDetailModel.getResponse_code() != 1) return;

        ArrayList<ExamItemModel> list = examDetailModel.getExams();
        if (list == null) return;

        int userExamId = LoginModel.getExamInfo().getExam_id();
        if (userExamId == 0) return;

        for (ExamItemModel examItemModel : list) {
            if (examItemModel == null) continue;
            if (examItemModel.getExam_id() == userExamId) {
                // 更新用户考试项目
                UserExamInfoModel curUserExam = LoginModel.getExamInfo();
                if (curUserExam == null) return;

                String name = examItemModel.getName();
                String date = examItemModel.getDate();

                curUserExam.setName(name);
                curUserExam.setDate(date);

                // 更新数据库
                LoginModel.updateExamInfo(
                        GsonManager.modelToString(curUserExam, UserExamInfoModel.class));

                // 更新页面显示
                long day = Utils.dateMinusNow(date);
                if (day < 0) day = 0;
                String text = "距离" + name + "还有" + String.valueOf(day) + "天";
                textView.setText(text);
            }
        }
    }

    /**
     * 更新考试项目
     * @param model 考试项目数据
     * @param fragment HomePageFragment
     */
    public static void updateExam(ExamItemModel model, HomePageFragment fragment) {
        if (model == null) return;
        LoginModel.updateExamInfo(GsonManager.modelToString(model, ExamItemModel.class));
        setExamCountDown(fragment.mTvExam, fragment.mQRequest);
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
                Intent intent = new Intent(activity, OpenCourseActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
