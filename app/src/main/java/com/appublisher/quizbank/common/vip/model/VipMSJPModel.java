package com.appublisher.quizbank.common.vip.model;

import android.content.Context;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;
import com.appublisher.quizbank.common.vip.netdata.VipSubmitResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 小班：名师精批
 */
public class VipMSJPModel extends VipBaseModel {

    public int mExerciseId;
    private VipMSJPActivity mView;

    // Umeng
    private String mUMDone = "0";
    public String mUMEntry;
    public long mUMBegin;
    public int mUMSwitch = 0;

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
        } else if (VipRequest.SUBMIT.equals(apiName)) {
            VipSubmitResp resp = GsonManager.getModel(response, VipSubmitResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
                mView.showLoading();
                getExerciseDetail();
                // Umeng
                mUMDone = "1";
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
            // Umeng
            int status = resp.getStatus();
            if (status == 1 || status == 5) {
                mUMDone = "1";
            }
        }
        mView.showContent(resp);
    }

    /**
     * 提交友盟统计数据
     */
    public void sendToUmeng() {
        HashMap<String, String> map = new HashMap<>();
        int dur = (int) ((System.currentTimeMillis() - mUMBegin) / 1000);
        map.put("Done", mUMDone);
        map.put("Entry", mUMEntry);
        map.put("Switch", String.valueOf(mUMSwitch));
        UmengManager.onEventValue(mContext, "Jingpi", map, dur);
    }
}
