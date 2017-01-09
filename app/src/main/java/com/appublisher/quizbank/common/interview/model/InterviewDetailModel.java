package com.appublisher.quizbank.common.interview.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.YaoguoUploadManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewParamBuilder;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.model.netdata.CommonResp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewDetailModel implements RequestCallback {

    private Context mContext;
    public InterviewRequest mRequest;
    private InterviewPaperDetailActivity mActivity;
    private ProgressDialog mProgressDialog;
    private String type;

    public InterviewDetailModel(Context context) {
        mContext = context;
        mRequest = new InterviewRequest(context, this);
    }


    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("submit_record".equals(apiName)) {

            CommonResp resp = GsonManager.getModel(response, CommonResp.class);

            if (resp != null && resp.getResponse_code() == 1) {
                mActivity.setCanBack(0);
                mActivity.getData();

            } else {
                ToastManager.showToast(mActivity,"刷新失败");
            }
            mActivity.hideLoading();
        }

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).hideLoading();
    }

    /*
   *   提交的弹窗
   * */
    public void showSubmitAnswerAlert(final InterviewPaperDetailActivity activity , String fileDir, InterviewPaperDetailResp.QuestionsBean mQuestionbean, final String durationTime, String questiontype){
        mActivity = activity;
        final String type = questiontype;                 // 问题的类型
        String userId = LoginModel.getUserId();
        final int question_Id = mQuestionbean.getId();
        String questionId = String.valueOf(question_Id);
        final int duration = Integer.parseInt(durationTime);

        String savePath = "/yaoguo_interview/" + userId + "/" + questionId +".amr" ;
        if (mProgressDialog == null) {
            mProgressDialog = YaoguoUploadManager.getProgressDialog(mActivity);
        }

        mProgressDialog.show();
        YaoguoUploadManager.CompleteListener completeListener = new YaoguoUploadManager.CompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result, String url) {
                if(isSuccess){
                    mActivity.showLoading();
                    ToastManager.showToast(mActivity,"上传成功 ");
                    mProgressDialog.cancel();
                    mRequest.submitRecord(InterviewParamBuilder.submitPaper(question_Id,url,duration,type));    //提交录音数据

                }else{
                    mProgressDialog.cancel();
                    ToastManager.showToast(mActivity, "上传失败，请重试……");
                }
            }
        };
        YaoguoUploadManager.ProgressListener progressListener = new YaoguoUploadManager.ProgressListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength) {
                mProgressDialog.setProgress((int) ((100 * bytesWrite) / contentLength));
            }
        };
        YaoguoUploadManager.blockUpload(fileDir,savePath,completeListener,progressListener);
    }

    /*
   *  创建重录页面dialog
   * */
    public static void showBackPressedDailog(final InterviewPaperDetailActivity mActivity){
        new AlertDialog.Builder(mActivity)
                .setMessage("放弃本次作答")
                .setTitle("提示")
                .setPositiveButton("再想想",
                        new DialogInterface.OnClickListener() {// 确定
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ToastManager.showToast(mActivity,"再按一次退出");
                                // 返回上一级
                                mActivity.setCanBack(0);
                            }
                        })
                .create().show();
    }

    /*
   *   检查menu是否为收藏状态:需要获取数据:由fragment传进来
   * */
    public boolean getIsCollected(int position, InterviewPaperDetailActivity activity) {
        InterviewPaperDetailActivity mActivity = activity;
        List<InterviewPaperDetailResp.QuestionsBean> list = mActivity.list;
        if (list == null) return false;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);

        if(mBean.getIs_collected()){
            return true;
        }else{
            return false;
        }

    }
    /*
   *   检查menu是否为收藏状态
   * */
    public boolean getIsAnswer(int position, InterviewPaperDetailActivity activity) {

        InterviewPaperDetailActivity mActivity = activity;
        List<InterviewPaperDetailResp.QuestionsBean> list = mActivity.list;
        if (list == null) return false;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);

        if(mBean.getUser_audio() != null && mBean.getUser_audio().length() > 0 ){
            return true;
        }else{
            return  false;
        }

    }
    /*
    *   设置menu的状态 :由fragment传入数据,由activity来判断
    * */
    public void setCollected(int position, boolean isCollected, InterviewPaperDetailActivity activity) {
        InterviewPaperDetailActivity mActivity = activity;
        List<InterviewPaperDetailResp.QuestionsBean> list = mActivity.list;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);

        if(isCollected){   // 将收藏变为true
            mBean.setIs_collected(true);
            type = "collect";
        }else {
            mBean.setIs_collected(false);
            type = "cancel_collect";
        }
        mActivity.list.set(position, mBean);        // 刷新list
        // 提交数据
        mRequest.collectQuestion(InterviewParamBuilder.submitCollectStated(type,mBean.getId()));     // 向服务器提交收藏状态

        // 刷新menu
        if (mContext instanceof InterviewPaperDetailActivity) {
            ((InterviewPaperDetailActivity) mContext).invalidateOptionsMenu();    // 刷新menu
        }
    }
}
