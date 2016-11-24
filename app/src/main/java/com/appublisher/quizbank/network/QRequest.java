package com.appublisher.quizbank.network;

import android.content.Context;

import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.volley.LoginParamBuilder;

import java.util.Map;

public class QRequest extends Request implements QApiConstants {

    public QRequest(Context context) {
        super(context);
    }

    public QRequest(Context context, RequestCallback callback) {
        super(context, callback);
    }

    private static String getFinalUrl(String url) {
        return LoginParamBuilder.finalUrl(url);
    }

    /*********************
     *     				 *
     * 	数据获取接口代码块	 *
     * 					 *
     *********************/

    /**
     * 获取服务器时间
     */
    public void getServerCurrentTime() {
        asyncRequest(getFinalUrl(serverCurrentTime), "server_current_time", "object");
    }

    /**
     * 获取考试项目列表
     */
    public void getExamList() {
        asyncRequest(getFinalUrl(getExamList), "exam_list", "object");
    }

    /**
     * 快速智能练习
     */
    public void getAutoTraining() {
        asyncRequest(getFinalUrl(getAutoTraining), "auto_training", "object");
    }

    /**
     * 获取常见问题
     */
    public void getQa() {
        asyncRequest(getFinalUrl(getQa), "qa", "object");
    }

    /**
     * 获取首页数据
     */
    public void getEntryData() {
        asyncRequest(getFinalUrl(getEntryData), "entry_data", "object");
    }

    /**
     * 获取模考和估分
     */
    public void getMockGufen() {
        asyncRequest(getFinalUrl(getMockGufen), "mock_gufen", "object");
    }

    /**
     * 获取轮播图
     */
    public void getCarousel() {
        asyncRequest(getFinalUrl(getCarousel), "get_carousel", "object");
    }

    /**
     * 专项练习获取题目
     *
     * @param note_id 知识点id
     * @param type    类型: all：所有题目 error：只从错题里抽 collect：只从收藏的题目里抽
     */
    public void getNoteQuestions(String note_id,
                                 String type) {
        asyncRequest(getFinalUrl(getNoteQuestions) + "&note_id=" + note_id + "&type=" + type,
                "note_questions",
                "object");
    }

    /**
     * 获取错题收藏
     *
     * @param note_id 知识点id
     * @param type    类型: collect:收藏 error:错题
     */
    public void collectErrorQuestions(String note_id,
                                      String type) {
        asyncRequest(
                getFinalUrl(collectErrorQuestions)
                        + "&note_id=" + note_id + "&type=" + type,
                "collect_error_questions",
                "object");
    }

    /**
     * 获取整卷试卷列表
     *
     * @param area_id 地区id
     * @param year    年份
     * @param offset  开始位置
     * @param count   数量
     */
    public void getEntirePapers(int area_id, int year, int offset, int count, String recommend) {
        asyncRequest(getFinalUrl(getEntirePapers) + "&area_id=" + area_id
                        + "&year=" + year + "&offset=" + offset + "&count=" + count
                        + "&recommend=" + recommend,
                "entire_papers", "object");
    }

    /**
     * 获取知识点层级
     *
     * @param type 类型(error:有错题的层级 collect:有收藏的层级 all:显示所有层级)
     */
    public void getNoteHierarchy(String type) {
        asyncRequest(getFinalUrl(getNoteHierarchy) + "&type=" + type,
                "note_hierarchy", "object");
    }

    /**
     * 获取地区和年份
     */
    public void getAreaYear() {
        asyncRequest(getFinalUrl(getAreaYear), "area_year", "object");
    }

    /**
     * 生成试卷练习(仅限天天模考、整卷和估分)
     *
     * @param paper_id   试卷id
     * @param paper_type 试卷类型(mokao：天天模考 entire：整卷 evaluate：估分)
     */
    public void getPaperExercise(int paper_id, String paper_type) {
        asyncRequest(getFinalUrl(getPaperExercise) + "&paper_id=" + paper_id
                + "&paper_type=" + paper_type, "paper_exercise", "object");
    }

    /**
     * 历史列表
     */
    public void getHistoryMokao() {
        asyncRequest(getFinalUrl(getHistoryMokao), "history_mokao", "object");
    }

    /**
     * 获取历史练习内容
     *
     * @param exercise_id   单次练习的id
     * @param exercise_type 练习类型(mokao：天天模考 entire：整卷 auto：智能练习 等等)
     */
    public void getHistoryExerciseDetail(int exercise_id, String exercise_type) {
        asyncRequest(getFinalUrl(getHistoryExerciseDetail)
                        + "&exercise_id=" + exercise_id + "&exercise_type=" + exercise_type,
                "history_exercise_detail", "object");
    }

    /**
     * 获取学习记录列表
     *
     * @param offset 开始位置(从1开始)
     * @param count  数量
     */
    public void getHistoryPapers(int offset, int count) {
        asyncRequest(getFinalUrl(getHistoryPapers) + "&offset=" + offset
                + "&count=" + count, "history_papers", "object");
    }

    /**
     * 获取能力评估
     */
    public void getEvaluation() {
        asyncRequest(getFinalUrl(getEvaluation), "evaluation", "object");
    }

    /**
     * 获取全局配置
     */
    public void getGlobalSettings() {
        asyncRequest(getFinalUrl(getGlobalSettings), "global_settings", "object");
    }

    /**
     * 获取模考总动员
     */
    public void getMockPreExamInfo(String mock_id) {
        asyncRequest(
                getFinalUrl(getMockPreExamInfo) + "&mock_id=" + mock_id,
                "mockpre_exam_info",
                "object");
    }

    /**
     * 获取通知
     *
     * @param offset 起点
     * @param count  数量
     */
    public void getNotifications(int offset, int count) {
        asyncRequest(getFinalUrl(getNotifications) + "&offset=" + offset
                + "&count=" + count, "notifications", "object");
    }

    /*********************
     *     				 *
     * 	数据提交接口代码块	 *
     * 					 *
     *********************/

    /**
     * 用户登录
     *
     * @param params 登录信息
     */
    public void login(Map<String, String> params) {
        postRequest(getFinalUrl(userLogin), params, "login", "object");
    }

    /**
     * 提交试卷
     */
    public void submitPaper(Map<String, String> params) {
        postRequest(getFinalUrl(submitPaper), params, "submit_paper", "object");
    }

    /**
     * 提交试卷
     */
    public void cacheSubmitPaper(Map<String, String> params) {
        postRequest(getFinalUrl(submitPaper), params, "cache_submit_paper", "object");
    }

    /**
     * 收藏&取消收藏题目
     *
     * @param params 参数
     */
    public void collectQuestion(Map<String, String> params) {
        postRequest(getFinalUrl(collectQuestion), params, "collect_question", "object");
    }

    /**
     * 错题反馈
     *
     * @param params 参数
     */
    public void reportErrorQuestion(Map<String, String> params) {
        postRequest(getFinalUrl(reportErrorQuestion),
                params, "report_error_question", "object");
    }

    /**
     * 错题删除
     *
     * @param params 删除
     */
    public void deleteErrorQuestion(Map<String, String> params) {
        postRequest(getFinalUrl(deleteErrorQuestion),
                params, "delete_error_question", "object");
    }

    /**
     * 标记通知已读
     *
     * @param params 参数
     */
    public void readNotification(Map<String, String> params) {
        postRequest(getFinalUrl(readNotification),
                params, "read_notification", "object");
    }

    /**
     * 预约公开课
     *
     * @param params 参数
     */
    public void bookOpenCourse(Map<String, String> params) {
        postRequest(getFinalUrl(bookOpenCourse),
                params, "book_open_course", "object");
    }

    /**
     * 预约模考
     *
     * @param params 参数
     */
    public void bookMock(Map<String, String> params) {
        postRequest(getFinalUrl(bookMcok),
                params, "book_mock", "object");
    }

    /**
     * 评论获取待赠送课程、开课
     *
     * @param params 参数
     */
    public void getRateCourse(Map<String, String> params) {
        postRequest(getFinalUrl(getRateCourse),
                params, "get_rate_course", "object");
    }

}
