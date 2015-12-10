package com.appublisher.quizbank;

import android.content.SharedPreferences;

import com.appublisher.quizbank.model.netdata.course.PromoteLiveCourseResp;
import com.appublisher.quizbank.model.netdata.course.RateCourseResp;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseStatusResp;
import com.google.gson.Gson;

/**
 * 全局变量
 */
public class Globals {

    // 调试模式
    public static final boolean IS_DEBUG = false;

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

    // Gson对象
    public static Gson gson;

    // 快讯数据模型
    public static PromoteLiveCourseResp promoteLiveCourseResp;

    // 评价赠送课程数据
    public static RateCourseResp rateCourseResp;
}
