package com.appublisher.quizbank.common.vip.network;

import android.support.v4.view.ViewPager;

import com.appublisher.quizbank.common.vip.model.VipSubmitEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 小班模块
 */
public class VipParamBuilder {

    /**
     * 小班提交
     * @param exercise_id 作业id
     * @param question_id 题目id
     * @param image_url 答题图片链接
     * @param record_id 用户答题记录id
     * @param postil 评论内容
     * @param level 评论级别
     * @param answer_content 答案内容
     * @param duration 总时长
     * @param summary 单题统计
     * @param done 是否是最后一道题
     * @return Map
     */
    public static Map<String, String> submit(String exercise_id,
                                             String question_id,
                                             String image_url,
                                             String record_id,
                                             String postil,
                                             String level,
                                             String answer_content,
                                             String duration,
                                             String summary,
                                             String done) {
        Map<String, String> params = new HashMap<>();
        params.put("exercise_id", exercise_id);
        params.put("question_id", question_id);
        params.put("image_url", image_url);
        params.put("record_id", record_id);
        params.put("postil", postil);
        params.put("level", level);
        params.put("answer_content", answer_content);
        params.put("duration", duration);
        params.put("summary", summary);
        params.put("done", done);
        return params;
    }

    /**
     * 小班提交
     * @return Map
     */
    public static Map<String, String> submit(VipSubmitEntity entity) {
        if (entity == null) return new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("exercise_id", String.valueOf(entity.exercise_id));
        params.put("question_id", String.valueOf(entity.question_id));
        params.put("image_url", entity.image_url);
        params.put("record_id", String.valueOf(entity.record_id));
        params.put("postil", entity.postil);
        params.put("level", String.valueOf(entity.level));
        params.put("answer_content", entity.answer_content);
        params.put("duration", String.valueOf(entity.duration));
        params.put("summary", entity.summary);
        params.put("done", String.valueOf(entity.done));
        return params;
    }

}