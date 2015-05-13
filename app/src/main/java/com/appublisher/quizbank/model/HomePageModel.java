package com.appublisher.quizbank.model;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.appublisher.quizbank.activity.OpenCourseNoneActivity;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.fragment.HomePageFragment;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.RegisterActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.homepage.LiveCourseM;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;

/**
 * HomePageFragment Model
 */
public class HomePageModel {

    private static Class<?> mCls;

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

        textView.setText("距离"+ name + "还有" + String.valueOf(day) + "天");
    }

    /**
     * 设置公开课
     * @param fragment HomePageFragment
     * @param liveCourse 公开课数据
     */
    public static void setOpenCourse(final HomePageFragment fragment, LiveCourseM liveCourse) {
        if (liveCourse == null) return;

        int type = liveCourse.getType();
        final String content = liveCourse.getContent();

        mCls = null;

        switch (type) {
            case 0:
                // 没有公开课
                mCls = OpenCourseNoneActivity.class;
                break;

            case 1:
                // 正在上课
                UserInfoModel userInfo = LoginModel.getUserInfoM();

                if (userInfo == null) return;

                if (userInfo.getMobile_num() == null || userInfo.getMobile_num().length() == 0) {
                    // 没有手机号
                    mCls = RegisterActivity.class;
                } else {
                    // 有手机号
                    mCls = WebViewActivity.class;
                }

                break;

            case 2:
                // 即将上课
                mCls = OpenCourseUnstartActivity.class;

                break;
        }

        fragment.mTvZhiboke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCls == null) return;

                Intent intent =
                        new Intent(fragment.mActivity, mCls);
                intent.putExtra("content", content);
                intent.putExtra("from", "opencourse_started");
                fragment.mActivity.startActivity(intent);
            }
        });
    }
}
