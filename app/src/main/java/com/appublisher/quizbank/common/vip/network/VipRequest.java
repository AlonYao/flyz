package com.appublisher.quizbank.common.vip.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.network.ParamBuilder;

import java.util.Map;

/**
 * 小班模块
 */
public class VipRequest extends Request implements VipApi {

    public static final String GET_INTELLIGENT_PAPER = "get_intelligent_paper";

    public VipRequest(Context context) {
        super(context);
    }

    public VipRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return LoginParamBuilder.finalUrl(url);
    }

    /**
     * 提交作业
     */
    public void submit(Map<String, String> params) {
        postRequest(getFinalUrl(submit), params, "submit", "object");
    }

    /**
     * 获取智能组卷
     * @param exercise_id 练习id
     */
    public void getIntelligentPaper(int exercise_id) {
        asyncRequest(
                getFinalUrl(getExerciseDetail) + "&exercise_id=" + exercise_id,
                GET_INTELLIGENT_PAPER,
                "object");
    }

    /**
     * 小班消息列表
     *
     * @param page
     */
    public void getVipNotifications(int page) {
        asyncRequest(
                getFinalUrl(getVipNotifications) + "&page=" + page,
                "notification_list",
                "object");
    }

    /**
     * 获取练习列表
     *
     * @param status_id
     * @param category_id
     * @param type_id
     */
    public void getExerciseList(int status_id, int category_id, int type_id) {
        asyncRequest(
                getFinalUrl(getExerciseList)
                + "&status_id=" + status_id
                + "&category_id=" + category_id
                + "&type_id=" + type_id,
                "exercise_list",
                "object");
    }

    /**
     * 获取练习详情
     *
     * @param exercise_id
     */
    public void getExerciseDetail(int exercise_id) {
        asyncRequest(
                getFinalUrl(getExerciseDetail)
                + "&exercise_id=" + exercise_id,
                "exercise_detail",
                "object");
    }

    /**
     * 获取小班filter
     */
    public void getVipFilter() {
        asyncRequest(ParamBuilder.finalUrl(getVipFilter), "vip_filter", "object");
    }

    /**
     * 获取小班练习列表
     *
     * @param status_id
     * @param category_id
     * @param type_id
     */
    public void getVipExercises(int status_id, int category_id, int type_id) {
        asyncRequest(ParamBuilder.finalUrl(getVipExercises) + "&status_id=" + status_id + "&category_id=" + category_id + "&type_id=" + type_id, "vip_exercise", "object");
    }
}
