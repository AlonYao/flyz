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

    // 生成试卷练习(仅限天天模考、整卷和估分)
    String getPaperExercise = baseUrl + "quizbank/get_paper_exercise";

    // 提交试卷
    String submitPaper = baseUrl + "quizbank/submit_paper";

    // 获取历史练习内容
    String getHistoryExerciseDetail = baseUrl + "quizbank/history_exercise_detail";

}
