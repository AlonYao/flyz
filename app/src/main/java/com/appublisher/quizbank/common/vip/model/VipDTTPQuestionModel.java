package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.activity.VipDTTPActivity;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * 小班：单题突破 问题tab
 */

public class VipDTTPQuestionModel extends VipDTTPModel {

    public static final int MAX_LENGTH = 6;

    public ArrayList<String> mPaths;
    public boolean mCanSubmit;

    private VipDTTPActivity mView;

    public VipDTTPQuestionModel(Context context) {
        super(context);
        mView = (VipDTTPActivity) context;
    }

    public void showMyJob(ArrayList<String> mPaths,
                          String type,
                          int maxLength,
                          FlowLayout mMyjobContainer,
                          Context context,
                          VipBaseActivity.MyJobActionListener listener) {
        mView.showMyJob(mPaths, type, maxLength, mMyjobContainer, context, listener);
    }

    public void updateSubmitButton(int curLength, int maxLength, Button btnSubmit) {
        mView.updateSubmitButton(curLength, maxLength, btnSubmit);
    }

    public ImageView getMyJobItem() {
        return mView.getMyJobItem();
    }

}
