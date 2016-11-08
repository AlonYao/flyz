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
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.fragment.StudyIndexFragment;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.netdata.course.GradeCourseResp;
import com.appublisher.quizbank.model.netdata.course.PromoteLiveCourseResp;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.mock.GufenM;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * HomePageFragment Model
 */
public class StudyIndexModel {

    /**
     * 设置考试项目倒计时
     *
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

        String text = "距离" + name + "\n还有" + String.valueOf(day) + "天";
        textView.setText(text);
    }


    /**
     * 开通课程
     *
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
     *
     * @param response         回调数据
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
     *
     * @param examDetailModel 考试列表数据模型
     * @param textView        考试项目控件
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
                String text = "距离" + name + "\n" + "还有" + String.valueOf(day) + "天";
                textView.setText(text);
            }
        }
    }

    /**
     * 更新考试项目
     *
     * @param model    考试项目数据
     * @param fragment HomePageFragment
     */
    public static void updateExam(ExamItemModel model, StudyIndexFragment fragment) {
        if (model == null) return;
        LoginModel.updateExamInfo(GsonManager.modelToString(model, ExamItemModel.class));
        setExamCountDown(fragment.examNameTv, fragment.mQRequest);
    }

    /**
     * 模考估分回调
     *
     * @param response
     * @param fragment
     */
    public static void dealMockGufenResp(JSONObject response, StudyIndexFragment fragment) {
        final MockGufenResp mockGufenResp = GsonManager.getModel(response, MockGufenResp.class);

        if (mockGufenResp.getResponse_code() == 1) {

            fragment.mockGufenResp = mockGufenResp;

            if (mockGufenResp.getMock() != null) {
                MockGufenResp.MockBean mockBean = mockGufenResp.getMock();
                fragment.mockNameTv.setText(mockBean.getName());
                fragment.mockView.setVisibility(View.VISIBLE);
                fragment.mock_id = mockBean.getId();
            }

            if (mockGufenResp.getGufen() != null) {
                GufenM gufenBean = mockGufenResp.getGufen();
                fragment.assessNameTv.setText(gufenBean.getName());
                fragment.assessView.setVisibility(View.VISIBLE);
            }
        }
    }

}
