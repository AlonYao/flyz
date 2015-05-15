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

    // 保存错题报告
    public static SharedPreferences reportErrorQuestions;

    // 保存知识点进度
    public static SharedPreferences knowledgePointProgress;

    // 数据库状态
    public static boolean db_initialize = false;

    // 是否从游客状态变成已注册用户
    public static boolean is_fromGuestToUser = false;

    // 友盟统计：进入登陆注册页面的方式
    public static String umeng_login_event;

    // 友盟统计：退出时的当前任务类型
    public static String umeng_quiz_nexttask;

    // 友盟统计：退出时完成的当前任务数
    public static int umeng_quiz_tasknum;

    // 友盟统计：上一个事件名称
    public static String umeng_quiz_lastevent;

    // 记录最近的系统通知的id
    public static int last_notice_id;

    // 记录公开课数据模型
    public static LiveCourseM live_course;
}
