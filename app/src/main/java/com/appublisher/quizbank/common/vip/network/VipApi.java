package com.appublisher.quizbank.common.vip.network;

import com.appublisher.lib_basic.volley.ApiConstants;

/**
 * 小班模块
 */
public interface VipApi extends ApiConstants {

    // 提交作业
    String submit = baseUrl + "vip/submit_exercise";

}
