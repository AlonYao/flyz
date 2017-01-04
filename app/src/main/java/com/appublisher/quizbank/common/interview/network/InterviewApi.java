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

    // 收藏、取消收藏题目
    String collectQuestion = baseUrl + "quizbank/update_collected_status";

    // 记录页面的面试页面的数据
    public String studyRecordInterviewPaperDetail = baseUrl + "quizbank/history_interview_detail";

    // 记录页面中面试中的条目的数据
    public String recordInterviewDetail = baseUrl + "quizbank/get_note_list";

    // 记录页面中面试中的收藏的数据
    public String recordInterviewCollectDetail = baseUrl + "quizbank/get_note_collect";

}
