package com.appublisher.quizbank.network;

import android.graphics.Bitmap.CompressFormat;

public interface ApiConstants {
	// 服务器切换：dev为测试服务器，api为正式服务器
    public static final String base = "dev";

	public static final String baseUrl = "http://api." + base + ".appublisher.com/";

    public static final String baseUrlImg = "http://img.edu.appublisher.com/";

    // 用户登录
    public static final String userLogin = baseUrl + "common/user_login";

    // 用户注册---获取验证码
    public static final String getSmsCode = baseUrl + "common/gen_mobile_token";

    // 用户注册---校验验证码
    public static final String checkSmsCode = baseUrl + "common/validate_mobile_token";

    // 用户注册---手机号注册
    public static final String userRegister = baseUrl + "common/user_register";

    // 用户注册---guest注册
    public static final String guestRegister = baseUrl + "common/guest_register";

    // 修改个人信息
    public static final String changeUserInfo = baseUrl + "common/user_info_update";

    // 登录信息授权
    public static final String authHandle = baseUrl + "common/auth_handle";

    // 修改密码
    public static final String changePwd = baseUrl + "common/user_pswd_update";

    // 获取当天的上岸计划
    public static final String getLatestPlan = baseUrl + "dailyplan/get_latest_plan";

    // 获取上岸计划具体任务详情
    public static final String getTaskDetail = baseUrl + "dailyplan/get_task_detail";

    // 完成任务提交数据
    public static final String submitTaskFinish = baseUrl + "dailyplan/finish_task";

    // 获取归类详情
    public static final String getGuileiDetail = baseUrl + "dailyplan/get_guilei_detail";

    // 获取学前学后详情
    public static final String getXqXhDetail = baseUrl + "dailyplan/get_task_questions";

    // 获取考试项目
    public static final String getExamList = baseUrl + "dailyplan/get_exams";

    // 设置考试项目
    public static final String setExam = baseUrl + "dailyplan/set_exam";

    // 获取自取任务
    public static final String getExtraTask = baseUrl + "dailyplan/get_extra_task";

    // 获取已完成的上岸计划历史
    public static final String getHistoryPlan = baseUrl + "dailyplan/get_history_plan";

    // 获取任务收藏列表
    public static final String getCollectedTasks = baseUrl + "dailyplan/get_collected_tasks";

    // 收藏上岸计划任务
    public static final String collectTask = baseUrl + "dailyplan/collect_task";

    // 删除收藏的上岸计划任务
    public static final String deleteCollectedTask = baseUrl + "dailyplan/delete_collected_task";

    // 获取错题列表
    public static final String getErrorQuestions = baseUrl + "dailyplan/error_question_list";

    // 获取真题
    public static final String getQuestions = baseUrl + "dailyplan/get_error_questions";

    // 获取雷达图和折线图信息
    public static final String getPlanSummary = baseUrl + "dailyplan/get_plan_summary";

    // 用户登出
    public static final String userLogout = baseUrl + "common/user_logout";

    // 反馈错题
    public static final String reportErrorQuestion = baseUrl + "dailyplan/report_error_question";

    // 忘记密码
    public static final String forgetPwd = baseUrl + "common/reset_password";

    // 获取知识点看错题
    public static final String getNoteErrorQuestions = baseUrl + "dailyplan/note_error_questions";

    // 获取全局配置
    public static final String getGlobalSettings = baseUrl + "dailyplan/get_global_settings";

    // 图片相关
	public static int DISK_IMAGECACHE_SIZE = 1024*1024*10; //设置10M的图片缓存
	public static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	public static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
}
