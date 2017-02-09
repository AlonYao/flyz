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
    *   记录页面中的面试页面的数据
    * */
//    public void getStudyRecordInterviewPaperDetail(int user_id, String type, String time){
//        asyncRequest(getFinalUrl(studyRecordInterviewPaperDetail)
//                + "&user_id=" + user_id
//                + "&type=" + type
//                + "&time=" + time, "history_interview_detail", "object"
//        );
//    }
    public void getStudyRecordInterviewPaperDetail(String type, String time) {
        asyncRequest(getFinalUrl(studyRecordInterviewPaperDetail)
                + "&type=" + type
                + "&time=" + time, "history_interview_detail", "object"
        );
    }

    /*
    *    获取记录页面中面试页面:收藏页面中的数据
    * */
    public void getRecordInterviewCollectDetail() {
        asyncRequest(getFinalUrl(recordInterviewDetail),
                "get_note_list", "object"
        );
    }

    /*
    *   获取面试页面中的收藏页面的数据
    * */
    public void getRecordInterviewCollectPaperDetail(int note_id) {
        asyncRequest(getFinalUrl(recordInterviewCollectDetail)
                        + "&note_id=" + note_id,
                "get_note_collect", "object"
        );
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

    /**
     * 获取名师点评商品列表
     */
    public void getTeacherCommentProducts() {
        asyncRequest(getFinalUrl(interviewCommentProducts), "comment_products", "object");
    }

    /**
     * 创建系统订单
     *
     * @param params
     */
    public void genOrder(Map<String, String> params) {
        postRequest(getFinalUrl(interviewGenOrder), params, "gen_order", "object");
    }

    /**
     * 订单信息
     */
    public void getOrderStatus(int order_num) {
        asyncRequest(getFinalUrl(orderStatus) + "&order_num=" + order_num, "order_status", "object");

    }

    /**
     * 点评filter
     */
    public void getCommentFilter() {
        asyncRequest(getFinalUrl(commentFilter), "comment_filter", "object");
    }

    /**
     * 获取点评列表
     */
    public void getCommentList(int status_id, int note_id, int page) {
        asyncRequest(getFinalUrl(commentList) + "&status_id=" + status_id + "&note_id=" + note_id + "&page=" + page, "comment_list", "object");
    }
}
