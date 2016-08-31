package com.appublisher.quizbank.common.vip.model;

import android.view.View;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.netdata.ExerciseResp;
import com.appublisher.quizbank.common.vip.netdata.VipNotificationResp;
import com.appublisher.quizbank.model.netdata.course.CourseListResp;
import com.appublisher.quizbank.model.netdata.course.CourseM;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by jinbao on 2016/8/30.
 */
public class VipIndexModel {

    /* 处理消息未显示数*/
    public static void dealNotifications(JSONObject response, TextView textView) {
        VipNotificationResp vipNotificationResp = GsonManager.getModel(response, VipNotificationResp.class);
        if (vipNotificationResp.getResponse_code() == 1) {
            List<VipNotificationResp.NotificationsBean> list = vipNotificationResp.getNotifications();

            int sum = 0;
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).isIs_read()) {
                    sum++;
                }
            }
            Logger.i("vip_message" + sum);
            if (sum != 0) {
                textView.setText(String.valueOf(sum));
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    /*处理即将开课的课程*/
    public static void dealCourseList(JSONObject response, TextView textView) {
        CourseListResp courseListResp = GsonManager.getModel(response, CourseListResp.class);
        if (courseListResp.getResponse_code() == 1) {
            List<CourseM> list = courseListResp.getCourses();
            String minTime = "";
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getToday_classes().size() != 0) {
                    List<CourseM.TodayClassesBean> classesBeen = list.get(i).getToday_classes();
                    for (int j = 0; j < classesBeen.size(); j++) {
                        if ("".equals(minTime)) {
                            minTime = classesBeen.get(j).getStart_time();
                        } else {
                            if (classesBeen.get(j).getStart_time().compareTo(minTime) < 0) {
                                minTime = classesBeen.get(j).getStart_time();
                            }
                        }
                    }
                }
            }
            long time = Utils.getSecondsByDateMinusNow(minTime) / (60 * 60);
            if (time >= 24) {
                long day = time / 24;
                textView.setText(day + "天后开课");
            } else if (time > 0) {
                textView.setText(time + "小时后开课");
            } else {
                time = (Utils.getSecondsByDateMinusNow(minTime) % (60 * 60)) / 60;
                if (time > 1) {
                    textView.setText(time + "分钟后开课");
                } else if (time > 0 && time < 1) {
                    textView.setText("1分钟后开课");
                } else {
                    textView.setText("正在上课");
                }

            }
        }
    }

    /*处理我的作业*/
    public static void dealExerciseList(JSONObject response, TextView timeText, TextView tipsText) {
        ExerciseResp exerciseResp = GsonManager.getModel(response, ExerciseResp.class);
        if (exerciseResp.getResponse_code() == 1) {
            List<ExerciseResp.ExercisesBean> list = exerciseResp.getExercises();
            int sum = 0;
            String minTime = "";
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus() == 6) {
                    sum++;
                }
                if (list.get(i).getStatus() == 0) {
                    sum++;
                    if ("".equals(minTime)) {
                        minTime = list.get(i).getEnd_time();
                    } else {
                        if (list.get(i).getEnd_time().compareTo(minTime) < 0) {
                            minTime = list.get(i).getEnd_time();
                        }
                    }
                }

            }
            Logger.i("vip_time_ex" + minTime);
            long time = Utils.getSecondsByDateMinusNow(minTime) / (60 * 60);
            if (time >= 24) {
                long day = time / 24;
                timeText.setText(day + "天后过期");
            } else if (time > 0) {
                timeText.setText(time + "小时后过期");
            } else {
                time = (Utils.getSecondsByDateMinusNow(minTime) % (60 * 60)) / 60;
                if (time > 1) {
                    timeText.setText(time + "分钟后过期");
                } else if (time > 0 && time < 1) {
                    timeText.setText("1分钟后过期");
                }
            }

            if (sum != 0) {
                if (sum > 99)
                    sum = 99;
                tipsText.setText(String.valueOf(sum));
                tipsText.setVisibility(View.VISIBLE);
            }
        }
    }
}
