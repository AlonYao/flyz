package com.appublisher.quizbank.common.interview.view;

import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;



public interface IIterviewDetailBaseFragmentView {

    int setLayoutResourceId();
    void initChildView();
    InterviewPaperDetailResp.QuestionsBean initChildData();
    String initChildQuestionType();
    void initChildListener();
    String getChildFragmentRich();
    String getIsUnPurchasedOrPurchasedView();       //获取是未付费还是已付费页面
    int getChildViewPosition();     // 具体viewpager的索引

}
