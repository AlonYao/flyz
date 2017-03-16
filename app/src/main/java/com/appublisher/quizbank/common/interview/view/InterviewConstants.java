package com.appublisher.quizbank.common.interview.view;


public interface InterviewConstants {
    /*
    *  播放器状态
    * */
    String SUBMIT = "submit";              //可提交
    String HAD_SUBMIT = "had_submit";      // 已提交
    String TEACHER_REMARK = "teacher_remark";      // 名师点评
    String QUESTION_ITEM = "question_item";
    String ANALYSIS_ITEM = "analysis_item";

    /*
    *  名师点评的状态
    * */
    int NOT_APPLY_REMARK = 3;   // 没有申请名师点评
    int COMMENT = 2;          // 等待点评中
    int HAD_REMARKED = 4;     // 已经点评
    int UN_LISTEN = 0;            // 没有收听

    /*
    *  播放的状态
    * */
    String NOT_EXIST_PLAYING_MEDIA = "not_exist_playing_media";
    String PLAY = "play";
    String PAUSE = "pause";
    String OVER = "over";

    /*
    *  录音时长限制和提醒
    * */
    int UPPER_LIMIT_RECORD_TIME = 360;
    int LOWER_LIMIT_RECORD_TIME = 5;
    int SHOW_TOAST_RECORD_TIME = 330;

    /*
    *  不同的录音页面状态
    * */
    int SHOW_NOT_RECORD_VIEW = 1;
    int SHOW_RECORDING_VIEW = 2;
    int SHOW_NOT_SUBMIT_VIEW = 3;
    int SHOW_HAD_SUBMIT_VIEW = 4;

    /*
    *  返回值成功
    * */
    int PAY_SUCCESS = 200;

    /*
    *  录音状态
    * */
    int UN_RECORD = 0;
    int RECORDING = 1;
    int RECORDED_UN_SUBMIT = 2;
    int RECORDED_HAD_SUBMIT = 3;
}