package com.appublisher.quizbank.common.measure.network;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 做题模块
 */

public class MeasureParamBuilder {

    /**
     * 提交试卷参数
     *
     * @param paper_id   试卷id
     * @param paper_type 试卷类型
     * @param redo       是否为重新做题
     * @param duration   答题总时长（秒）
     * @param questions  序列化json，同上岸计划，额外增加每道题的时间
     * @param status     完成状态
     * @return 参数
     */
    public static Map<String, String> submitPaper(int paper_id,
                                                  String paper_type,
                                                  boolean redo,
                                                  int duration,
                                                  String questions,
                                                  String status) {
        Map<String, String> params = new HashMap<>();
        params.put("paper_id", String.valueOf(paper_id));
        params.put("paper_type", paper_type);

        if (redo) {
            params.put("redo", "true");
        } else {
            params.put("redo", "false");
        }

        params.put("duration", String.valueOf(duration));
        params.put("questions", questions);
        params.put("status", status);

        return params;
    }

    /**
     * 收藏&取消收藏题目
     *
     * @param question_id 题目id
     * @param isCollect boolean
     * @return 参数Map
     */
    public static Map<String, String> collectQuestion(int question_id, boolean isCollect) {
        Map<String, String> params = new Hashtable<>();
        params.put("question_id", String.valueOf(question_id));
        if (isCollect) {
            params.put("type", "collect");
        } else {
            params.put("type", "cancel");
        }

        return params;
    }

    /**
     * 删除错题
     *
     * @param question_id 题目id
     * @return 参数Map
     */
    public static Map<String, String> deleteErrorQuestion(int question_id) {
        Map<String, String> params = new Hashtable<>();
        params.put("question_id", String.valueOf(question_id));
        return params;
    }

}
