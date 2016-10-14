package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 小班：名师精批
 */
public class VipMSJPModel extends VipBaseModel {

    public int mExerciseId;
    private VipMSJPActivity mView;

    public VipMSJPModel(Context context) {
        super(context);
        mView = (VipMSJPActivity) context;
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

    /**
     * 练习详情回调
     * @param response JSONObject
     */
    private void dealExerciseDetailResp(JSONObject response) {
        VipMSJPResp resp = GsonManager.getModel(response, VipMSJPResp.class);
        // 约束作业处理
        if (resp != null) {
            ArrayList<VipMSJPResp.PreExercisesBean> preExercises = resp.getPre_exercises();
            if (preExercises != null && preExercises.size() != 0) {
                String nameList = "";
                for (VipMSJPResp.PreExercisesBean preExercise : preExercises) {
                    if (preExercise == null) continue;
                    nameList = nameList + preExercise.getExercise_name() + "\n";
                }
                if (nameList.length() > 0) {
                    mView.showPreExercisesAlert(nameList);
                    return;
                }
            }
        }
        mView.showContent(resp);
    }
}
