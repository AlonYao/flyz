package com.appublisher.quizbank.model;

import android.widget.TextView;

import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.utils.GsonManager;
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

        textView.setText("距离"+ name + "还有" + String.valueOf(day) + "天");
    }
}
