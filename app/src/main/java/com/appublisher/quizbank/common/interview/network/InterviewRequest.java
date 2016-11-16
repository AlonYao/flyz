package com.appublisher.quizbank.common.interview.network;


import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipApi;

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
}
