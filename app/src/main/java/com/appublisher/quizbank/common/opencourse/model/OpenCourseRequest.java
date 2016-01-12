package com.appublisher.quizbank.common.opencourse.model;

import android.content.Context;

import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;

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

}
