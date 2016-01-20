package com.appublisher.quizbank.common.opencourse.model;

import com.appublisher.quizbank.network.ApiConstants;

/**
 * 公开课接口
 */
public interface OpenCourseApi extends ApiConstants{

    // 获取公开课列表
    String getOpenCourseList = baseUrl + "course/get_open_class";

    // 获取评价列表
    String getRateList = baseUrl + "course/get_rate_list";

    // 获取未评价的课堂列表
    String getUnratedClass = baseUrl + "course/get_unrated_class";

    // 评价课程
    String rateClass = baseUrl + "course/rate_class";

}
