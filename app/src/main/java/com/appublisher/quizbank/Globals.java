package com.appublisher.quizbank;

import android.content.SharedPreferences;

import com.appublisher.lib_course.opencourse.netdata.OpenCourseStatusResp;
import com.appublisher.quizbank.model.netdata.course.RateCourseResp;

/**
 * 全局变量
 */
public class Globals {

    // 调试模式
    public static final boolean IS_DEBUG = true;

    // 版本号
    public static String appVersion;

    // 本地缓存文件
    public static SharedPreferences sharedPreferences;

    // 数据库状态
    public static boolean db_initialize = false;

    // 记录最近的系统通知的id
    public static int last_notice_id;

    // 记录公开课状态数据模型
    public static OpenCourseStatusResp openCourseStatus;

    // 评价赠送课程数据
    public static RateCourseResp rateCourseResp;
}
