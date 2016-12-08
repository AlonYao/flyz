package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipXCReportActivity;
import com.appublisher.quizbank.common.vip.netdata.VipXCResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import com.appublisher.quizbank.model.business.KnowledgeTreeModel;
import com.appublisher.quizbank.model.netdata.hierarchy.HierarchyM;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 小班：行测报告页面
 */

public class VipXCReportModel extends VipBaseModel{

    public int mExerciseId;
    private VipXCReportActivity mView;
    private VipXCResp mResp;

    public VipXCReportModel(Context context) {
        super(context);
        mView = (VipXCReportActivity) context;
    }

    /**
     * 获取练习详情
     */
    public void getExerciseDetail() {
        mVipRequest.getExerciseDetail(mExerciseId);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            dealExerciseDetailResp(response);
        }
        super.requestCompleted(response, apiName);
    }

    private void dealExerciseDetailResp(JSONObject response) {
        mResp = GsonManager.getModel(response, VipXCResp.class);
        if (mResp == null || mResp.getResponse_code() != 1) return;
        mView.showCourseName(mResp.getCourse_name());
        mView.showExerciseName(mResp.getExercise_name());
        mView.showJYYS(timeFormat(mResp.getDuration()));

        int questionCount = mResp.getQuestion() == null ? 0 : mResp.getQuestion().size();

        VipXCResp.SummaryBean summaryBean = mResp.getSummary();
        if (summaryBean != null) {
            // 正确率
            float accuracy = summaryBean.getAccuracy();
            accuracy = accuracy * 100;
            mView.showAccuracy(String.valueOf(accuracy));
            // 平均速度
            mView.showSpeed(Utils.getSpeedByRound(summaryBean.getDuration(), questionCount));
            // 次序
            mView.showPosition(summaryBean.getPosition());
            // 实际用时
            mView.showSJYS(timeFormat(summaryBean.getDuration()));
            // 知识点
            ArrayList<VipXCResp.SummaryBean.CategoryInfoBean> categorys =
                    summaryBean.getCategory_info();
            if (categorys != null) {
                ArrayList<HierarchyM> hierarchys = new ArrayList<>();
                int size = categorys.size();
                for (int i = 0; i < size; i++) {
                    VipXCResp.SummaryBean.CategoryInfoBean category = categorys.get(i);
                    if (category == null) continue;
                    HierarchyM hierarchyM = GsonManager.getModel(
                            GsonManager.modelToString(category), HierarchyM.class);
                    hierarchys.add(hierarchyM);
                }
                KnowledgeTreeModel knowledgeTreeModel = new KnowledgeTreeModel(
                        mContext,
                        mView.getTreeContainer(),
                        KnowledgeTreeModel.TYPE_VIP_XC_REPORT);
                knowledgeTreeModel.showHierarchys(hierarchys);
            }
        }
    }

    private String timeFormat(int duration) {
        int min = duration / 60;
        int sec = duration % 60;
        return String.valueOf(min) + ":" + String.valueOf(sec);
    }

    public ArrayList<QuestionM> getAllQuestions() {
        if (mResp == null) return null;
        ArrayList<VipXCResp.QuestionBean> questionBeans = mResp.getQuestion();
        if (questionBeans == null) return null;

        ArrayList<QuestionM> questions = new ArrayList<>();
        for (VipXCResp.QuestionBean questionBean : questionBeans) {
            if (questionBean == null) continue;
            QuestionM questionM = GsonManager.getModel(
                    GsonManager.modelToString(questionBean), QuestionM.class);
            if (questionM == null) continue;
            questionM.setId(questionBean.getQuestion_id());
            questions.add(questionM);
        }
        return questions;
    }

    public ArrayList<AnswerM> getAllAnswers() {
        if (mResp == null) return null;
        ArrayList<VipXCResp.QuestionBean> questionBeans = mResp.getQuestion();
        if (questionBeans == null) return null;

        ArrayList<AnswerM> answers = new ArrayList<>();
        for (VipXCResp.QuestionBean questionBean : questionBeans) {
            if (questionBean == null) continue;
            VipXCResp.QuestionBean.UserAnswerBean userAnswerBean = questionBean.getUser_answer();
            if (userAnswerBean == null) continue;
            AnswerM answerM = new AnswerM();
            answerM.setId(userAnswerBean.getQuestion_id());
            answerM.setIs_collected(userAnswerBean.isIs_collected());
            answerM.setIs_right(userAnswerBean.isIs_right());
            answerM.setAnswer(userAnswerBean.getAnswer());
            answers.add(answerM);
        }
        return answers;
    }

    public ArrayList<QuestionM> getErrorQuestions() {
        if (mResp == null) return null;
        ArrayList<VipXCResp.QuestionBean> questionBeans = mResp.getQuestion();
        if (questionBeans == null) return null;

        ArrayList<QuestionM> questions = new ArrayList<>();
        for (VipXCResp.QuestionBean questionBean : questionBeans) {
            if (questionBean == null) continue;
            VipXCResp.QuestionBean.UserAnswerBean userAnswerBean = questionBean.getUser_answer();
            // 过滤错题
            if (userAnswerBean == null || userAnswerBean.isIs_right()) continue;
            QuestionM questionM = GsonManager.getModel(
                    GsonManager.modelToString(questionBean), QuestionM.class);
            if (questionM == null) continue;
            questionM.setId(questionBean.getQuestion_id());
            questions.add(questionM);
        }
        return questions;
    }

    public ArrayList<AnswerM> getErrorAnswers() {
        if (mResp == null) return null;
        ArrayList<VipXCResp.QuestionBean> questionBeans = mResp.getQuestion();
        if (questionBeans == null) return null;

        ArrayList<AnswerM> answers = new ArrayList<>();
        for (VipXCResp.QuestionBean questionBean : questionBeans) {
            if (questionBean == null) continue;
            VipXCResp.QuestionBean.UserAnswerBean userAnswerBean = questionBean.getUser_answer();
            // 过滤错题
            if (userAnswerBean == null || userAnswerBean.isIs_right()) continue;
            AnswerM answerM = new AnswerM();
            answerM.setId(userAnswerBean.getQuestion_id());
            answerM.setIs_collected(userAnswerBean.isIs_collected());
            answerM.setIs_right(userAnswerBean.isIs_right());
            answerM.setAnswer(userAnswerBean.getAnswer());
            answers.add(answerM);
        }
        return answers;
    }
}
