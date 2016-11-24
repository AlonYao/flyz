package com.appublisher.quizbank.common.measure.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureExcludeBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;
import com.appublisher.quizbank.common.measure.bean.MeasureTabBean;
import com.appublisher.quizbank.common.measure.netdata.MeasureAutoResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureEntireResp;
import com.appublisher.quizbank.common.measure.netdata.MeasureNotesResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：管理类
 */

public class MeasureModel implements RequestCallback, MeasureConstants {

    public int mPaperId;
    public int mHierarchyId;
    public int mCurPagePosition;
    public boolean mRedo;
    public long mCurTimestamp;
    public String mPaperType;
    public List<MeasureExcludeBean> mExcludes;
    public MeasureRequest mRequest;
    public Context mContext;
    public SparseIntArray mFinalHeightMap;
    public List<MeasureTabBean> mTabs;
    public List<MeasureSubmitBean> mSubmits;

    public MeasureModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
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
        if (AUTO.equals(mPaperType)) {
            mRequest.getAutoTraining();
        } else if (NOTE.equals(mPaperType)) {
            mRequest.getNoteQuestions(mHierarchyId, NOTE);
        } else if (ENTIRE.equals(mPaperType)) {
            mRequest.getPaperExercise(mPaperId, mPaperType);
        }
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
        List<MeasureQuestionBean> questions = setQuestionOrder(resp.getQuestions(), 0);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 处理每日模考&整卷
     * @param response JSONObject
     */
    private void dealPaperExerciseResp(JSONObject response) {
        MeasureEntireResp resp = GsonManager.getModel(response, MeasureEntireResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        List<MeasureEntireResp.CategoryBean> categorys = resp.getCategory();
        if (categorys == null) return;

        // 构造数据结构
        mTabs = new ArrayList<>();
        List<MeasureQuestionBean> questions = new ArrayList<>();

        // 遍历
        int size = categorys.size();
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
        questions = setQuestionOrder(questions, size);

        if (!(mContext instanceof MeasureActivity)) return;
        ((MeasureActivity) mContext).showTabLayout(mTabs);
        ((MeasureActivity) mContext).showViewPager(questions);
    }

    /**
     * 设置题号&索引(同时初始化用户记录)
     * @param list List<MeasureQuestionBean>
     * @return List<MeasureQuestionBean>
     */
    private List<MeasureQuestionBean> setQuestionOrder(List<MeasureQuestionBean> list, int descSize) {
        if (list == null) return new ArrayList<>();
        int size = list.size();
        int amount = size - descSize;
        int order = 0;
        mExcludes = new ArrayList<>();
        mSubmits = new ArrayList<>();

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
            mSubmits.add(submitBean);
        }

        // 缓存
        saveSubmitPaperInfo();
        saveUserAnswerCache(mContext, mSubmits);

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
     * 是否有选中记录
     * @return boolean
     */
    public boolean hasRecord() {
        List<MeasureSubmitBean> submits = getUserAnswerCache(mContext);
        if (submits == null) return false;

        for (MeasureSubmitBean submit : submits) {
            if (submit == null) continue;
            if (submit.getAnswer().length() > 0) return true;
        }

        return false;
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

                // 构建note_ids
                List<Integer> noteIdList = new ArrayList<>();
                JSONArray noteIdArray = object.getJSONArray(SUBMIT_NOTE_IDS);
                int arrayLength = noteIdArray.length();
                for (int j = 0; j < arrayLength; j++) {
                    noteIdList.add(noteIdArray.getInt(j));
                }
                submitBean.setNote_ids(noteIdList);
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
                if (noteIds == null) continue;
                JSONArray noteIdArray = new JSONArray();
                for (Integer noteId : noteIds) {
                    noteIdArray.put(noteId);
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
     * @param position 此处的position指的是题目的索引
     */
    public void saveSubmitDuration(int position) {
        if (!(mContext instanceof MeasureActivity)) return;

        List<MeasureQuestionBean> questions = ((MeasureActivity) mContext).mAdapter.getQuestions();
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

        mCurPagePosition = position;
        mCurTimestamp = System.currentTimeMillis();
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

                // 异步加载图片
//                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
//                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度
//
//                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        Bitmap data = imageContainer.getBitmap();
//
//                        if (data == null) return;
//
//                        // 对小于指定尺寸的图片进行放大(2倍)
//                        int width = data.getWidth();
//                        int height = data.getHeight();
//                        if (height < minHeight) {
//                            Matrix matrix = new Matrix();
//                            matrix.postScale(2.0f, 2.0f);
//                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
//                        }
//
//                        imgView.setImageBitmap(data);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                };
//
//                request.loadImage(segment.text.toString(), imageListener);

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

    public void submit() {
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

//        mRequest.submitPaper(MeasureParamBuilder.submitPaper(
//                paperId, paperTpye, redo, durtion, answer, "done"));
    }

    /**
     * 获取用于答题卡使用的Questions
     * @param list List<MeasureQuestionBean>
     * @return String
     */
    public String getQuestionsForSheet(List<MeasureQuestionBean> list) {
        if (list == null) return "";
        int size = list.size();

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < size; i++) {
            String bean = GsonManager.modelToString(list.get(i));
            try {
                JSONObject jsonObject = new JSONObject(bean);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
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
            // Empty
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
