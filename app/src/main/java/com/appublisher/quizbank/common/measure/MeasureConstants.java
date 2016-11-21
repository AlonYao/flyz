package com.appublisher.quizbank.common.measure;

/**
 * 做题模块：常量
 */

public interface MeasureConstants {

    String PAPER_TYPE = "paper_type";
    String PAPER_ID = "paper_id";
    String HIERARCHY_ID = "hierarchy_id";
    String REDO = "redo";

    /**
     * 试卷类型
     */
    String AUTO = "auto";
    String NOTE = "note";
    String ERROR = "error";
    String COLLECT = "collect";
    String ENTIRE = "entire";
    String MOCK = "mock";
    String MOKAO = "mokao";
    String EVALUATE = "evaluate";

    /**
     * 接口请求
     */
    String AUTO_TRAINING = "auto_training";
    String NOTE_QUESTIONS = "note_questions";
    String PAPER_EXERCISE = "paper_exercise";
    String SUBMIT_PAPER = "submit_paper";

    /**
     * 缓存
     */
    String CACHE_USER_ANSWER = "cache_user_answer";
    String CACHE_PAPER_ID = "cache_paper_id";
    String CACHE_PAPER_TYPE = "cache_paper_type";
    String CACHE_REDO = "cache_redo";
    String CACHE_MOCK_TIME = "cache_mock_time";
    String CACHE_PAPER_NAME = "cache_paper_name";
    String YAOGUO_MEASURE = "yaoguo_measure";

}
