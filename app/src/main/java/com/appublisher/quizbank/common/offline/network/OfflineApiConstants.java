package com.appublisher.quizbank.common.offline.network;

import com.appublisher.quizbank.network.ApiConstants;

/**
 * 离线视频Api
 */
public interface OfflineApiConstants extends ApiConstants{

    // 获取已购课程列表
    String getPurchasedCourses = baseUrl + "course/get_purchased_courses";
}
