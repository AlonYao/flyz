package com.appublisher.quizbank.common.measure.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.common.measure.MeasureConstants;

import java.util.Map;

/**
 * 做题模块
 */

public class MeasureRequest extends Request implements MeasureApi, MeasureConstants{

    public MeasureRequest(Context context) {
        super(context);
    }

    public MeasureRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return LoginParamBuilder.finalUrl(url);
    }

    /**
     * 快速智能练习
     */
    public void getAutoTraining() {
        asyncRequest(getFinalUrl(getAutoTraining), AUTO_TRAINING, "object");
    }

    /**
     * 专项练习获取题目
     *
     * @param note_id 知识点id
     * @param type    类型: all：所有题目 error：只从错题里抽 collect：只从收藏的题目里抽
     */
    public void getNoteQuestions(int note_id,
                                 String type) {
        asyncRequest(
                getFinalUrl(getNoteQuestions) + "&note_id=" + note_id + "&type=" + type,
                NOTE_QUESTIONS,
                "object");
    }

    /**
     * 生成试卷练习(仅限天天模考、整卷和估分)
     *
     * @param paper_id   试卷id
     * @param paper_type 试卷类型(mokao：天天模考 entire：整卷 evaluate：估分)
     */
    public void getPaperExercise(int paper_id, String paper_type) {
        asyncRequest(
                getFinalUrl(getPaperExercise)
                        + "&paper_id=" + paper_id
                        + "&paper_type=" + paper_type,
                PAPER_EXERCISE, "object");
    }

    /**
     * 提交试卷
     */
    public void submitPaper(Map<String, String> params) {
        postRequest(getFinalUrl(submitPaper), params, SUBMIT_PAPER, "object");
    }

    /**
     * 获取历史练习内容
     *
     * @param exercise_id   单次练习的id
     * @param exercise_type 练习类型(mokao：天天模考 entire：整卷 auto：智能练习 等等)
     */
    public void getHistoryExerciseDetail(int exercise_id, String exercise_type) {
        asyncRequest(getFinalUrl(getHistoryExerciseDetail)
                        + "&exercise_id=" + exercise_id + "&exercise_type=" + exercise_type,
                HISTORY_EXERCISE_DETAIL, "object");
    }

    /**
     * 收藏&取消收藏题目
     *
     * @param params 参数
     */
    public void collectQuestion(Map<String, String> params) {
        postRequest(getFinalUrl(collectQuestion), params, COLLECT_QUESTION, "object");
    }

    /**
     * 获取错题&收藏
     *
     * @param note_id 知识点id
     * @param type    类型: collect:收藏 error:错题
     */
    public void getCollectErrorQuestions(int note_id,
                                      String type) {
        asyncRequest(
                getFinalUrl(collectErrorQuestions)
                        + "&note_id=" + note_id + "&type=" + type,
                COLLECT_ERROR_QUESTIONS,
                "object");
    }

}
