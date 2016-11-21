package com.appublisher.quizbank.network;

import com.appublisher.lib_basic.OpenUDIDManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ParamBuilder implements QApiConstants {

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
    public static Map<String, String> submitPaper(String paper_id, String paper_type,
                                                  String redo, String duration,
                                                  String questions, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("paper_id", paper_id);
        params.put("paper_type", paper_type);
        params.put("redo", redo);
        params.put("duration", duration);
        params.put("questions", questions);
        params.put("status", status);

        return params;
    }

    /**
     * 收藏&取消收藏题目
     *
     * @param question_id 题目id
     * @param type        collect：收藏 cancel：取消收藏
     * @return 参数Map
     */
    public static Map<String, String> collectQuestion(String question_id, String type) {
        Map<String, String> params = new Hashtable<>();
        params.put("question_id", question_id);
        params.put("type", type);

        return params;
    }

    /**
     * 错题反馈
     *
     * @param question_id 题目id
     * @param error_type  1：图/文有错 2：答案有错 3：解析有错 4：我有更好的解析
     * @param my_analysis error_type为4时，更好的解析
     * @return 参数Map
     */
    public static Map<String, String> reportErrorQuestion(String question_id,
                                                          String error_type,
                                                          String my_analysis) {
        Map<String, String> params = new Hashtable<>();
        params.put("question_id", question_id);
        params.put("error_type", error_type);
        params.put("my_analysis", my_analysis);

        return params;
    }

    /**
     * 删除错题
     *
     * @param question_id 题目id
     * @return 参数Map
     */
    public static Map<String, String> deleteErrorQuestion(String question_id) {
        Map<String, String> params = new Hashtable<>();
        params.put("question_id", question_id);
        return params;
    }

    /**
     * 标记通知已读
     *
     * @param notification_id 题目id
     * @return 参数Map
     */
    public static Map<String, String> readNotification(String notification_id) {
        Map<String, String> params = new Hashtable<>();
        params.put("notification_id", notification_id);
        return params;
    }

    /**
     * 预约公开课
     *
     * @param course_id 课程id
     * @return 参数Map
     */
    public static Map<String, String> bookOpenCourse(String course_id) {
        Map<String, String> params = new HashMap<>();
        params.put("course_id", course_id);
        return params;
    }

    /**
     * 预约公开课
     *
     * @param action    获取课程:getCourse 开通课程:enroll
     * @param course_id 课程id
     * @return 参数Map
     */
    public static Map<String, String> getRateCourse(String action, String course_id) {
        Map<String, String> params = new HashMap<>();
        params.put("action", action);
        params.put("course_id", course_id);
        return params;
    }

    /**
     * 预约模考
     * @param mock_id
     * @return
     */
    public static Map<String,String> getBookMock(String mock_id){
        Map<String,String> params = new HashMap<>();
        params.put("mock_id",mock_id);
        return params;
    }

    /**
     * 获取国考推广跳转课程链接
     *
     * @param url
     * @return
     */
    public static String getPromoteCourseUrl(String url) {
        StringBuilder finalUrl = new StringBuilder();
        if (url.contains("?")) {
            finalUrl.append(url)
                    .append("&terminal_type=android_phone")
                    .append("&app_type=quizbank")
                    .append("&app_version=")
                    .append(Globals.appVersion)
                    .append("&uuid=")
                    .append(OpenUDIDManager.getID() == null ? "" : OpenUDIDManager.getID())
                    .append("&user_id=")
                    .append(LoginModel.getUserId())
                    .append("&user_token=")
                    .append(LoginModel.getUserToken())
                    .append("&timestamp=")
                    .append(System.currentTimeMillis());
        } else {
            finalUrl.append(url)
                    .append("?terminal_type=android_phone")
                    .append("&app_type=quizbank")
                    .append("&app_version=")
                    .append(Globals.appVersion)
                    .append("&uuid=")
                    .append(OpenUDIDManager.getID() == null ? "" : OpenUDIDManager.getID())
                    .append("&user_id=")
                    .append(LoginModel.getUserId())
                    .append("&user_token=")
                    .append(LoginModel.getUserToken())
                    .append("&timestamp=")
                    .append(System.currentTimeMillis());
        }
        return finalUrl.toString();
    }
}
