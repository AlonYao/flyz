package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipZJZDActivity;
import com.appublisher.quizbank.common.vip.netdata.VipZJZDResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 小班：字迹诊断模块
 */
public class VipZJZDModel extends VipBaseModel {

    private static final int PIC_SIDE = 147;
    public static final int MAX_LENGTH = 1;

    private VipZJZDActivity mView;
    private String mExampleUrl;
    private int mQuestionId;

    public boolean mCanSubmit;
    public ArrayList<String> mPaths;
    public int mExerciseId;

    public VipZJZDModel(Context context) {
        super(context);
        mView = (VipZJZDActivity) context;
    }

    /**
     * 获取练习详情
     */
    public void getExerciseDetail() {
        mVipRequest.getExerciseDetail(mExerciseId);
    }

    /**
     * 处理练习详情接口回调
     * @param response JSONObject
     */
    private void dealExerciseDetailResp(JSONObject response) {
        VipZJZDResp resp = GsonManager.getModel(response, VipZJZDResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        // 提交按钮
        mCanSubmit = resp.isCan_submit();
        mView.showSubmitBtn(mCanSubmit);

        // 状态
        int status = resp.getStatus();
        String statusText = resp.getStatus_text();
        mView.showStatus(status, statusText);

        VipZJZDResp.QuestionBean question = resp.getQuestion();
        if (question != null) {
            // 更新questionId
            mQuestionId = question.getQuestion_id();
            // 材料
            mView.showTvMaterial(question.getContent());
            // 作业示例
            mExampleUrl = question.getImage_url();
            mView.showIvExample(mExampleUrl + "!/fw/" + PIC_SIDE);
        }

        // 我的作业处理
        VipZJZDResp.UserAnswerBean userAnswer = resp.getUser_answer();
        if (userAnswer != null) {
            if (mCanSubmit) {
                mView.showMyJob(null, VipZJZDActivity.FILE, MAX_LENGTH);
            } else {
                mPaths = new ArrayList<>();
                mPaths.add(userAnswer.getImage_url());
                mView.showMyJob(mPaths, VipZJZDActivity.URL, MAX_LENGTH);
            }

            // 被驳回状态
            if (status == 4) {
                mView.showRejectAlert(userAnswer.getReview_postil(), userAnswer.getSubmit_time());
            }
        }
    }

    /**
     * 提交
     */
    public void submit() {
        upload(mExerciseId, ZJZD, mPaths, new UpLoadListener() {
            @Override
            public void onComplete(String submitImgUrl) {
                VipSubmitEntity entity = new VipSubmitEntity();
                entity.exercise_id = mExerciseId;
                entity.question_id = mQuestionId;
                entity.image_url = submitImgUrl;
                mView.showLoading();
                submit(entity);
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            dealExerciseDetailResp(response);
        } else if (VipRequest.SUBMIT.equals(apiName)) {
            mView.showLoading();
            getExerciseDetail();
        }
        super.requestCompleted(response, apiName);
    }

    /** GET & SET **/

    public int getExerciseId() {
        return mExerciseId;
    }

    public void setExerciseId(int mExerciseId) {
        this.mExerciseId = mExerciseId;
    }

    public String getExampleUrl() {
        return mExampleUrl;
    }

    public boolean isCanSubmit() {
        return mCanSubmit && (mPaths != null && mPaths.size() > 0);
    }

    public ArrayList<String> getPaths() {
        return mPaths;
    }

    public void setPaths(ArrayList<String> mPaths) {
        this.mPaths = mPaths;
    }
}
