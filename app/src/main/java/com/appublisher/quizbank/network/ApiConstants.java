package com.appublisher.quizbank.network;

import android.graphics.Bitmap.CompressFormat;

public interface ApiConstants {
	// 服务器切换：dev为测试服务器，api为正式服务器
    String base = "dev";

	String baseUrl = "http://api." + base + ".appublisher.com/";

    String baseUrlImg = "http://img.edu.appublisher.com/";

    // 用户登录
    String userLogin = baseUrl + "common/user_login";

    // 用户注册---获取验证码
    String getSmsCode = baseUrl + "common/gen_mobile_token";

    // 用户注册---校验验证码
    String checkSmsCode = baseUrl + "common/validate_mobile_token";

    // 用户注册---手机号注册
    String userRegister = baseUrl + "common/user_register";

    // 修改个人信息
    String changeUserInfo = baseUrl + "common/user_info_update";

    // 登录信息授权
    String authHandle = baseUrl + "common/auth_handle";

    // 修改密码
    String changePwd = baseUrl + "common/user_pswd_update";

    // 获取考试项目
    String getExamList = baseUrl + "quizbank/get_exams";

    // 设置考试项目
    String setExam = baseUrl + "quizbank/set_exam";

    // 用户登出
    String userLogout = baseUrl + "common/user_logout";

    // 忘记密码
    String forgetPwd = baseUrl + "common/reset_password";

    // 快速智能练习
    String getAutoTraining = baseUrl + "quizbank/auto_training";

    // 首页数据
    String getEntryData = baseUrl + "quizbank/get_entry_data";

    // 专项练习获取题目
    String getNoteQuestions = baseUrl + "quizbank/get_note_questions";

    // 提交试卷
    String submitPaper = baseUrl + "quizbank/submit_paper";

    // 获取知识点层级列表
    String getNoteHierarchy = baseUrl + "quizbank/get_note_hierarchy";

    // 获取错题收藏
    String collectErrorQuestions =
            baseUrl + "quizbank/collect_error_questions";

    // 获取整卷试卷列表
    String getEntirePapers = baseUrl + "quizbank/get_entire_papers";

    // 获取地区和年份
    String getAreaYear = baseUrl + "quizbank/get_area_year";

    // 生成试卷练习(仅限天天模考、整卷和估分)
    String getPaperExercise = baseUrl + "quizbank/get_paper_exercise";

    // 历史列表
    String getHistoryMokao = baseUrl + "quizbank/get_history_mokao";

    // 获取历史练习内容
    String getHistoryExerciseDetail = baseUrl + "quizbank/history_exercise_detail";

    // 获取学习记录列表
    String getHistoryPapers = baseUrl + "quizbank/get_history_papers";

    // 获取能力评估
    String getEvaluation = baseUrl + "quizbank/get_evaluation";

    // 获取全局配置
    String getGlobalSettings = baseUrl + "quizbank/get_global_settings";

    // 获取通知
    String getNotifications = baseUrl + "quizbank/get_notifications";

    // 收藏、取消收藏题目
    String collectQuestion = baseUrl + "quizbank/collect_question";

    // 错题反馈
    String reportErrorQuestion = baseUrl + "quizbank/report_error_question";

    // 删除错题
    String deleteErrorQuestion = baseUrl + "quizbank/delete_error_question";

    // 图片相关
    int DISK_IMAGECACHE_SIZE = 1024*1024*10; //设置10M的图片缓存
	CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    //PNG is lossless so quality is ignored but must be provided
    int DISK_IMAGECACHE_QUALITY = 100;
}
