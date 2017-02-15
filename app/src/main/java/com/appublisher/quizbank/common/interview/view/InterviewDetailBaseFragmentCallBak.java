package com.appublisher.quizbank.common.interview.view;


public interface InterviewDetailBaseFragmentCallBak {

    // 名师点评的次数的回调
    void refreshTeacherRemarkRemainder(String num);

    // 申请点评后,进行弹窗
    void popupAppliedForRemarkReminderAlert();

    // 检验是否第一次提交录音
    void checkIsFirstSubmit();
}
