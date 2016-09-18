package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipZJZDActivity;
import com.appublisher.quizbank.common.vip.netdata.VipZJZDResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

/**
 * 小班：字迹诊断模块
 */
public class VipZJZDModel extends VipManager{

    private static final int PIC_SIDE = 147;

    public static final int MAX_LENGTH = 1;
    public static final String INTENT_EXERCISEID = "exercise_id";

    private int mExerciseId;
    private VipZJZDActivity mView;
    private String mExampleUrl;
    private String mMyJobPicPath;
    private String mMyJobPicUrl;
    private boolean mCanSubmit;

    public VipZJZDModel(Context context) {
        super(context);
        mView = (VipZJZDActivity) context;
    }

//    /**
//     * 跳转至拍照或相册
//     */
//    public void toCamera() {
//        toCamera(1);
//    }

    /**
     * 获取缩略图
     * @param data 图片地址
     * @return Bitmap
     */
    public Bitmap getThumbnail(Intent data) {
        mMyJobPicPath = getPathByIndex(data, 0);
        return getThumbnail(mMyJobPicPath, PIC_SIDE, PIC_SIDE);
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

        mCanSubmit = resp.isCan_submit();

        VipZJZDResp.QuestionBean question = resp.getQuestion();
        if (question != null) {
            // 材料
            mView.showTvMaterial(question.getContent());
            // 作业示例
            mExampleUrl = question.getImage_url();
            mView.showIvExample(mExampleUrl + "!/fw/" + PIC_SIDE);
        }

        VipZJZDResp.UserAnswerBean userAnswer = resp.getUser_answer();
        if (userAnswer != null) {
            mMyJobPicUrl = userAnswer.getImage_url();
        }
    }

    /**
     * 是否可以添加用户照片
     * @return boolean
     */
    public boolean isCanAdd() {
        return mCanSubmit && (mMyJobPicPath == null || mMyJobPicPath.length() == 0);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            dealExerciseDetailResp(response);
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
        return mCanSubmit;
    }

    public String getMyJobPicUrl() {
        return mMyJobPicUrl;
    }

    public String getMyJobPicPath() {
        return mMyJobPicPath;
    }
}
