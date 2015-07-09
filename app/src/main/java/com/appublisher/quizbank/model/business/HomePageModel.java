package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.OpenCourseNoneActivity;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.activity.RegisterActivity;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;

/**
 * HomePageFragment Model
 */
public class HomePageModel {

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
}
