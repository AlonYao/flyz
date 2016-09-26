package com.appublisher.quizbank.common.promote;

import com.appublisher.quizbank.network.ApiConstants;

/**
 * 国考公告解读宣传
 */
public interface PromoteApi extends ApiConstants{

    // 获取数据
    String getPromoteData = baseUrl + "course/get_promote_flash";

}
