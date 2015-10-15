package com.appublisher.quizbank.model.offline.network;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

/**
 * 离线模块网络请求
 */
public class OfflineRequest extends Request implements OfflineApiConstants{

    public OfflineRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    /**
     * 获取已购课程列表
     */
    public void getPurchasedCourses() {
        asyncRequest(ParamBuilder.finalUrl(getPurchasedCourses), "purchased_courses", "object");
    }
}
