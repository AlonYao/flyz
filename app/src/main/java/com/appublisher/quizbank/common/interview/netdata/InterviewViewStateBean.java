package com.appublisher.quizbank.common.interview.netdata;

/**
 * 保存面试页面中状态改变的属性
 */
public class InterviewViewStateBean {
    // 题目行
    private int questionItemState;  // 默认为零
    // 解析行
    private int analysisItemState;
    // 未提交时的状态
    private int userNotSubmitState;
    // 已提交时的状态
    private int userHadSubmitState;
    // 已点评时的名师点评的状态
    private int teacherRemarkItemState;
    // 播放时的时间
    private int playingDuration;
    // 具体是哪一个播放器在播放
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public int getTeacherRemarkItemState() {
        return teacherRemarkItemState;
    }
    public void setTeacherRemarkItemState(int teacherRemarkItemState) {
        this.teacherRemarkItemState = teacherRemarkItemState;
    }
    public int getUserHadSubmitState() {
        return userHadSubmitState;
    }
    public void setUserHadSubmitState(int userHadSubmitState) {
        this.userHadSubmitState = userHadSubmitState;
    }
    public int getUserNotSubmitState() {
        return userNotSubmitState;
    }
    public void setUserNotSubmitState(int userNotSubmitState) {
        this.userNotSubmitState = userNotSubmitState;
    }
    public int getQuestionItemState() {
        return questionItemState;
    }
    public void setQuestionItemState(int questionItemState) {
        this.questionItemState = questionItemState;
    }
    public int getAnalysisItemState() {
        return analysisItemState;
    }
    public void setAnalysisItemState(int analysisItemState) {
        this.analysisItemState = analysisItemState;
    }
    public int getPlayingDuration() {
        return playingDuration;
    }
    public void setPlayingDuration(int playingDuration) {
        this.playingDuration = playingDuration;
    }
}
