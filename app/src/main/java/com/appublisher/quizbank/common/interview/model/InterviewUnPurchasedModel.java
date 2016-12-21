package com.appublisher.quizbank.common.interview.model;

import android.content.Context;

import com.appublisher.quizbank.common.interview.fragment.InterviewUnPurchasedFragment;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

import java.util.List;



public class InterviewUnPurchasedModel extends InterviewDetailModel{

    private Context mContext;
    private InterviewUnPurchasedFragment mFragment;

    private List<InterviewPaperDetailResp.QuestionsBean> mList;



    public InterviewUnPurchasedModel(Context context) {
        super(context);
        mContext = context;



    }


}
