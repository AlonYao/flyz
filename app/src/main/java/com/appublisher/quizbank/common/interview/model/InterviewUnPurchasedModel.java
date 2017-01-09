package com.appublisher.quizbank.common.interview.model;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_pay.PayListener;
import com.appublisher.lib_pay.PayModel;
import com.appublisher.lib_pay.ProductEntity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.model.netdata.CommonResp;

import org.json.JSONObject;


/*
*  本model为未付费页面:InterviewUnPurchasedFragment中Model
* */
public class InterviewUnPurchasedModel extends InterviewDetailModel{

    private InterviewPaperDetailActivity mActivity;

    public InterviewUnPurchasedModel(Context context) {
        super(context);
        if (context instanceof InterviewPaperDetailActivity)
            mActivity = (InterviewPaperDetailActivity) context;
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
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        mWindow.getDecorView().setPadding(0,0,0,0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        TextView goAnswer = (TextView) mWindow.findViewById(R.id.go_answer);
        TextView payOne = (TextView) mWindow.findViewById(R.id.pay_one);
        TextView payNine = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancle = (TextView) mWindow.findViewById(R.id.cancle);

        // 0.01元处理
        InterviewPaperDetailResp.SingleAudioBean bean = mActivity.getSingleAudioBean();
        if (bean != null && bean.is_purchased()) {
            payOne.setTextColor(Color.GRAY);
        } else {
            payOne.setTextColor(ContextCompat.getColor(mActivity, R.color.common_text));
        }

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
                // 0.01元支付
                InterviewPaperDetailResp.SingleAudioBean bean = mActivity.getSingleAudioBean();
                if (bean == null || bean.is_purchased()) return;

                ProductEntity entity = new ProductEntity();
                entity.setProduct_id(String.valueOf(bean.getProduct_id()));
                entity.setProduct_type(bean.getProduct_type());
                entity.setProduct_count(String.valueOf(1));
                entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                showChoicePay(entity);

                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });

        payNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 9元支付
                InterviewPaperDetailResp.AllAudioBean bean = mActivity.getAllAudioBean();
                if (bean == null || bean.is_purchased()) return;

                ProductEntity entity = new ProductEntity();
                entity.setProduct_id(String.valueOf(bean.getProduct_id()));
                entity.setProduct_type(bean.getProduct_type());
                entity.setProduct_count(String.valueOf(1));
                entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                showChoicePay(entity);

                mAalertDialog.dismiss();
                mActivity.setCanBack(0);              // 可以按返回键
            }
        });
    }

    /*
   *   创建开启完整版的dailog
   * */
    public void showOpenFullDialog() {
        if (mActivity == null) return;

        final AlertDialog mAalertDialog = new AlertDialog.Builder(mActivity).create();
        mAalertDialog.setCancelable(false);                         // 背景页面不可点,返回键也不可点击
        mAalertDialog.show();

        Window mWindow = mAalertDialog.getWindow();
        if (mWindow == null) return;
        mWindow.setContentView(R.layout.interview_popupwindow_openfull);
        mWindow.setBackgroundDrawableResource(R.color.transparency);   //背景色
        mWindow.setGravity(Gravity.BOTTOM);                         // 除底部弹出
        mWindow.getDecorView().setPadding(0, 0, 0, 0);                 // 消除边距
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;        // 背景宽度设置成和屏幕宽度一致
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(layoutParams);

        TextView payNine = (TextView) mWindow.findViewById(R.id.pay_nine);
        TextView cancle = (TextView) mWindow.findViewById(R.id.cancle);

        // 点击事件
        payNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 9元支付
                InterviewPaperDetailResp.AllAudioBean bean = mActivity.getAllAudioBean();
                if (bean == null || bean.is_purchased()) return;

                ProductEntity entity = new ProductEntity();
                entity.setProduct_id(String.valueOf(bean.getProduct_id()));
                entity.setProduct_type(bean.getProduct_type());
                entity.setProduct_count(String.valueOf(1));
                entity.setExtra(String.valueOf(mActivity.getCurQuestionId()));
                showChoicePay(entity);

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
    *   录音提交后:返回的信息
    * */
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
        }else if("update_collected_status".equals(apiName)){    //  收藏后的回调
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
            if (resp != null && resp.getResponse_code() == 1) {
            } else {
                ToastManager.showToast(mActivity,"刷新失败");
            }
        }
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
                }
            }
        });
    }
}
