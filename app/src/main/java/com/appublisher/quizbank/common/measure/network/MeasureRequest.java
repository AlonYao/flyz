package com.appublisher.quizbank.common.measure.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;

/**
 * 做题模块
 */

public class MeasureRequest extends Request implements MeasureApi{

    public static final String AUTO_TRAINING = "auto_training";
    public static final String NOTE_QUESTIONS = "note_questions";

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
        asyncRequest(getFinalUrl(getAutoTraining), "auto_training", "object");
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
                "note_questions",
                "object");
    }

}
