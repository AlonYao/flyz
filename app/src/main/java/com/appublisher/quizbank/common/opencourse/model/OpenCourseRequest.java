package com.appublisher.quizbank.common.opencourse.model;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

/**
 * 公开课模块请求
 */
public class OpenCourseRequest extends Request implements OpenCourseApi{

    public OpenCourseRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    /**
     * 获取公开课列表
     */
    public void getOpenCourseList() {
        asyncRequest(ParamBuilder.finalUrl(getOpenCourseList), "open_course_list", "object");
    }
}
