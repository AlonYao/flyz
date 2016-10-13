package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipDTTPActivity;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

/**
 * 小班：单题突破
 */

public class VipDTTPModel extends VipBaseModel{

    private VipDTTPActivity mView;

    public VipDTTPModel(Context context) {
        super(context);
        mView = (VipDTTPActivity) context;
    }

    /**
     * 获取练习详情
     */
    public void getExerciseDetail() {
//        mVipRequest.getExerciseDetail(mExerciseId);
        mVipRequest.getExerciseDetail(334);
    }

    /**
     * 练习详情回调
     * @param response JSONObject
     */
    private void dealExerciseDetailResp(JSONObject response) {
        VipDTTPResp resp = GsonManager.getModel(response, VipDTTPResp.class);
        mView.showContent(resp);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            dealExerciseDetailResp(response);
        }
        super.requestCompleted(response, apiName);
    }

}
