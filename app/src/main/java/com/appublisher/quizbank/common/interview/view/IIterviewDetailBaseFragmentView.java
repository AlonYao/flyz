package com.appublisher.quizbank.common.interview.view;

import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;



public interface IIterviewDetailBaseFragmentView {

    int setLayoutResouceId();
    void initChildView();
    InterviewPaperDetailResp.QuestionsBean initChildData();
    String initChildQuestionType();
    void initChildListener();
    void banFragmentTouch();
    void releaseFragmentTouch();
    String getChildFragmentRich();

    // 申请名师点评的弹窗
    //void popupApplyForRemarkAlert();

    // 申请点评后,进行弹窗
   void popupAppliedForRemarkReminderAlert();
}
