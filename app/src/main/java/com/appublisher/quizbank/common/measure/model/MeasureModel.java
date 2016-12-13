package com.appublisher.quizbank.common.measure.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.activity.MeasureReportActivity;
import com.appublisher.quizbank.common.measure.adapter.MeasureAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureExcludeBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureAutoResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureEntireResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureHistoryResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureNotesResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureSubmitResp;
import com.appublisher.quizbank.common.measure.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.common.measure.network.MeasureParamBuilder;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.common.vip.model.VipXCModel;
import com.appublisher.quizbank.common.vip.netdata.VipXCResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 做题模块：管理类
 */

public class MeasureModel implements RequestCallback, MeasureConstants {

    public int mPaperId;
    public int mHierarchyId;
    public int mMockDuration;
    public int mCurPagePosition;
    public boolean mRedo;
    public long mCurTimestamp;
    public String mPaperType;
    public String mPaperName;
    public String mMockTime;
    public List<MeasureExcludeBean> mExcludes;
    public MeasureRequest mRequest;
    public Context mContext;
    public List<MeasureTabBean> mTabs;
    public String mVipXCData;

    private SparseIntArray mFinalHeightMap;
    private SubmitListener mSubmitListener;
    private ServerTimeListener mServerTimeListener;
    private int mPaperDuration;


    public MeasureModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
    }

    interface SubmitListener {
        void onComplete(boolean success, int exercise_id);
    }

    public interface ServerTimeListener {
        void onTimeOut();
        void canSubmit();
    }

    public int getFinalHeightById(int id) {
        if (mFinalHeightMap == null) return 0;
        return mFinalHeightMap.get(id);
    }

    public void saveFinalHeight(int id, int finalHeight) {
        if (mFinalHeightMap == null) mFinalHeightMap = new SparseIntArray();
        mFinalHeightMap.put(id, finalHeight);
    }

    public void getData() {
        if (mRedo) {
            // 继续做
            mRequest.getHistoryExerciseDetail(mPaperId, mPaperType);
        } else {
            // 新题
            if (AUTO.equals(mPaperType)) {
                mRequest.getAutoTraining();
            } else if (NOTE.equals(mPaperType) || COLLECT.equals(mPaperType)
                    || ERROR.equals(mPaperType)) {
                mRequest.getNoteQuestions(mHierarchyId, mPaperType);
            } else if (ENTIRE.equals(mPaperType) || MOKAO.equals(mPaperType)
                    || MOCK.equals(mPaperType) || EVALUATE.equals(mPaperType)) {
                mRequest.getPaperExercise(mPaperId, mPaperType);
            } else if (VIP.equals(mPaperType)) {
                dealVipXC();
                hideLoading();
            }
        }
    }

    /**
     * 处理小班行测
     */
    private void dealVipXC() {
        VipXCResp resp = GsonManager.getModel(mVipXCData, VipXCResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        List<VipXCResp.QuestionBean> originList = resp.getQuestion();
        if (originList == null || originList.size() == 0) return;

        List<MeasureQuestionBean> questions = new ArrayList<>();
        for (VipXCResp.QuestionBean questionBean : originList) {
            if (questionBean == null) continue;
            MeasureQuestionBean question = vipXCQuestionTransform(questionBean);
            questions.add(question);
        }

        questions = setQuestionOrder(questions, 0);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    public static MeasureQuestionBean vipXCQuestionTransform(VipXCResp.QuestionBean origin) {
        MeasureQuestionBean question = new MeasureQuestionBean();
        if (origin == null) return question;

        question.setId(origin.getQuestion_id());
        question.setMaterial(origin.getMaterial());
        question.setQuestion(origin.getQuestion());
        question.setOption_a(origin.getOption_a());
        question.setOption_b(origin.getOption_b());
        question.setOption_c(origin.getOption_c());
        question.setOption_d(origin.getOption_d());
        question.setAnswer(origin.getAnswer());
        question.setAnalysis(origin.getAnalysis());
        question.setNote_id(origin.getNote_id());
        question.setNote_ids(origin.getNote_ids());
        question.setNote_name(origin.getNote_name());
        question.setCategory_id(origin.getCategory_id());
        question.setCategory_name(origin.getCategory_name());
        question.setSource(origin.getSource());
        question.setAccuracy(origin.getAccuracy());
        question.setSummary_accuracy(origin.getSummary_accuracy());
        question.setSummary_count(origin.getSummary_count());
        question.setSummary_fallible(origin.getSummary_fallible());
        question.setMaterial_id(origin.getMaterial_id());

        return question;
    }

    private void hideLoading() {
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    /**
     * 处理快速智能练习
     * @param response JSONObject
     */
    private void dealAutoTrainingResp(JSONObject response) {
        MeasureAutoResp resp = GsonManager.getModel(response, MeasureAutoResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureActivity)) return;

        mPaperId = resp.getPaper_id();
        List<MeasureQuestionBean> questions = setQuestionOrder(resp.getQuestions(), 0);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 处理知识点专项训练
     * @param response JSONObject
     */
    private void dealNoteQuestionsResp(JSONObject response) {
        MeasureNotesResp resp = GsonManager.getModel(response, MeasureNotesResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureActivity)) return;

        mPaperId = resp.getPaper_id();
        List<MeasureQuestionBean> questions = setQuestionOrder(resp.getQuestions(), 0);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 处理每日模考&整卷
     * @param response JSONObject
     */
    private void dealPaperExerciseResp(JSONObject response) {
        if (!(mContext instanceof MeasureActivity)) return;

        MeasureEntireResp resp = GsonManager.getModel(response, MeasureEntireResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureEntireResp.CategoryBean> categorys = resp.getCategory();

        if (categorys == null || categorys.size() == 0) {
            // 非整卷
            // 设置题号
            questions = setQuestionOrder(resp.getQuestions(), 0);
        } else {
            // 整卷
            mTabs = new ArrayList<>();
            int size = categorys.size();
            // 遍历
            for (int i = 0; i < size; i++) {
                MeasureEntireResp.CategoryBean category = categorys.get(i);
                if (category == null) continue;
                List<MeasureQuestionBean> categoryQuestions = category.getQuestions();
                if (categoryQuestions == null) continue;

                // 添加Tab数据
                MeasureTabBean tabBean = new MeasureTabBean();
                tabBean.setName(category.getName());
                tabBean.setPosition(questions.size());
                mTabs.add(tabBean);

                // 添加题目数据，构造说明页
                MeasureQuestionBean question = new MeasureQuestionBean();
                question.setIs_desc(true);
                question.setCategory_name(category.getName());
                question.setDesc_position(i);

                // 添加至题目list
                questions.add(question);
                questions.addAll(categoryQuestions);
            }

            // 设置题号
            // 模考特殊处理
            if (MOCK.equals(mPaperType)) {
                questions = setMockQuestionOrder(questions, size);
            } else {
                questions = setQuestionOrder(questions, size);
            }

            // 显示Tab
            ((MeasureActivity) mContext).showTabLayout(mTabs);
        }

        // 模考特殊处理
        if (MOCK.equals(mPaperType)) {
            mPaperDuration = resp.getDuration();
            setMockDuration();
        }

        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 处理Redo为true时请求的接口
     * @param response JSONObject
     */
    private void dealHistoryExerciseDetail(JSONObject response) {
        if (!(mContext instanceof MeasureActivity)) return;

        MeasureHistoryResp resp = GsonManager.getModel(response, MeasureHistoryResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;

        List<MeasureQuestionBean> questions = new ArrayList<>();
        List<MeasureSubmitBean> submits = new ArrayList<>();
        mExcludes = new ArrayList<>();
        List<MeasureCategoryBean> categorys = resp.getCategory();

        if (categorys == null || categorys.size() == 0) {
            // 非整卷

            // 遍历原始问题
            List<MeasureQuestionBean> originQuestions = resp.getQuestions();
            if (originQuestions == null) return;

            int order = 0;
            int size = originQuestions.size();
            for (int i = 0; i < size; i++) {
                // 索引&题号
                MeasureQuestionBean questionBean = originQuestions.get(i);
                if (questionBean == null) continue;
                questionBean.setQuestion_index(i);
                order++;
                questionBean.setQuestion_order(order);
                questionBean.setQuestion_amount(size);
                questions.add(questionBean);

                // 选项排除
                mExcludes.add(new MeasureExcludeBean());

                // 添加用户答案中的noteids
                MeasureSubmitBean submitBean = new MeasureSubmitBean();
                submitBean.setNote_ids(questionBean.getNote_ids());
                submits.add(submitBean);
            }

            // 遍历原始用户答案
            List<MeasureAnswerBean> originAnswers = resp.getAnswers();
            if (originAnswers == null) return;

            size = originAnswers.size();
            for (int i = 0; i < size; i++) {
                MeasureAnswerBean answerBean = originAnswers.get(i);
                if (answerBean == null) continue;
                if (i >= submits.size()) continue;

                MeasureSubmitBean submitBean = submits.get(i);
                if (submitBean == null) continue;
                submitBean.setId(answerBean.getId());
                submitBean.setAnswer(answerBean.getAnswer());
                submitBean.setCategory(answerBean.getCategory());
                submitBean.setDuration(answerBean.getDuration());
                if (answerBean.is_right()) {
                    submitBean.setIs_right(1);
                } else {
                    submitBean.setIs_right(0);
                }
                submits.set(i, submitBean);
            }

        } else {
            // 整卷
            mTabs = new ArrayList<>();
            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                MeasureCategoryBean category = categorys.get(i);
                if (category == null) continue;
                List<MeasureQuestionBean> categoryQuestions = category.getQuestions();
                if (categoryQuestions == null) continue;
                List<MeasureAnswerBean> categoryAnswers = category.getAnswers();
                if (categoryAnswers == null) continue;

                // 添加Tab数据
                MeasureTabBean tabBean = new MeasureTabBean();
                tabBean.setName(category.getName());
                tabBean.setPosition(questions.size());
                mTabs.add(tabBean);

                // 构造说明页
                MeasureQuestionBean question = new MeasureQuestionBean();
                question.setIs_desc(true);
                question.setCategory_name(category.getName());
                question.setDesc_position(i);

                // 添加题目
                questions.add(question);
                questions.addAll(categoryQuestions);

                // 添加用户答案
                List<MeasureSubmitBean> childSubmits = new ArrayList<>();
                int qSize = categoryQuestions.size();
                for (int j = 0; j < qSize; j++) {
                    MeasureQuestionBean questionBean = categoryQuestions.get(j);
                    if (questionBean == null) continue;
                    MeasureSubmitBean submitBean = new MeasureSubmitBean();
                    submitBean.setNote_ids(questionBean.getNote_ids());
                    childSubmits.add(submitBean);
                }

                qSize = categoryAnswers.size();
                for (int j = 0; j < qSize; j++) {
                    MeasureAnswerBean answerBean = categoryAnswers.get(j);
                    if (answerBean == null) continue;
                    if (j >= childSubmits.size()) continue;

                    MeasureSubmitBean submitBean = childSubmits.get(j);
                    if (submitBean == null) continue;
                    submitBean.setId(answerBean.getId());
                    submitBean.setAnswer(answerBean.getAnswer());
                    submitBean.setCategory(answerBean.getCategory());
                    submitBean.setDuration(answerBean.getDuration());

                    if (answerBean.is_right()) {
                        submitBean.setIs_right(1);
                    } else {
                        submitBean.setIs_right(0);
                    }

                    // 更新
                    childSubmits.set(j, submitBean);
                }

                submits.addAll(childSubmits);
            }

            // 添加题号索引选项排除
            int index = 0;
            int order = 0;
            int amount = questions.size() - size;
            mExcludes = new ArrayList<>();

            for (MeasureQuestionBean question : questions) {
                if (question == null) continue;

                // 题量&索引
                question.setQuestion_index(index);
                question.setQuestion_amount(amount);

                // 题号
                if (!question.is_desc()) {
                    order++;
                    question.setQuestion_order(order);
                }

                // 更新
                questions.set(index, question);
                index++;

                // 选项排除
                mExcludes.add(new MeasureExcludeBean());
            }

            // 显示Tab
            ((MeasureActivity) mContext).showTabLayout(mTabs);
        }

        // 缓存
        saveSubmitPaperInfo();
        saveUserAnswerCache(mContext, submits);

        // 设置计时器时间
        int startFrom = resp.getStart_from();
        ((MeasureActivity) mContext).mSec = startFrom % 60;
        ((MeasureActivity) mContext).mMins = startFrom / 60;

        // 展示
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 设置题号&索引(同时初始化用户记录)
     * @param list List<MeasureQuestionBean>
     * @return List<MeasureQuestionBean>
     */
    private List<MeasureQuestionBean> setQuestionOrder(List<MeasureQuestionBean> list,
                                                       int descSize) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
        int amount = size - descSize;
        int order = 0;
        mExcludes = new ArrayList<>();
        List<MeasureSubmitBean> submits = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            // 设置索引
            MeasureQuestionBean measureQuestionBean = list.get(i);
            if (measureQuestionBean == null) continue;
            measureQuestionBean.setQuestion_index(i);

            // 设置题号
            if (measureQuestionBean.is_desc()) continue;
            order++;
            measureQuestionBean.setQuestion_order(order);
            measureQuestionBean.setQuestion_amount(amount);
            list.set(i, measureQuestionBean);

            // 选项排除
            mExcludes.add(new MeasureExcludeBean());

            // 用户做题记录
            MeasureSubmitBean submitBean = new MeasureSubmitBean();
            submitBean.setId(measureQuestionBean.getId());
            submitBean.setCategory(measureQuestionBean.getCategory_id());
            submitBean.setNote_ids(measureQuestionBean.getNote_ids());
            submits.add(submitBean);
        }

        // 缓存
        saveSubmitPaperInfo();
        saveUserAnswerCache(mContext, submits);

        return list;
    }

    /**
     * 设置模考题号&索引(同时初始化用户记录)
     * @param list List<MeasureQuestionBean>
     * @return List<MeasureQuestionBean>
     */
    private List<MeasureQuestionBean> setMockQuestionOrder(List<MeasureQuestionBean> list,
                                                       int descSize) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
        int amount = size - descSize;
        int order = 0;
        mExcludes = new ArrayList<>();

        boolean isFresh = false;
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null || submits.size() == 0) {
            isFresh = true;
            submits = new ArrayList<>();
        }

        for (int i = 0; i < size; i++) {
            // 设置索引
            MeasureQuestionBean measureQuestionBean = list.get(i);
            if (measureQuestionBean == null) continue;
            measureQuestionBean.setQuestion_index(i);

            // 设置题号
            if (measureQuestionBean.is_desc()) continue;
            order++;
            measureQuestionBean.setQuestion_order(order);
            measureQuestionBean.setQuestion_amount(amount);
            list.set(i, measureQuestionBean);

            // 选项排除
            mExcludes.add(new MeasureExcludeBean());

            // 用户做题记录
            if (isFresh) {
                MeasureSubmitBean submitBean = new MeasureSubmitBean();
                submitBean.setId(measureQuestionBean.getId());
                submitBean.setCategory(measureQuestionBean.getCategory_id());
                submitBean.setNote_ids(measureQuestionBean.getNote_ids());
                submits.add(submitBean);
            }
        }

        // 缓存
        saveSubmitPaperInfo();
        saveUserAnswerCache(mContext, submits);

        return list;
    }

    /**
     * 获取做题模块缓存
     * @return SharedPreferences
     */
    private static SharedPreferences getMeasureCache(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(YAOGUO_MEASURE, Context.MODE_PRIVATE);
    }

    /**
     * 是否有做题记录
     * @return boolean
     */
    private boolean hasRecord() {
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null) return false;

        for (MeasureSubmitBean submit : submits) {
            if (submit == null) continue;
            if (submit.getAnswer().length() > 0) return true;
        }

        return false;
    }

    /**
     * 是否全部完成
     * @return boolean
     */
    public boolean isAllDone() {
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null) return false;

        int size = submits.size();
        int count = 0;
        for (MeasureSubmitBean submit : submits) {
            if (submit == null) continue;
            if (submit.getAnswer().length() > 0) count++;
        }

        return count == size;
    }

    /**
     * 获取用户答案缓存
     * @param context Context
     * @return List<MeasureSubmitBean>
     */
    public static List<MeasureSubmitBean> getUserAnswerCache(Context context) {
        if (context == null) return new ArrayList<>();
        SharedPreferences cache = getMeasureCache(context);
        if (cache == null) return new ArrayList<>();

        List<MeasureSubmitBean> list = new ArrayList<>();
        String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
        if (userAnswer.length() == 0) return new ArrayList<>();
        try {
            JSONArray array = new JSONArray(userAnswer);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                MeasureSubmitBean submitBean = new MeasureSubmitBean();

                JSONObject object = array.getJSONObject(i);

                submitBean.setId(object.getInt(SUBMIT_ID));
                submitBean.setAnswer(object.getString(SUBMIT_ANSWER));
                submitBean.setCategory(object.getInt(SUBMIT_CATEGORY));
                submitBean.setDuration(object.getInt(SUBMIT_DURATION));
                submitBean.setIs_right(object.getInt(SUBMIT_IS_RIGHT));

                // 构建note_ids
                List<Integer> noteIdList = new ArrayList<>();
                JSONArray noteIdArray = object.getJSONArray(SUBMIT_NOTE_IDS);
                int arrayLength = noteIdArray.length();
                for (int j = 0; j < arrayLength; j++) {
                    noteIdList.add(noteIdArray.getInt(j));
                }
                submitBean.setNote_ids(noteIdList);

                list.add(submitBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressLint("CommitPrefEdits")
    private static void saveUserAnswerCache(Context context, List<MeasureSubmitBean> list) {
        if (list == null) return;
        JSONArray array = new JSONArray();
        for (MeasureSubmitBean submitBean : list) {

            if (submitBean == null) continue;
            JSONObject object = new JSONObject();
            try {
                object.put(SUBMIT_ID, submitBean.getId());
                object.put(SUBMIT_CATEGORY, submitBean.getCategory());
                object.put(SUBMIT_ANSWER, submitBean.getAnswer());
                object.put(SUBMIT_IS_RIGHT, submitBean.getIs_right());

                // 构建noteIds
                List<Integer> noteIds = submitBean.getNote_ids();
                JSONArray noteIdArray = new JSONArray();
                if (noteIds != null) {
                    for (Integer noteId : noteIds) {
                        noteIdArray.put(noteId);
                    }
                }
                object.put(SUBMIT_NOTE_IDS, noteIdArray);

                object.put(SUBMIT_DURATION, submitBean.getDuration());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }

        SharedPreferences spf = getMeasureCache(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(CACHE_USER_ANSWER, array.toString());
        editor.commit();
    }

    public static void saveSubmitAnswer(Context context, int position, String option, int isRight) {
        List<MeasureSubmitBean> list = getUserAnswerCache(context);
        if (list == null || position >= list.size() || position < 0) return;
        MeasureSubmitBean submitBean = list.get(position);
        if (submitBean == null) return;
        submitBean.setAnswer(option);
        submitBean.setIs_right(isRight);
        list.set(position, submitBean);
        saveUserAnswerCache(context, list);
    }

    /**
     * 缓存用户做题时长
     */
    public void saveSubmitDuration() {
        if (!(mContext instanceof MeasureActivity)) return;

        List<MeasureQuestionBean> questions = getAdapterQuestions();
        if (questions == null || mCurPagePosition < 0 || mCurPagePosition >= questions.size())
            return;

        // 去掉说明页
        MeasureQuestionBean questionBean = questions.get(mCurPagePosition);
        if (questionBean == null || questionBean.is_desc()) return;

        int order = questionBean.getQuestion_order();
        order--;
        if (order < 0) return;
        List<MeasureSubmitBean> list = getUserAnswerCache(mContext);
        if (list == null || order >= list.size()) return;

        MeasureSubmitBean submitBean = list.get(order);
        if (submitBean == null) return;

        int duration = (int) ((System.currentTimeMillis() - mCurTimestamp) / 1000);
        if (duration == 0) return;
        int preDuration = submitBean.getDuration();
        duration = duration + preDuration;
        submitBean.setDuration(duration);
        list.set(order, submitBean);
        saveUserAnswerCache(mContext, list);

        mCurTimestamp = System.currentTimeMillis();

        // Umeng
        String isDone = "0";
        if (submitBean.getAnswer().length() > 0) {
            isDone = "1";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", isDone);
        UmengManager.onEvent(mContext, "Question", map);
    }

    public static String getUserAnswerByPosition(Context context, int position) {
        List<MeasureSubmitBean> list = getUserAnswerCache(context);
        if (list == null || position < 0 || position >= list.size()) return "";
        MeasureSubmitBean bean = list.get(position);
        if (bean == null) return "";
        return bean.getAnswer();
    }

    @SuppressLint("CommitPrefEdits")
    private void saveSubmitPaperInfo() {
        SharedPreferences spf = getMeasureCache(mContext);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt(CACHE_PAPER_ID, mPaperId);
        editor.putString(CACHE_PAPER_TYPE, String.valueOf(mPaperType));
        editor.putBoolean(CACHE_REDO, mRedo);
        editor.putString(CACHE_MOCK_TIME, mMockTime);
        editor.commit();
    }

    /**
     * 动态添加富文本
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    @SuppressWarnings("deprecation")
    public static void addRichTextToContainer(final Context context,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

//        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(context);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(context);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        for (final ParseManager.ParsedSegment segment : segments) {
            if (segment.text == null || segment.text.length() == 0) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(context.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                ImageManager.displayImage(segment.text.toString(), imgView);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, ScaleImageActivity.class);
                        intent.putExtra(ScaleImageActivity.INTENT_IMGURL, segment.text.toString());
                        context.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

    public int getTabPositionScrollTo(int curPosition) {
        if (mTabs == null) return 0;
        int size = mTabs.size();
        int tab = 0;
        for (int i = 0; i < size; i++) {
            MeasureTabBean tabBean = mTabs.get(i);
            if (tabBean == null) continue;
            if (curPosition >= tabBean.getPosition()) tab = i;
        }
        return tab;
    }

    public int getPositionByTab(int tabPosition) {
        if (mTabs == null || tabPosition >= mTabs.size()) return 0;
        MeasureTabBean tabBean = mTabs.get(tabPosition);
        if (tabBean == null) return 0;
        return tabBean.getPosition();
    }

    public void submit(boolean isDone, SubmitListener listener) {
        mSubmitListener = listener;
        submit(isDone);
    }

    public void submit(boolean isDone) {
        String doneStatus = SUBMIT_DONE;
        if (!isDone) doneStatus = SUBMIT_UNDONE;

        SharedPreferences spf = getMeasureCache(mContext);
        if (spf == null) return;
        int paperId = spf.getInt(CACHE_PAPER_ID, 0);
        String paperTpye = spf.getString(CACHE_PAPER_TYPE, "");
        boolean redo = spf.getBoolean(CACHE_REDO, false);
        String answer = spf.getString(CACHE_USER_ANSWER, "");
        if (paperId == 0 || paperTpye.length() == 0 || answer.length() == 0) return;

        // 统计总时长
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null) return;
        int durtion = 0;
        for (MeasureSubmitBean submitBean : submits) {
            if (submitBean == null) continue;
            durtion = durtion + submitBean.getDuration();
        }

        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).showLoading();
        mRequest.submitPaper(MeasureParamBuilder.submitPaper(
                paperId, paperTpye, redo, durtion, answer, doneStatus));
    }

    public void submitPaperDone() {
        if (!(mContext instanceof MeasureActivity)) return;
        submit(true, new SubmitListener() {
            @Override
            public void onComplete(boolean success, int exercise_id) {
                if (success) {
                    Intent intent = new Intent(mContext, MeasureReportActivity.class);
                    intent.putExtra(INTENT_PAPER_ID, exercise_id);
                    intent.putExtra(INTENT_PAPER_TYPE, mPaperType);
                    mContext.startActivity(intent);
                    ((MeasureActivity) mContext).finish();
                    // 清除做题缓存
                    clearUserAnswerCache(mContext);
                } else {
                    ((MeasureActivity) mContext).showSubmitErrorToast();
                }
            }
        });
    }

    public void submitVipXCPaper() {
        SharedPreferences spf = getMeasureCache(mContext);
        if (spf == null) return;
        String answer = spf.getString(CACHE_USER_ANSWER, "");

        // 统计总时长
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null) return;
        int durtion = 0;
        for (MeasureSubmitBean submitBean : submits) {
            if (submitBean == null) continue;
            durtion = durtion + submitBean.getDuration();
        }

        VipXCModel.submitPaper(mContext, mPaperId, answer, durtion);

        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", "1");
        UmengManager.onEvent(mContext, "Zhineng", map);
    }

    public void checkRecord() {
        if (!(mContext instanceof MeasureActivity)) return;
        if (MOCK.equals(mPaperType)) {
            // 模考特殊处理
            ((MeasureActivity) mContext).showLoading();
            getServerTimeStamp(new ServerTimeListener() {
                @Override
                public void onTimeOut() {
                    // 超时处理
                    ((MeasureActivity) mContext).showMockTimeOutAlert();
                    submitPaperDone();
                }

                @Override
                public void canSubmit() {
                    // 可提交状态
                    if (hasRecord()) {
                        ((MeasureActivity) mContext).showMockSaveTestAlert();
                    } else {
                        ((MeasureActivity) mContext).finish();
                    }
                }
            });
        } else if (VIP.equals(mPaperType)) {
            ((MeasureActivity) mContext).finish();
        } else {
            if (hasRecord()) {
                ((MeasureActivity) mContext).showSaveTestAlert();
            } else {
                ((MeasureActivity) mContext).finish();
            }
        }
    }

    private void dealSubmitResp(JSONObject response) {
        if (mSubmitListener == null) return;
        MeasureSubmitResp resp = GsonManager.getModel(response, MeasureSubmitResp.class);
        if (resp != null && resp.getResponse_code() == 1) {
            mSubmitListener.onComplete(true, resp.getExercise_id());
        } else {
            mSubmitListener.onComplete(false, 0);
        }
    }

    public List<MeasureQuestionBean> getAdapterQuestions() {
        List<MeasureQuestionBean> list = new ArrayList<>();
        if (mContext instanceof MeasureActivity) {
            MeasureAdapter adapter = ((MeasureActivity) mContext).mAdapter;
            if (adapter != null) list = adapter.getQuestions();
        }
        return list;
    }

    /**
     * 设置模考持续时间
     */
    private void setMockDuration() {
        setMockDuration(System.currentTimeMillis());
    }

    /**
     * 设置模考持续时间
     */
    private void setMockDuration(long curTimestamp) {
        try {
            Date date;
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(mMockTime);
            long mockTimeStamp = date.getTime();
            mockTimeStamp = mockTimeStamp + (mPaperDuration * 1000);
            mMockDuration = (int) ((mockTimeStamp - curTimestamp) / 1000);

            if (mMockDuration > 0 && mContext instanceof MeasureActivity) {
                ((MeasureActivity) mContext).setMinAndSec(mMockDuration);
            }

        } catch (Exception e) {
            // Empty
        }
    }

    /**
     * 模考：是否时间到
     * @return boolean
     */
    private boolean isMockTimeOut() {
        return mMockDuration <= 0;
    }

    /**
     * 模考：开考是否小于30分钟
     * @return boolean
     */
    private boolean isMockTimeUnder30() {
        return mPaperDuration - mMockDuration < 1800;
    }

    public void getServerTimeStamp(ServerTimeListener serverTimeListener) {
        mServerTimeListener = serverTimeListener;
        mRequest.getServerCurrentTime();
    }

    private void dealServerCurrentTimeResp(JSONObject response) {
        ServerCurrentTimeResp resp = GsonManager.getModel(response, ServerCurrentTimeResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        String serverTimeStampString = resp.getCurrent_time();
        long serverTimeStamp = Long.parseLong(serverTimeStampString);
        if (serverTimeStamp <= 0) return;

        setMockDuration(serverTimeStamp * 1000);

        if (!(mContext instanceof MeasureActivity)) return;
        if (isMockTimeOut()) {
            // 时间到
            if (mServerTimeListener != null) mServerTimeListener.onTimeOut();
            return;
        } else if (isMockTimeUnder30()) {
            ((MeasureActivity) mContext).showMockTime30Toast();
        } else {
            if (mServerTimeListener != null) mServerTimeListener.canSubmit();
        }

        // 恢复计时
        if (((MeasureActivity) mContext).mTimer == null)
            ((MeasureActivity) mContext).startTimer();
    }

    public void checkCache() {
        SharedPreferences cache = getMeasureCache(mContext);
        int paperId = cache.getInt(CACHE_PAPER_ID, 0);
        if (paperId == 0) return;
        String paperType = cache.getString(CACHE_PAPER_TYPE, "");
        if (paperType.length() == 0 || "vip".equals(paperType)) return;

        if (hasRecord()) {
            if ("mock".equals(paperType)) {
                new QRequest(mContext, this).getMockPreExamInfo(String.valueOf(paperId));
            } else {
                // 提交做题数据
                String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
                boolean redo = cache.getBoolean(CACHE_REDO, false);
                new QRequest(mContext, this).cacheSubmitPaper(
                        MeasureParamBuilder.submitPaper(
                                paperId,
                                paperType,
                                redo,
                                countDuration(userAnswer),
                                userAnswer,
                                "undone")
                );
                clearUserAnswerCache(mContext);
            }
        } else {
            clearUserAnswerCache(mContext);
        }
    }

    private int countDuration(String userAnswer) {
        int count = 0;
        try {
            JSONArray array = new JSONArray(userAnswer);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = array.getJSONObject(i);
                int duration = jo.getInt("duration");
                count = count + duration;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return count;
    }

    @SuppressLint("CommitPrefEdits")
    public static void clearUserAnswerCache(Context context) {
        SharedPreferences cache = getMeasureCache(context);
        SharedPreferences.Editor editor = cache.edit();
        editor.putInt(CACHE_PAPER_ID, 0);
        editor.putString(CACHE_PAPER_TYPE, "");
        editor.putString(CACHE_USER_ANSWER, "");
        editor.putBoolean(CACHE_REDO, false);
        editor.putString(CACHE_MOCK_TIME, "");
        editor.putString(CACHE_PAPER_NAME, "");
        editor.commit();
    }

    private void showMockCacheSubmitAlert() {
        if (mContext == null || ((Activity) mContext).isFinishing()) return;
        String msg = "你的模考正在进行中";
        String n = "放弃";
        String p = "继续";
        new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setNegativeButton(n, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearUserAnswerCache(mContext);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences cache = getMeasureCache(mContext);
                                int paperId = cache.getInt(CACHE_PAPER_ID, 0);
                                String mockTime = cache.getString(CACHE_MOCK_TIME, "");
                                // 跳转
                                Intent intent = new Intent(mContext, MeasureActivity.class);
                                intent.putExtra(INTENT_PAPER_ID, paperId);
                                intent.putExtra(INTENT_PAPER_TYPE, MOCK);
                                intent.putExtra(INTENT_MOCK_TIME, mockTime);
                                mContext.startActivity(intent);
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showMockCacheSubmitTimeOutAlert() {
        if (mContext == null || ((Activity) mContext).isFinishing()) return;
        String msg = "你的模考时间已到";
        String n = "放弃";
        String p = "提交";
        new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setNegativeButton(n, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearUserAnswerCache(mContext);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 提交做题数据
                                SharedPreferences cache = getMeasureCache(mContext);
                                int paperId = cache.getInt(CACHE_PAPER_ID, 0);
                                String paperType = cache.getString(CACHE_PAPER_TYPE, "");
                                String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
                                String redo = cache.getString(CACHE_REDO, "false");
                                new QRequest(mContext).submitPaper(
                                        ParamBuilder.submitPaper(
                                                String.valueOf(paperId),
                                                paperType,
                                                redo,
                                                String.valueOf(countDuration(userAnswer)),
                                                userAnswer,
                                                "done")
                                );
                                clearUserAnswerCache(mContext);
                                dialog.dismiss();
                            }
                        }).show();
    }

    private void showCacheSubmitAlert() {
        if (mContext == null || ((Activity) mContext).isFinishing()) return;
        String msg = "你有一次未完成的练习";
        String p = "去看记录";
        new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setPositiveButton(p,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 跳转至学习记录
                                if (mContext instanceof MainActivity) {
                                    ((MainActivity) mContext).recordRadioButton.setChecked(true);
                                }
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (AUTO_TRAINING.equals(apiName)) {
            dealAutoTrainingResp(response);
        } else if (NOTE_QUESTIONS.equals(apiName)) {
            dealNoteQuestionsResp(response);
        } else if (PAPER_EXERCISE.equals(apiName)) {
            dealPaperExerciseResp(response);
        } else if (SUBMIT_PAPER.equals(apiName)) {
            dealSubmitResp(response);
        } else if (HISTORY_EXERCISE_DETAIL.equals(apiName)) {
            dealHistoryExerciseDetail(response);
        } else if (SERVER_CURRENT_TIME.equals(apiName)) {
            dealServerCurrentTimeResp(response);
        } else if ("cache_submit_paper".equals(apiName)) {
            // 缓存提交
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
            if (resp == null || resp.getResponse_code() != 1) return;
            showCacheSubmitAlert();
        } else if ("mockpre_exam_info".equals(apiName)) {
            MockPreResp mockPreResp = GsonManager.getModel(response.toString(), MockPreResp.class);
            if (mockPreResp == null || mockPreResp.getResponse_code() != 1) return;
            String status = mockPreResp.getMock_status();
            if ("finish".equals(status)) {
                showMockCacheSubmitTimeOutAlert();
            } else {
                showMockCacheSubmitAlert();
            }
        }

        hideLoading();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

}
