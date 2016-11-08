package com.appublisher.quizbank.common.measure.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.common.measure.MeasureConstants;

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

}
