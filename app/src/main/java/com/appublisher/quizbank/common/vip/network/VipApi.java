package com.appublisher.quizbank.common.vip.network;

import com.appublisher.lib_basic.volley.ApiConstants;

/**
 * 小班模块
 */
public interface VipApi extends ApiConstants {

    /**
     * 提交作业
     */
    String submit = baseUrl + "vip/submit_exercise";

    /**
     * 小班消息列表
     */
    String getVipNotifications = baseUrl + "vip/get_notifications";

    /**
     * 获取练习列表
     */
    String getExerciseList = baseUrl + "vip/get_filtered_exercise";

    /**
     * 获取练习详情
     */
    String getExerciseDetail = baseUrl + "vip/get_exercise_detail";

    /**
     * 获取筛选条件
     */
    String getVipFilter = baseUrl + "vip/get_exercise_filter";

    /**
     * 获取练习列表
     */
    String getVipExercises = baseUrl + "vip/get_filtered_exercise";

    /**
     * 获取小班首页数据
     */
    String getVipIndexEntryData = baseUrl + "vip/get_entry_data";

    /**
     * 消息已读
     */
    String getReadNotification = baseUrl + "vip/read_notification";
}
