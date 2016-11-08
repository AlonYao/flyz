package com.appublisher.quizbank.common.measure.network;

import com.appublisher.lib_basic.volley.ApiConstants;

/**
 * 做题模块
 */

public interface MeasureApi extends ApiConstants{

    // 快速智能练习
    String getAutoTraining = baseUrl + "quizbank/auto_training";

    // 专项练习获取题目
    String getNoteQuestions = baseUrl + "quizbank/get_note_questions";

}
