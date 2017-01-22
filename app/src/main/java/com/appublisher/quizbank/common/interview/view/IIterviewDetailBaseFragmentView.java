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

}
