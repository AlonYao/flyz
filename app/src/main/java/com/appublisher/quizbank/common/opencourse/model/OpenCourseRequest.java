package com.appublisher.quizbank.common.opencourse.model;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

import java.util.HashMap;

/**
 * 公开课模块请求
 */
public class OpenCourseRequest extends Request implements OpenCourseApi{

    public OpenCourseRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    /**
     * 获取公开课列表
     */
    public void getOpenCourseList() {
        asyncRequest(ParamBuilder.finalUrl(getOpenCourseList), "open_course_list", "object");
    }

    /**
     * 获取评价列表
     * @param course_id 课程id
     * @param class_id 课堂id
     * @param lector_id 教师id
     * @param page 每页15条
     */
    public void getGradeList(int course_id, int class_id, int lector_id, int page) {
        asyncRequest(
                ParamBuilder.finalUrl(getRateList)
                        + "&course_id=" + course_id
                        + "&class_id=" + class_id
                        + "&lector_id=" + lector_id
                        + "&page=" + page,
                "get_grade_list",
                "object");
    }

    /**
     * 获取未评价的课堂列表
     * @param is_open 是否为公开课(true:公开课 false:课程中心)
     * @param page 每页15条
     */
    public void getUnratedClass(String is_open, int page) {
        asyncRequest(
                ParamBuilder.finalUrl(getUnratedClass)
                        + "&is_open=" + is_open
                        + "&page=" + page,
                "get_unrated_class",
                "object");
    }

    /**
     * 提交评价
     * @param entity 评价信息
     */
    public void rateClass(OpenCourseRateEntity entity) {
        if (entity == null) return;

        HashMap<String, String> map = new HashMap<>();
        map.put("course_id", String.valueOf(entity.course_id));
        map.put("is_open", entity.is_open);
        map.put("class_id", String.valueOf(entity.class_id));
        map.put("score", String.valueOf(entity.score));
        map.put("comment", entity.comment);

        postRequest(ParamBuilder.finalUrl(rateClass), map, "rate_class", "object");
    }

}
