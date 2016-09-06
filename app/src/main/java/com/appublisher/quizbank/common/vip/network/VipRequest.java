package com.appublisher.quizbank.common.vip.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.network.ParamBuilder;

import java.util.Map;

/**
 * 小班模块
 */
public class VipRequest extends Request implements VipApi {

    public VipRequest(Context context) {
        super(context);
    }

    public VipRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return ParamBuilder.finalUrl(url);
    }

    /**
     * 提交作业
     */
    public void submit(Map<String, String> params) {
        postRequest(getFinalUrl(submit), params, "submit", "object");
    }

    /**
     * 小班消息列表
     *
     * @param page
     */
    public void getVipNotifications(int page) {
        asyncRequest(ParamBuilder.finalUrl(getVipNotifications) + "&page=" + page, "notification_list", "object");
    }

    /**
     * 获取练习列表
     *
     * @param status_id
     * @param category_id
     * @param type_id
     */
    public void getExerciseList(int status_id, int category_id, int type_id) {
        asyncRequest(ParamBuilder.finalUrl(getExerciseList) + "&status_id=" + status_id + "&category_id=" + category_id + "&type_id=" + type_id, "exercise_list", "object");
    }

    /**
     * 获取练习详情
     * @param exercise_id
     */
    public void getExerciseDetail(int exercise_id) {
        asyncRequest(ParamBuilder.finalUrl(getExerciseDetail) + "&exercise_id=" + exercise_id, "exercise_detail", "object");
    }

    public void getVipFilter(){
        asyncRequest(ParamBuilder.finalUrl(getVipFilter),"vip_filter","object");
    }
}
