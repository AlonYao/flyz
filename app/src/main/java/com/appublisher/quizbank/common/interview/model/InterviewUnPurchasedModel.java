package com.appublisher.quizbank.common.interview.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.YaoguoUploadManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.fragment.InterviewUnPurchasedFragment;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.network.InterviewParamBuilder;
import com.appublisher.quizbank.model.netdata.CommonResp;

import org.json.JSONObject;

import java.util.List;


/*
*  本model为未付费页面:InterviewUnPurchasedFragment中Model
*  主要处理:
*           1.两种弹窗的背景及弹窗中的item点击事件
*           2.支付成功和支付失败的处理
* */
public class InterviewUnPurchasedModel extends InterviewDetailModel{

    private Context mContext;
    private InterviewUnPurchasedFragment mFragment;

    private ProgressDialog mProgressDialog;
    private InterviewPaperDetailActivity mActivity;
    private boolean isAnswer;
    private boolean isCollect;
    private int mViewpgId;
    private InterviewPaperDetailResp.QuestionsBean mQuestionbean;
    private List<InterviewPaperDetailResp.QuestionsBean> list;
    private String type;
    private String type1;



    public InterviewUnPurchasedModel(Context context) {
        super(context);
        mContext = context;

    }
    /*
    *  显示未付费页面的dailog
    *  jump_url:为跳转到支付页面的url
    * */
    public static void showNoAnswerDialog(final InterviewPaperDetailActivity mActivity, String jump_url){
        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();


        Window mWindow = mAalertDialog.getWindow();
        mWindow.setContentView(R.layout.interview_popupwindow_reminder);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        mWindow.getDecorView().setPadding(0,0,0,0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        // 获取控件
        TextView goAnswer = (TextView) mWindow.findViewById(R.id.go_answer);
        TextView payOne = (TextView) mWindow.findViewById(R.id.pay_one);
        TextView payNine = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancle = (TextView) mWindow.findViewById(R.id.cancle);



        // 处理点击事件
        goAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
        payOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastManager.showToast(mActivity,"去支付一分钱页面");
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
        payNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastManager.showToast(mActivity,"去支付9元页面 ");
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
    }

    /*
   *   创建开启完整版的dailog
   * */
    public static void showOpenFullDialog(final InterviewPaperDetailActivity mActivity, String jump_url) {
        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        mWindow.setContentView(R.layout.interview_popupwindow_openfull);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        mWindow.getDecorView().setPadding(0, 0, 0, 0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        // 获取控件
        TextView payNine = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancle = (TextView) mWindow.findViewById(R.id.cancle);

        // 点击事件
        payNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastManager.showToast(mActivity, "去支付9元页面 ");
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
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
    *   提交的弹窗
    * */
    public void showSubmitAnswerAlert(final InterviewPaperDetailActivity activity , String fileDir, InterviewPaperDetailResp.QuestionsBean mQuestionbean, final String durationTime,String questiontype){
        mActivity = activity;
        final String type = questiontype;                 // 问题的类型guokao/teacher/category/before
        String userId = LoginModel.getUserId();
        final int question_Id = mQuestionbean.getId();
        String questionId = String.valueOf(question_Id);
        final int duration = Integer.parseInt(durationTime);

        String savePath = "/quizbank_interview/" + userId + "/" + questionId +".amr" ;
        if (mProgressDialog == null) {
            mProgressDialog = YaoguoUploadManager.getProgressDialog(mActivity);
        }

        mProgressDialog.show();
        YaoguoUploadManager.CompleteListener completeListener = new YaoguoUploadManager.CompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result, String url) {
                if(isSuccess){
                    mActivity.showLoading();
                    // 重新进行数据请求:刷新adapter
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
    *   录音提交后:返回的信息
    * */
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("submit_record".equals(apiName)) {

            CommonResp resp = GsonManager.getModel(response, CommonResp.class);

            if (resp != null && resp.getResponse_code() == 1) {
                Logger.e("录音提交成功");
                //获取数据
                // 在此需要在封装成一次bean对象
                mActivity.getData();
            } else {
                Logger.e("录音提交失败");
                ToastManager.showToast(mActivity,"刷新失败");
            }
            mActivity.hideLoading();
        }else if("update_collected_status".equals(apiName)){    //  收藏后的回调
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
                Logger.e("收藏的回调成功");

            } else {

                ToastManager.showToast(mActivity,"刷新失败");
            }
        }

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

    public int getCurQuestionId(int position,InterviewPaperDetailActivity activity) {

        InterviewPaperDetailActivity mActivity = activity;
        List<InterviewPaperDetailResp.QuestionsBean> list = mActivity.list;

        if (list == null || position < 0 || position >= list.size()) return 0;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);
        if (mBean == null) return 0;
        return mBean.getId();
    }
}
