package com.appublisher.quizbank.common.opencourse.model;

import com.appublisher.quizbank.network.ApiConstants;

/**
 * 公开课接口
 */
public interface OpenCourseApi extends ApiConstants{

    // 获取公开课列表
    String getOpenCourseList = baseUrl + "course/get_open_class";

}
