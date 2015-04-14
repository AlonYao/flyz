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

    // 修改个人信息
    public static final String changeUserInfo = baseUrl + "common/user_info_update";

    // 登录信息授权
    public static final String authHandle = baseUrl + "common/auth_handle";

    // 修改密码
    public static final String changePwd = baseUrl + "common/user_pswd_update";

    // 获取考试项目
    public static final String getExamList = baseUrl + "dailyplan/get_exams";

    // 设置考试项目
    public static final String setExam = baseUrl + "dailyplan/set_exam";

    // 用户登出
    public static final String userLogout = baseUrl + "common/user_logout";

    // 忘记密码
    public static final String forgetPwd = baseUrl + "common/reset_password";

    // 快速智能练习
    public static final String getAutoTraining = baseUrl + "quizbank/auto_training";

    // 首页数据
    public static final String getEntryData = baseUrl + "quizbank/get_entry_data";

    // 专项练习获取题目
    public static final String getNoteQuestions = baseUrl + "quizbank/get_entire_papers";

    // 提交试卷
    public static final String submitPaper = baseUrl + "quizbank/submit_paper";

    // 图片相关
	public static int DISK_IMAGECACHE_SIZE = 1024*1024*10; //设置10M的图片缓存
	public static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	public static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
}
