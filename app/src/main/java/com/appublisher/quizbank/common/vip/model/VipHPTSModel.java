package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipHPTSActivity;
import com.appublisher.quizbank.common.vip.netdata.VipHPTSResp;
import com.appublisher.quizbank.common.vip.netdata.VipSubmitResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 小班：互评提升
 */

public class VipHPTSModel extends VipBaseModel {

    private VipHPTSActivity mView;
    public int mExerciseId;

    // Umeng
    private boolean mUMIsPostType = false;

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
            VipSubmitResp resp = GsonManager.getModel(response, VipSubmitResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
                mView.showLoading();
                getExerciseDetail();

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "1");
                UmengManager.onEvent(mContext, "Huping", map);

            } else {
                mView.showSubmitErrorToast();
            }
        }
        super.requestCompleted(response, apiName);
    }

    /**
     * 练习详情回调
     * @param response JSONObject
     */
    private void dealExerciseDetailResp(JSONObject response) {
        VipHPTSResp resp = GsonManager.getModel(response, VipHPTSResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        mView.showContent(resp);

        // Umeng
        if (!mUMIsPostType) {
            String umStatus;
            int status = resp.getStatus();
            if (status == 1 || status == 3 || status == 5) {
                umStatus = "1";
            } else {
                umStatus = "0";
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("Type", umStatus);
            UmengManager.onEvent(mContext, "Huping", map);
            mUMIsPostType = true;
        }
    }

}
