package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * 小班：名师精辟 问题tab
 */

public class VipMSJPQuestionModel extends VipMSJPModel{

    public static final int MAX_LENGTH = 6;

    public ArrayList<String> mPaths;
    public boolean mCanSubmit;

    private VipMSJPActivity mView;

    public VipMSJPQuestionModel(Context context) {
        super(context);
        mView = (VipMSJPActivity) context;
    }

    public void showMyJob(ArrayList<String> mPaths,
                          String type,
                          int maxLength,
                          FlowLayout mMyjobContainer,
                          Context context,
                          VipBaseActivity.MyJobActionListener listener) {
        mView.showMyJob(mPaths, type, maxLength, mMyjobContainer, context, listener);
    }

    public void updateSubmitButton(int curLength, Button btnSubmit) {
        mView.updateSubmitButton(curLength, btnSubmit);
    }

    public ImageView getMyJobItem() {
        return mView.getMyJobItem();
    }

    public void showRejectAlert(String review_postil, String review_time) {
        mView.showRejectAlert(review_postil, review_time);
    }
}
