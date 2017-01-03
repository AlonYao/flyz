package com.appublisher.quizbank.common.interview.network;


import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;

import java.util.Map;

/**
 * Created by jinbao on 2016/11/16.
 */

public class InterviewRequest extends Request implements InterviewApi {

    public InterviewRequest(Context context) {
        super(context);
    }

    public InterviewRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return LoginParamBuilder.finalUrl(url);
    }

    /**
     * 获取filter
     */
    public void getInterviewFilter() {
        asyncRequest(getFinalUrl(interviewFilter), "interview_filter", "object");
    }

    /**
     * 获取试卷列表
     *
     * @param area_id
     * @param year
     * @param note_id
     * @param page
     */
    public void getPaperList(int area_id, int year, int note_id, int page) {
        asyncRequest(getFinalUrl(interviewPaperList)
                + "&area_id=" + area_id
                + "&year=" + year
                + "&note_id=" + note_id
                + "&page=" + page, "interview_paper_list", "object");
    }

    /**
     * 获取名师解析
     */
    public void getTeacherPaperList(int page) {
        asyncRequest(getFinalUrl(interviewTeacherPaperList)
                + "&page=" + page, "interview_paper_list", "object");
    }

    /**
     * 试卷详情页
     *
     * @param paper_id
     */
    public void getPaperDetail(int paper_id, String paper_type, int note_id) {
        asyncRequest(getFinalUrl(interviewPaperDetail)
                + "&paper_id=" + paper_id
                + "&paper_type=" + paper_type
                + "&note_id=" + note_id, "paper_detail", "object");
    }

    /*
    *   提交录音
    * */
    public void submitRecord(Map<String, String> params) {

        postRequest(getFinalUrl(submitRecord), params, "submit_record", "object");
    }

    /**
     * 收藏&取消收藏题目
     *
     * @param params 参数
     */
    public void collectQuestion(Map<String, String> params) {
        postRequest(getFinalUrl(collectQuestion), params, "update_collected_status", "object");   // 第三个是apiName

    }
}
