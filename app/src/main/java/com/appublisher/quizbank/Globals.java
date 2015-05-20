package com.appublisher.quizbank;

import android.content.SharedPreferences;

import com.appublisher.quizbank.model.netdata.homepage.LiveCourseM;

/**
 * 全局变量
 */
public class Globals {

    // 版本号
    public static String appVersion;

    // 本地缓存文件
    public static SharedPreferences sharedPreferences;

    // 数据库状态
    public static boolean db_initialize = false;

    // 记录最近的系统通知的id
    public static int last_notice_id;

    // 记录公开课数据模型
    public static LiveCourseM live_course;

    // 记录是否要弹出评分Alert
    public static boolean is_show_grade_alert = false;
}
