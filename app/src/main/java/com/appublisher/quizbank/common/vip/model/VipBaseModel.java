package com.appublisher.quizbank.common.vip.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.YaoguoUploadManager;
import com.appublisher.lib_basic.volley.ApiConstants;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * 小班模块管理类
 */
public class VipBaseModel implements RequestCallback{

    Context mContext;
    VipRequest mVipRequest;
    private ProgressDialog mProgressDialog;
    private String mSubmitImgUrl;
    private int mCurUpLoadIndex;

    public static final int CAMERA_REQUEST_CODE = 10;
    public static final int GALLERY_REQUEST_CODE = 11;
    public static final String CUSTOM_STYLE = "<style type='text/css'>html, " +
            "body {width:100%;height:100%;margin: " +
            "0px;padding: 0px}</style>";
    static final String ZJZD = "zjzd";

    VipBaseModel(Context context) {
        mContext = context;
        mVipRequest = new VipRequest(mContext, this);
    }

    interface UpLoadListener {
        void onComplete(String submitImgUrl);
    }

    /**
     * 跳转至拍照
     * @param max_length 最大能获取的图片数量
     */
    public void toCamera(int max_length) {
        MultiImageSelector.create()
                .count(max_length)
                .start((Activity) mContext, CAMERA_REQUEST_CODE);
    }

    void upload(final int exericiseId,
                final String type,
                final ArrayList<String> paths,
                final UpLoadListener listener) {
        if (paths == null || paths.size() == 0 || mCurUpLoadIndex >= paths.size()) return;
        if (mProgressDialog == null)
            mProgressDialog = YaoguoUploadManager.getProgressDialog(mContext);
        mProgressDialog.setTitle(mCurUpLoadIndex + 1 + "/" + paths.size());
        mProgressDialog.show();

        String localPath = paths.get(mCurUpLoadIndex);
        String savePath = getSavePath(exericiseId, type);
        if (mCurUpLoadIndex == 0) mSubmitImgUrl = "";

        YaoguoUploadManager.blockUpload(localPath, savePath,
                new YaoguoUploadManager.CompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess, String result, String url) {
                        if (isSuccess) {
                            mCurUpLoadIndex++;
                            if (mCurUpLoadIndex >= paths.size()) {
                                // 上传完成
                                mSubmitImgUrl = mSubmitImgUrl + url;
                                listener.onComplete(mSubmitImgUrl);
                                mProgressDialog.cancel();
                            } else {
                                // 上传未完成
                                mSubmitImgUrl = mSubmitImgUrl + url + "#";
                                upload(exericiseId, type, paths, listener);
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

    /**
     * 获取上传保存路径
     * @param exericiseId 练习id
     * @param type 练习类型
     * @return String
     */
    private String getSavePath(int exericiseId, String type) {
        String folderName;
        if (ZJZD.equals(type)) {
            folderName = "ziji";
        } else {
            folderName = "composition";
        }
        if (ApiConstants.baseUrl.contains("dev")) {
            return "/xiaoban/" + folderName + "/"
                    + "4791/"
                    + exericiseId + "_"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg";
        } else {
            return "/xiaoban/" + folderName + "/"
                    + LoginModel.getUserId() + "/"
                    + exericiseId + "_"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg";
        }
    }

    public void submit(VipSubmitEntity entity) {
        mVipRequest.submit(VipParamBuilder.submit(entity));
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (mContext instanceof VipBaseActivity) {
            ((VipBaseActivity) mContext).hideLoading();
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        if (mContext instanceof VipBaseActivity) {
            ((VipBaseActivity) mContext).hideLoading();
        }
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (mContext instanceof VipBaseActivity) {
            ((VipBaseActivity) mContext).hideLoading();
        }
    }
}
