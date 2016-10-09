package com.appublisher.quizbank.common.vip.model;

import android.app.ProgressDialog;
import android.content.Context;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.YaoguoUploadManager;
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
    public static final String INTENT_EXERCISEID = "exercise_id";

    private int mExerciseId;
    private int mStatus;
    private VipZJZDActivity mView;
    private String mExampleUrl;
    private boolean mCanSubmit;
    private ArrayList<String> mPaths;
    private ProgressDialog mProgressDialog;
    private String mSubmitImgUrl;

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
        mStatus = resp.getStatus();
        String statusText = resp.getStatus_text();
        mView.showStatus(mStatus, statusText);

        VipZJZDResp.QuestionBean question = resp.getQuestion();
        if (question != null) {
            // 材料
            mView.showTvMaterial(question.getContent());
            // 作业示例
            mExampleUrl = question.getImage_url();
            mView.showIvExample(mExampleUrl + "!/fw/" + PIC_SIDE);
        }

        // 我的作业处理
        VipZJZDResp.UserAnswerBean userAnswer = resp.getUser_answer();
        if (mCanSubmit) {
            mView.showMyJob(null, VipZJZDActivity.FILE, MAX_LENGTH);
        } else {
            mPaths = new ArrayList<>();
            mPaths.add(userAnswer.getImage_url());
            mView.showMyJob(mPaths, VipZJZDActivity.URL, MAX_LENGTH);
        }
    }

    public void submit() {
        if (mPaths == null || mPaths.size() == 0) return;
        mSubmitImgUrl = "";
        upload(mPaths, getSavePath(), 0);
    }

    private void upload(final ArrayList<String> paths , String savePath, final int index) {
        if (paths == null || paths.size() == 0 || index >= paths.size()) return;
        if (mProgressDialog == null)
            mProgressDialog = YaoguoUploadManager.getProgressDialog(mContext);
        mProgressDialog.setTitle(index + 1 + "/" + paths.size());
        mProgressDialog.show();

        String localPath = paths.get(index);
        YaoguoUploadManager.blockUpload(localPath, savePath,
                new YaoguoUploadManager.CompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result, String url) {
                        if (isSuccess) {
                            mSubmitImgUrl = mSubmitImgUrl + url;
                            if (index == paths.size()) {
                                mProgressDialog.cancel();
                            } else {
                                upload(paths, getSavePath(), index + 1);
                            }
                        } else {
                            mProgressDialog.cancel();
                            ToastManager.showToast(mContext, "上传失败，请重试……");
                        }
                    }
                },
                new YaoguoUploadManager.ProgressListener() {
                    @Override
                    public void onRequestProgress(long bytesWrite, long contentLength) {
                        mProgressDialog.setProgress((int) ((100 * bytesWrite) / contentLength));
                    }
                });
    }

    private String getSavePath() {
        return "/huaxiao_test.jpg";
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
        return mCanSubmit && (mPaths != null && mPaths.size() > 0);
    }

    public ArrayList<String> getPaths() {
        return mPaths;
    }

    public void setPaths(ArrayList<String> mPaths) {
        this.mPaths = mPaths;
    }
}
