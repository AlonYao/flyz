package com.appublisher.quizbank.common.measure;

/**
 * 做题模块：常量
 */

public interface MeasureConstants {

    /**
     * intent 参数
     */
    String INTENT_PAPER_TYPE = "paper_type";
    String INTENT_PAPER_ID = "paper_id";
    String INTENT_HIERARCHY_ID = "hierarchy_id";
    String INTENT_REDO = "redo";
    String INTENT_ANALYSIS_BEAN = "analysis_bean";
    String INTENT_ANALYSIS_IS_ERROR_ONLY = "is_error_only";
    String INTENT_IS_FROM_FOLDER = "is_from_folder";
    String INTENT_MOCK_TIME = "mock_time";
    String INTENT_VIP_XC_DATA = "vip_xc_data";

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
    String VIP = "vip";

    /**
     * 接口请求
     */
    String AUTO_TRAINING = "auto_training";
    String NOTE_QUESTIONS = "note_questions";
    String PAPER_EXERCISE = "paper_exercise";
    String SUBMIT_PAPER = "submit_paper";
    String HISTORY_EXERCISE_DETAIL = "history_exercise_detail";
    String COLLECT_QUESTION = "collect_question";
    String COLLECT_ERROR_QUESTIONS = "collect_error_questions";
    String SERVER_CURRENT_TIME = "server_current_time";

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

    /**
     * 提交数据
     */
    String SUBMIT_ID = "id";
    String SUBMIT_ANSWER = "answer";
    String SUBMIT_CATEGORY = "category";
    String SUBMIT_NOTE_IDS = "note_ids";
    String SUBMIT_IS_RIGHT = "is_right";
    String SUBMIT_DURATION = "duration";
    String SUBMIT_DONE = "done";
    String SUBMIT_UNDONE = "undone";

    /**
     * 选项
     */
    String OPTION_A = "A";
    String OPTION_B = "B";
    String OPTION_C = "C";
    String OPTION_D = "D";

}
