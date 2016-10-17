package com.appublisher.quizbank.common.vip.model;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.common.vip.activity.VipIndexActivity;
import com.appublisher.quizbank.common.vip.fragment.VipIndexFragment;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;
import com.appublisher.quizbank.common.vip.netdata.VipIndexEntryDataResp;
import com.appublisher.quizbank.common.vip.netdata.VipNotificationResp;
import com.appublisher.quizbank.model.netdata.course.CourseListResp;
import com.appublisher.quizbank.model.netdata.course.CourseM;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by jinbao on 2016/8/30.
 */
public class VipIndexModel {

    /**
     * 处理获取后的数据
     *
     * @param response
     * @param activity
     */
    public static void dealEntryData(JSONObject response, VipIndexActivity activity) {
        VipIndexEntryDataResp vipIndexEntryDataResp = GsonManager.getModel(response, VipIndexEntryDataResp.class);
        if (vipIndexEntryDataResp.getResponse_code() == 1) {
            if (vipIndexEntryDataResp.isIs_vip_member()) {
                SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                editor.putBoolean("vip" + LoginModel.getUserId(), true);
                editor.commit();
            }

            int exerciseTips = vipIndexEntryDataResp.getExercises().getTotal();
            if (exerciseTips > 99)
                exerciseTips = 99;
            if (exerciseTips > 0) {
                activity.homeworkTipsText.setText(String.valueOf(exerciseTips));
                activity.homeworkTipsText.setVisibility(View.VISIBLE);
            }

            int messageTips = vipIndexEntryDataResp.getNotifications().getUnread_total();
            if (messageTips > 99)
                messageTips = 99;
            if (messageTips > 0) {
                activity.messageTips.setText(String.valueOf(messageTips));
                activity.messageTips.setVisibility(View.VISIBLE);
            }


            String classDate = vipIndexEntryDataResp.getClassroom().getStart_time();
            String exerciseDate = vipIndexEntryDataResp.getExercises().getEnd_time();

            if (classDate != null && !"".equals(classDate)) {
                long time = Utils.getSecondsByDateMinusNow(classDate) / (60 * 60);
                if (time >= 24) {
                    long day = time / 24;
                    activity.classTime.setText(day + "天后开课");
                } else if (time > 0) {
                    activity.classTime.setText(time + "小时后开课");
                } else {
                    time = (Utils.getSecondsByDateMinusNow(classDate) % (60 * 60)) / 60;
                    if (time > 1) {
                        activity.classTime.setText(time + "分钟后开课");
                    } else if (time > 0 && time < 1) {
                        activity.classTime.setText("1分钟后开课");
                    } else {
                        activity.classTime.setText("正在上课");
                    }

                }
            }

            if (exerciseDate != null && !"".equals(exerciseDate)) {
                long time = Utils.getSecondsByDateMinusNow(exerciseDate) / (60 * 60);
                if (time >= 24) {
                    long day = time / 24;
                    activity.homeworkTimeText.setText(day + "天后过期");
                } else if (time > 0) {
                    activity.homeworkTimeText.setText(time + "小时后过期");
                } else {
                    time = (Utils.getSecondsByDateMinusNow(exerciseDate) % (60 * 60)) / 60;
                    if (time > 1) {
                        activity.homeworkTimeText.setText(time + "分钟后过期");
                    } else if (time > 0 && time < 1) {
                        activity.homeworkTimeText.setText("1分钟后过期");
                    }
                }
            }

        }
    }

    /**
     * 处理获取后的数据
     *
     * @param response
     * @param fragment
     */
    public static void dealEntryData(JSONObject response, VipIndexFragment fragment) {
        VipIndexEntryDataResp vipIndexEntryDataResp = GsonManager.getModel(response, VipIndexEntryDataResp.class);
        if (vipIndexEntryDataResp.getResponse_code() == 1) {
            if (vipIndexEntryDataResp.isIs_vip_member()) {
                SharedPreferences.Editor editor = Globals.sharedPreferences.edit();
                editor.putBoolean("vip" + LoginModel.getUserId(), true);
                editor.commit();
            }

            int exerciseTips = vipIndexEntryDataResp.getExercises().getTotal();
            if (exerciseTips > 99)
                exerciseTips = 99;
            if (exerciseTips > 0) {
                fragment.homeworkTipsText.setText(String.valueOf(exerciseTips));
                fragment.homeworkTipsText.setVisibility(View.VISIBLE);
            }

            int messageTips = vipIndexEntryDataResp.getNotifications().getUnread_total();
            if (messageTips > 99)
                messageTips = 99;
            if (messageTips > 0) {
                fragment.messageTips.setText(String.valueOf(messageTips));
                fragment.messageTips.setVisibility(View.VISIBLE);
            }


            String classDate = vipIndexEntryDataResp.getClassroom().getStart_time();
            String exerciseDate = vipIndexEntryDataResp.getExercises().getEnd_time();

            if (classDate != null && !"".equals(classDate)) {
                long time = Utils.getSecondsByDateMinusNow(classDate) / (60 * 60);
                if (time >= 24) {
                    long day = time / 24;
                    fragment.classTime.setText(day + "天后开课");
                } else if (time > 0) {
                    fragment.classTime.setText(time + "小时后开课");
                } else {
                    time = (Utils.getSecondsByDateMinusNow(classDate) % (60 * 60)) / 60;
                    if (time > 1) {
                        fragment.classTime.setText(time + "分钟后开课");
                    } else if (time > 0 && time < 1) {
                        fragment.classTime.setText("1分钟后开课");
                    } else {
                        fragment.classTime.setText("正在上课");
                    }

                }
            }

            if (exerciseDate != null && !"".equals(exerciseDate)) {
                long time = Utils.getSecondsByDateMinusNow(exerciseDate) / (60 * 60);
                if (time >= 24) {
                    long day = time / 24;
                    fragment.homeworkTimeText.setText(day + "天后过期");
                } else if (time > 0) {
                    fragment.homeworkTimeText.setText(time + "小时后过期");
                } else {
                    time = (Utils.getSecondsByDateMinusNow(exerciseDate) % (60 * 60)) / 60;
                    if (time > 1) {
                        fragment.homeworkTimeText.setText(time + "分钟后过期");
                    } else if (time > 0 && time < 1) {
                        fragment.homeworkTimeText.setText("1分钟后过期");
                    }
                }
            }

        }
    }
}
