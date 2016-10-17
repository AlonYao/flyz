package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipHPTSActivity;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

/**
 * 小班：互评提升
 */

public class VipHPTSModel extends VipBaseModel {

    private VipHPTSActivity mView;
    public int mExerciseId;

    public VipHPTSModel(Context context) {
        super(context);
        mView = (VipHPTSActivity) context;
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
        } else if (VipRequest.SUBMIT.equals(apiName)) {
            mView.showLoading();
            getExerciseDetail();
        }
        super.requestCompleted(response, apiName);
    }

    /**
     * 练习详情回调
     * @param response JSONObject
     */
    private void dealExerciseDetailResp(JSONObject response) {
        VipHPTSResp resp = GsonManager.getModel(response, VipHPTSResp.class);
        mView.showContent(resp);
    }

}
