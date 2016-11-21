package com.appublisher.quizbank.common.measure.network;

import java.util.HashMap;
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

}
