package com.appublisher.quizbank.common.interview.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinbao on 2016/11/16.
 */

public class InterviewParamBuilder {
    /**
     * 提交试卷参数
     *
     * @param question_id   试卷id
     * @param user_audio 用户录音地址
     * @param duration   答题总时长（秒）
     * @param type   题目的类型:guokao/teacher/category/before
     *
     * @return 参数
     */
    public static Map<String, String> submitPaper(int question_id,
                                                  String user_audio,
                                                  int duration,
                                                  String type
                                                  ) {
        Map<String, String> params = new HashMap<>();
        params.put("question_id", String.valueOf(question_id));
        params.put("user_audio", user_audio);
        params.put("duration", String.valueOf(duration));
        params.put("type", type);

        return params;
    }
    /**
     * 提交收藏的参数
     *
     * @param type   是否收藏
     * @param question_id 问题id
     * @return 参数
     */
    public static Map<String, String> submitCollectStated(String type,
                                                  int question_id
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("question_id", String.valueOf(question_id));
        return params;
    }
}
