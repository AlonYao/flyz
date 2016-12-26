package com.appublisher.quizbank.common.interview.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperDetailActivity;
import com.appublisher.quizbank.common.interview.fragment.InterviewUnPurchasedFragment;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

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

    private List<InterviewPaperDetailResp.QuestionsBean> mList;


    public InterviewUnPurchasedModel(Context context) {
        super(context);
        mContext = context;
    }
    /*
    *  显示未付费页面的弹窗
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
   *   创建开启完整版的弹窗
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
                                ToastManager.showToast(mActivity,"返回本录音页面");
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // 返回上一级
                                mActivity.setCanBack(0);
                            }
                        })
                .create().show();
    }


}
