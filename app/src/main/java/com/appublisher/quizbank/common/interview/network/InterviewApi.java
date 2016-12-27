package com.appublisher.quizbank.common.interview.network;

import com.appublisher.lib_basic.volley.ApiConstants;

/**
 * Created by jinbao on 2016/11/16.
 */

public interface InterviewApi extends ApiConstants {

    //获取filter
    public String interviewFilter = baseUrl + "quizbank/interview_paper_filters";

    //试卷列表
    public String interviewPaperList = baseUrl + "quizbank/get_interview_papers";

    //名师解析
    public String interviewTeacherPaperList = baseUrl + "quizbank/teacher_interview_papers";

    //面试详情页
    public String interviewPaperDetail = baseUrl + "quizbank/interview_paper_detail";

    // 提交录音
    public String submitRecord = baseUrl + "quizbank/submit_record";
}
