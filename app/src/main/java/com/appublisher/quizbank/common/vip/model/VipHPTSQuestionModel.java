package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.appublisher.quizbank.common.vip.activity.VipBaseActivity;
import com.appublisher.quizbank.common.vip.activity.VipHPTSActivity;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * 小班：互评提升 问题Tab
 */

public class VipHPTSQuestionModel extends VipHPTSModel {

    public static final int MAX_LENGTH = 6;

    public ArrayList<String> mPaths;
    public boolean mCanSubmit;

    private VipHPTSActivity mView;

    public VipHPTSQuestionModel(Context context) {
        super(context);
        mView = (VipHPTSActivity) context;
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

}
