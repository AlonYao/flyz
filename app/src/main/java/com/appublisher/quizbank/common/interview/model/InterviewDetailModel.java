package com.appublisher.quizbank.common.interview.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.YaoguoUploadManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.lib_pay.PayListener;
import com.appublisher.lib_pay.PayModel;
import com.appublisher.lib_pay.ProductEntity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.common.interview.netdata.InterviewTeacherRemarkNumResp;
import com.appublisher.quizbank.common.interview.network.InterviewParamBuilder;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
import com.appublisher.quizbank.common.interview.view.InterviewDetailBaseFragmentCallBak;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.network.QApiConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class InterviewDetailModel extends InterviewModel implements RequestCallback {

    public InterviewRequest mRequest;
    private InterviewPaperDetailActivity mActivity;
    private ProgressDialog mProgressDialog;
    private final InterviewDetailBaseFragmentCallBak mInterfaceViewCallBak;

    public InterviewDetailModel(Context context, InterviewDetailBaseFragmentCallBak interviewDetailBaseFragmentViewCallBak) {
        super();
        if (context instanceof InterviewPaperDetailActivity) {
            mActivity = (InterviewPaperDetailActivity) context;
        }
        mRequest = new InterviewRequest(context, this);
        mInterfaceViewCallBak = interviewDetailBaseFragmentViewCallBak;
    }

    /*
   *   提交录音
   * */
    public void popupSubmitAnswerProgressBar(String fileDir, InterviewPaperDetailResp.QuestionsBean questionBean, final String durationTime,final String questionType){
        String userId = LoginModel.getUserId();
        final int question_Id = questionBean.getId();
        final int duration = Integer.parseInt(durationTime);

        String savePath = "/yaoguo_interview/" + userId + "/" + String.valueOf(questionBean.getId()) +".amr" ;
        if (QApiConstants.baseUrl.contains("dev")) {
            savePath = "/dev/yaoguo_interview/" + userId + "/" + String.valueOf(questionBean.getId()) +".amr" ;
        }
        if (mProgressDialog == null) {
            mProgressDialog = YaoguoUploadManager.getProgressDialog(mActivity);
        }
        mProgressDialog.show();

        YaoguoUploadManager.CompleteListener completeListener = new YaoguoUploadManager.CompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result, String url) {
                if(isSuccess){
                    mActivity.showLoading();
                    mProgressDialog.cancel();
                    ToastManager.showToast(mActivity,"上传成功 ");
                    mRequest.submitRecord(InterviewParamBuilder.submitPaper(question_Id,url,duration,questionType));    //提交录音数据

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
        YaoguoUploadManager.formUpload(fileDir,savePath,completeListener,progressListener);
    }

    /*
   *  创建重录页面弹窗
   * */
    public static void showBackPressedAlert(final InterviewPaperDetailActivity activity){
        if (activity.isFinishing()) return;
        new AlertDialog.Builder(activity)
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
                                // 判断此时是否有播放的题目
                                if(activity.mMediaRecorderManager != null){
                                    activity.mMediaRecorderManager.stopPlay();
                                }
                                // 返回上一级
                                activity.finish();
                            }
                        })
                .create().show();
    }

    /*
    *   创建停止播放语音的弹窗提示
    * */
    public static void showStopMediaPlayingAlert(final InterviewPaperDetailActivity activity){
        //  弹窗提示
        if (activity.isFinishing()) return;
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        alertDialog.show();
        Window mWindow = alertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_reminder_stopplaying_media);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        lp.width = (int)(metrics.widthPixels * 0.8);
        lp.height = (int)(metrics.heightPixels * 0.35);
        mWindow.setAttributes(lp);
        TextView stopPlaying = (TextView) mWindow.findViewById(R.id.stop_playing);
        TextView continuePlaying = (TextView) mWindow.findViewById(R.id.continue_playing);
        final CheckBox checkBox = (CheckBox) mWindow.findViewById(R.id.stop_playing_checkbox);

        stopPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.mMediaRecorderManager.stopPlay();
                alertDialog.dismiss();
                // 返回上一级
                activity.finish();
                if(checkBox.isChecked()){
                    // 记录状态
                    SharedPreferences shp = getInterviewSharedPreferences(activity);
                    SharedPreferences.Editor edit = shp.edit();
                    edit.putBoolean("isFirstCheckBox", false);
                    edit.apply();
                }
            }
        });
        continuePlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if(checkBox.isChecked()){
                    // 记录状态
                    SharedPreferences shp = getInterviewSharedPreferences(activity);
                    SharedPreferences.Editor edit = shp.edit();
                    edit.putBoolean("isFirstCheckBox", false);
                    edit.apply();
                }
            }
        });

    }
    /*
   *   检查menu是否为收藏状态:需要获取数据:由fragment传进来
   * */
    public boolean getIsCollected(int position) {
        List<InterviewPaperDetailResp.QuestionsBean> list = mActivity.mQuestionsBeanList;
        if(list == null || list.size()<= 0 || position > list.size() || position < 0) return false;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);
        return mBean != null && mBean.getIs_collected();
    }
    /*
    *   设置menu的状态 :由fragment传入数据,由activity来判断
    * */
    public void setCollected(int position, boolean isCollected) {         // question_type 数据源来源
        if (mActivity == null) return;
        List<InterviewPaperDetailResp.QuestionsBean> list =  mActivity.mQuestionsBeanList;
        if(list == null || list.size()<= 0 || position > list.size() || position < 0) return;
        InterviewPaperDetailResp.QuestionsBean mBean = list.get(position);
        if(mBean == null ) return;
        String type;
        if(isCollected){   // 将收藏变为true
            mBean.setIs_collected(true);
            type = "collect";
        }else {
            mBean.setIs_collected(false);
            type = "cancel_collect";
        }
        mActivity.mQuestionsBeanList.set(position, mBean);        // 刷新list
        // 提交数据
        mRequest.collectQuestion(InterviewParamBuilder.submitCollectStated(type,mBean.getId()));     // 向服务器提交收藏状态

        // 刷新menu
        if (mActivity instanceof InterviewPaperDetailActivity) {
            mActivity.invalidateOptionsMenu();    // 刷新menu
        }
    }
    /*
  *  显示未付费页面的dailog
  * */
    public void showNoAnswerDialog(){
        if (mActivity == null) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_reminder);
        setWindowBackground(mWindow);

        TextView goAnswer = (TextView) mWindow.findViewById(R.id.go_answer);
        TextView paySingle = (TextView) mWindow.findViewById(R.id.pay_one);
        TextView payAll = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancle = (TextView) mWindow.findViewById(R.id.cancle);

        // 0.01元处理
        final InterviewPaperDetailResp.SingleAudioBean singleAudioBean = mActivity.getSingleAudioBean();
        if (singleAudioBean != null && singleAudioBean.is_purchased()) {
            paySingle.setVisibility(View.GONE);
        } else {
            paySingle.setTextColor(ContextCompat.getColor(mActivity, R.color.common_text));
            String singlePayText = "付 ¥ 0.01, 获取本题解析(此体验机会仅限一次)";
            if (singleAudioBean != null) {
                singlePayText = "付 ¥ " + String.valueOf(singleAudioBean.getPrice())
                        + ", 获取本题解析(此体验机会仅限一次)";
            }
            paySingle.setText(singlePayText);

            paySingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 0.01元支付
                    if (singleAudioBean == null || singleAudioBean.is_purchased()) return;

                    ProductEntity entity = new ProductEntity();
                    entity.setProduct_id(String.valueOf(singleAudioBean.getProduct_id()));
                    entity.setProduct_type(singleAudioBean.getProduct_type());
                    entity.setProduct_count(String.valueOf(1));
                    entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                    showChoicePay(entity);

                    mAalertDialog.dismiss();
                    mActivity.setCanBack(0);              // 可以按返回键

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "2");
                    UmengManager.onEvent(mActivity, "InterviewAnswer", map);
                }
            });
        }

        // 处理点击事件
        goAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "1");
                UmengManager.onEvent(mActivity, "InterviewAnswer", map);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });

        String allPayText = "付 ¥ 9, 解锁本题库全部解析";
        final InterviewPaperDetailResp.AllAudioBean allAudioBean = mActivity.getAllAudioBean();
        if (allAudioBean != null) {
            allPayText = "付 ¥ " + String.valueOf(allAudioBean.getPrice()) + ", 解锁本题库全部解析";
        }
        payAll.setText(allPayText);

        payAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 9元支付
                if (allAudioBean == null || allAudioBean.is_purchased()) return;

                ProductEntity entity = new ProductEntity();
                entity.setProduct_id(String.valueOf(allAudioBean.getProduct_id()));
                entity.setProduct_type(allAudioBean.getProduct_type());
                entity.setProduct_count(String.valueOf(1));
                entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                showChoicePay(entity);

                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "3");
                UmengManager.onEvent(mActivity, "InterviewAnswer", map);
            }
        });
    }

    /*
   *   创建开启完整版的dailog
   * */
    public void showOpenFullDialog() {

        if (mActivity == null) return;

        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        alertDialog.show();

        Window mWindow = alertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_openfull);
        setWindowBackground(mWindow);

        TextView payNine = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancel = (TextView) mWindow.findViewById(R.id.cancel);

        String allPayText = "付 ¥ 9, 解锁本题库全部解析";
        final InterviewPaperDetailResp.AllAudioBean bean = mActivity.getAllAudioBean();
        if (bean != null) {
            allPayText = "付 ¥ " + String.valueOf(bean.getPrice()) + ", 解锁本题库全部解析";
        }
        payNine.setText(allPayText);

        // 点击事件
        payNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 9元支付
                if (bean == null || bean.is_purchased()) return;

                ProductEntity entity = new ProductEntity();
                entity.setProduct_id(String.valueOf(bean.getProduct_id()));
                entity.setProduct_type(bean.getProduct_type());
                entity.setProduct_count(String.valueOf(1));
                entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                showChoicePay(entity);

                alertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "1");
                UmengManager.onEvent(mActivity, "InterviewVip", map);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
    }

    private void setWindowBackground(Window window){
        window.setBackgroundDrawableResource(R.color.transparency);   //背景色
        window.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        window.getDecorView().setPadding(0, 0, 0, 0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

    }

    /**
     * 选择支付方式
     */
    private void showChoicePay(final ProductEntity entity) {
        if (entity == null || mActivity == null || mActivity.isFinishing()) return;
        final AlertDialog mAlertDialog = new AlertDialog.Builder(mActivity).create();
        mAlertDialog.setCancelable(true);
        mAlertDialog.show();

        Window window = mAlertDialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.alert_choice_pay);
        window.setBackgroundDrawableResource(R.color.transparency);

        final CheckBox aliPay = (CheckBox) window.findViewById(R.id.aliPay);
        final CheckBox wxPay = (CheckBox) window.findViewById(R.id.wxPay);

        aliPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    wxPay.setChecked(false);
            }
        });

        wxPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    aliPay.setChecked(false);
            }
        });

        Button payBtn = (Button) window.findViewById(R.id.pay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aliPay.isChecked() && !wxPay.isChecked()) {
                    ToastManager.showToast(mActivity, "请选择支付方式");
                } else if (aliPay.isChecked()) {
                    new PayModel(mActivity).aliPay(entity, new PayListener() {
                        @Override
                        public void isPaySuccess(boolean isPaySuccess, String orderId) {
                            if (isPaySuccess) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.showLoading();
                                        mActivity.getData();
                                        mAlertDialog.dismiss();
                                    }
                                });
                            } else {
                                ToastManager.showToast(mActivity, "支付失败");
                            }
                        }
                    });
                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "2");
                    UmengManager.onEvent(mActivity, "InterviewVip", map);

                } else if (wxPay.isChecked()) {
                    new PayModel(mActivity).wxPay(entity, new PayListener() {
                        @Override
                        public void isPaySuccess(boolean isPaySuccess, String orderId) {
                            if (isPaySuccess) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.showLoading();
                                        mActivity.getData();
                                        mAlertDialog.dismiss();
                                    }
                                });
                            } else {
                                ToastManager.showToast(mActivity, "支付失败");
                            }
                        }
                    });
                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "2");
                    UmengManager.onEvent(mActivity, "InterviewVip", map);
                }
            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) return;

        if ("submit_record".equals(apiName)) {          // 提交录音
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
                mActivity.setCanBack(0);
                mActivity.getData();
                // 检验是否为第一次提交录音
                mInterfaceViewCallBak.checkIsFirstSubmit();
            } else {
                ToastManager.showToast(mActivity,"提交失败");
            }
            mActivity.hideLoading();
        } else if("get_user_service_status".equals(apiName)){       // 获取名师点评的次数
            InterviewTeacherRemarkNumResp resp = GsonManager.getModel(response, InterviewTeacherRemarkNumResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
                // 回调刷新
                List<InterviewTeacherRemarkNumResp.Data> mDataList = resp.getData();
                if (mDataList != null && mDataList.size() > 0) {
                    mInterfaceViewCallBak.refreshTeacherRemarkRemainder(mDataList.get(0).getVal());
                }
            } else {
                mInterfaceViewCallBak.refreshTeacherRemarkRemainder("0");
                ToastManager.showToast(mActivity,"获取失败");
            }
        } else if("update_comment_status".equals(apiName)){     // 申请名师点评
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
            if (resp == null || resp.getResponse_code() != 1) {
                ToastManager.showToast(mActivity,"申请失败");
            }else {
                // 刷新申请的次数
                mActivity.getData();
                mInterfaceViewCallBak.popupAppliedForRemarkReminderAlert();
            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        if (mActivity != null)
            mActivity.hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        if (mActivity != null)
            mActivity.hideLoading();
    }

}
