package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.LegacyMeasureActivity;
import com.appublisher.quizbank.adapter.LegacyMeasureAdapter;
import com.appublisher.quizbank.common.measure.UserAnswerEntity;
import com.appublisher.quizbank.common.measure.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.common.vip.model.VipXCModel;
import com.appublisher.quizbank.common.vip.netdata.VipXCResp;
import com.appublisher.quizbank.dao.PaperDAO;
import com.appublisher.quizbank.model.netdata.CommonResp;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseResp;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 做题模块
 */
public class LegacyMeasureModel implements RequestCallback{

    private static boolean mOptionClick;
    private LegacyMeasureActivity mActivity;
    private Context mContext;

    private static final String CACHE_USER_ANSWER = "cache_user_answer";
    private static final String CACHE_PAPER_ID = "cache_paper_id";
    private static final String CACHE_PAPER_TYPE = "cache_paper_type";
    private static final String CACHE_REDO = "cache_redo";
    private static final String CACHE_MOCK_TIME = "cache_mock_time";
    private static final String CACHE_PAPER_NAME = "cache_paper_name";
    private static final String YAOGUO_MEASURE = "yaoguo_measure";

    public LegacyMeasureModel(LegacyMeasureActivity activity) {
        mActivity = activity;
    }

    public LegacyMeasureModel(Context context) {
        mContext = context;
    }

    /**
     * 获取小班智能组卷
     */
    public void getVipIntelligentPaper() {
        final VipXCModel xcModel = new VipXCModel(mActivity);
        xcModel.obtainIntelligentPaper(
                mActivity.mPaperId,
                new VipXCModel.IntelligentPaperListener() {
                    @Override
                    public void complete(JSONObject resp) {
                        if (resp == null) return;
                        VipXCResp xcResp = GsonManager.getModel(resp, VipXCResp.class);
                        if (xcResp == null || xcResp.getResponse_code() != 1) return;

                        ArrayList<VipXCResp.QuestionBean> questionBeanList = xcResp.getQuestion();
                        if (questionBeanList == null) return;

                        ArrayList<QuestionM> questions = new ArrayList<>();
                        for (VipXCResp.QuestionBean questionBean : questionBeanList) {
                            if (questionBean == null) continue;
                            String question = GsonManager.modelToString(questionBean);
                            QuestionM questionM = GsonManager.getModel(question, QuestionM.class);
                            if (questionM == null) continue;
                            questionM.setId(questionM.getQuestion_id());
                            questions.add(questionM);
                        }

                        mActivity.mQuestions = questions;
                        LegacyMeasureActivity.mDuration = xcResp.getDuration();
                        mActivity.setContent();
                    }
        });
    }

    /**
     * 获取View高度
     *
     * @param view View控件
     * @return 高度
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * 动态添加富文本(包含题号)
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public static void addRichTextToContainer(final Activity activity,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick,
                                              String questionPosition) {
        if (rich == null || rich.length() <= 0 || questionPosition == null) return;

        Request request = new Request(activity);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(activity);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        // 修改题号的样式
        String qNum = rich.substring(0, rich.indexOf("#%")) + "  ";
        TextView textView = new TextView(activity);
        Spannable word = new SpannableString(qNum);
        word.setSpan(
                new AbsoluteSizeSpan(Utils.sp2px(activity, 22)),
                0,
                qNum.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setTextColor(activity.getResources().getColor(R.color.common_text));
        textView.setText(word);
        flowLayout.addView(textView);

        // 分离题号
        rich = rich.substring(rich.indexOf("#%") + 2, rich.length());
        if (rich.length() == 0) return;

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(activity);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        for (final ParseManager.ParsedSegment segment : segments) {
            if (segment.text == null || segment.text.length() == 0) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(activity.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度

                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();

                        if (data == null) return;

                        // 对小于指定尺寸的图片进行放大(2倍)
                        int width = data.getWidth();
                        int height = data.getHeight();
                        if (height < minHeight) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(2.0f, 2.0f);
                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                        }

                        imgView.setImageBitmap(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                };

                request.loadImage(segment.text.toString(), imageListener);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        activity.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

    /**
     * 动态添加富文本
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public static void addRichTextToContainer(final Activity activity,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(activity);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(activity);
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
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(activity.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度

                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();

                        if (data == null) return;

                        // 对小于指定尺寸的图片进行放大(2倍)
                        int width = data.getWidth();
                        int height = data.getHeight();
                        if (height < minHeight) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(2.0f, 2.0f);
                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                        }

                        imgView.setImageBitmap(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                };

                request.loadImage(segment.text.toString(), imageListener);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        activity.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

    /**
     * 获取数据
     *
     * @param activity LegacyMeasureActivity
     */
    public static void getData(LegacyMeasureActivity activity) {
        if (activity.mPaperType == null) return;

        // 其他
        if (activity.mRedo) {
            // 重新做题
            // 提交时的字段是paperId，所有这里要统一
            activity.mPaperId = activity.mExerciseId;
            ProgressDialogManager.showProgressDialog(activity, true);
            LegacyMeasureActivity.mQRequest.getHistoryExerciseDetail(
                    activity.mExerciseId, activity.mPaperType);
        } else {
            // 做新题
            switch (activity.mPaperType) {
                case "auto":
                    ProgressDialogManager.showProgressDialog(activity, true);
                    LegacyMeasureActivity.mQRequest.getAutoTraining();
                    break;

                case "note":
                case "error":
                case "collect":
                    ProgressDialogManager.showProgressDialog(activity, true);
                    LegacyMeasureActivity.mQRequest.getNoteQuestions(
                            String.valueOf(activity.mHierarchyId),
                            activity.mPaperType);
                    break;

                case "evaluate":
                case "mock":
                case "entire":
                case "mokao":
                    ProgressDialogManager.showProgressDialog(activity, true);
                    LegacyMeasureActivity.mQRequest.getPaperExercise(
                            activity.mPaperId, activity.mPaperType);
                    break;
            }
        }
    }

    /**
     * 处理历史练习回调(用户已经做过一次后，请求的接口)
     *
     * @param activity LegacyMeasureActivity
     * @param response 回调数据
     */
    public void dealExerciseDetailResp(LegacyMeasureActivity activity,
                                              JSONObject response) {
        if (response == null) return;

        HistoryExerciseResp historyExerciseResp =
                GsonManager.getModel(response.toString(), HistoryExerciseResp.class);

        if (historyExerciseResp == null || historyExerciseResp.getResponse_code() != 1) return;

        ArrayList<CategoryM> categorys = historyExerciseResp.getCategory();
        ArrayList<QuestionM> questions = historyExerciseResp.getQuestions();

        if (categorys != null && categorys.size() != 0
                && historyExerciseResp.getQuestions() == null) {
            // 整卷
            activity.mQuestions = new ArrayList<>();
            activity.mEntirePaperCategory = new ArrayList<>();
            activity.mUserAnswerList = new ArrayList<>();
            // 用户答案缓存处理
            String cacheUserAnswer =
                    activity.getIntent().getStringExtra("cache_userAnswer");
            boolean isUseCache = false;
            if (cacheUserAnswer != null && cacheUserAnswer.length() > 0) {
                isUseCache = true;
            }

            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                CategoryM category = categorys.get(i);

                if (category == null) continue;

                ArrayList<QuestionM> categoryQuestions = category.getQuestions();
                String categoryName = category.getName();

                if (categoryQuestions == null || categoryQuestions.size() == 0) continue;

                activity.mQuestions.addAll(categoryQuestions);

                // 保存各个分类的数量(根据科目分组)
                HashMap<String, Integer> map = new HashMap<>();
                map.put(categoryName, categoryQuestions.size());
                activity.mEntirePaperCategory.add(map);

                if (!isUseCache) {
                    ArrayList<AnswerM> answers = category.getAnswers();
                    jointUserAnswer(categoryQuestions, answers, activity.mUserAnswerList);
                }
            }
            // 做题缓存处理
            if (isUseCache) {
                ArrayList<AnswerM> answers = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(cacheUserAnswer);
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jo = array.getJSONObject(i);
                        AnswerM answerM = GsonManager.getModel(jo, AnswerM.class);
                        answers.add(answerM);
                    }
                    jointUserAnswer(mActivity.mQuestions, answers, activity.mUserAnswerList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            // 非整卷
            if (questions == null || questions.size() == 0) return;

            // 初始化答案
            activity.mQuestions = questions;
            activity.mUserAnswerList = new ArrayList<>();
            ArrayList<AnswerM> answers = historyExerciseResp.getAnswers();
            jointUserAnswer(activity.mQuestions, answers, activity.mUserAnswerList);
        }

        // 倒计时
        LegacyMeasureActivity.mDuration =
                historyExerciseResp.getDuration() - historyExerciseResp.getStart_from();
        if (LegacyMeasureActivity.mockpre && LegacyMeasureActivity.mMockTime != null) {
            // 如果是模考则计算剩余时间
            time();
        } else {
            startTimer(activity);
        }

        // 设置ViewPager
        setViewPager(activity);
    }

    /**
     * 设置ViewPager
     */
    private void setViewPager(final LegacyMeasureActivity activity) {
        LegacyMeasureAdapter legacyMeasureAdapter = new LegacyMeasureAdapter(activity);
        activity.mViewPager.setAdapter(legacyMeasureAdapter);

        //noinspection deprecation
        activity.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveQuestionTime();
                activity.mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 执行跳转
        if (activity.mRedo && "study_record".equals(activity.mFrom)) {
            activity.mCurPosition = PaperDAO.getLastPosition(activity.mPaperId);
            activity.mViewPager.setCurrentItem(activity.mCurPosition);
        }
    }

    /**
     * 保存做题时间
     */
    public void saveQuestionTime() {
        int duration = (int) ((System.currentTimeMillis() - mActivity.mCurTimestamp) / 1000);
        mActivity.mCurTimestamp = System.currentTimeMillis();
        HashMap<String, Object> userAnswerMap =
                mActivity.mUserAnswerList.get(mActivity.mCurPosition);

        if (userAnswerMap.containsKey("duration")) {
            duration = duration + (int) userAnswerMap.get("duration");
        }

        userAnswerMap.put("duration", duration);
        mActivity.mUserAnswerList.set(mActivity.mCurPosition, userAnswerMap);
        updatePaperCache(mActivity.mUserAnswerList, mActivity.mCurPosition, mActivity.mRedo);
    }

    /**
     * 更新做题缓存（后期会去掉HashMap，这里暂时这样处理）
     * @param userAnswerList 用户答案
     * @param curIndex 当前索引
     */
    @SuppressLint("CommitPrefEdits")
    private void updatePaperCache(ArrayList<HashMap<String, Object>> userAnswerList,
                                  int curIndex,
                                  boolean redo) {
        if (userAnswerList == null || curIndex >= userAnswerList.size()) return;

        HashMap<String, Object> userAnswerMap = userAnswerList.get(curIndex);
        if (userAnswerMap == null) return;

        int id = (int) userAnswerMap.get("id");
        String answer = (String) userAnswerMap.get("answer");
        int category = (int) userAnswerMap.get("category_id");
        int note_id = (int) userAnswerMap.get("note_id");
        int duration = (int) userAnswerMap.get("duration");
        String right_answer = (String) userAnswerMap.get("right_answer");

        // 判断对错
        boolean is_right = false;
        if (answer == null) answer = "";
        if (answer.equals(right_answer)) {
            is_right = true;
        }

        SharedPreferences cache = getUserAnswerCache(mActivity);
        String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
        try {
            // 初始化JSONArray
            JSONArray userAnswerArray;
            if (userAnswer.length() == 0) {
                userAnswerArray = new JSONArray();
                for (HashMap<String, Object> itemMap : userAnswerList) {
                    if (itemMap == null) continue;
                    int itemId = (int) itemMap.get("id");
                    String itemAnswer = (String) itemMap.get("answer");
                    int itemCategory = (int) itemMap.get("category_id");
                    int itemNoteId = (int) itemMap.get("note_id");
                    int itemDuration = (int) itemMap.get("duration");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", itemId);
                    jsonObject.put("answer", itemAnswer);
                    jsonObject.put("is_right", false);
                    jsonObject.put("category", itemCategory);
                    jsonObject.put("note_id", itemNoteId);
                    jsonObject.put("duration", itemDuration);
                    userAnswerArray.put(jsonObject);
                }
            } else {
                userAnswerArray = new JSONArray(userAnswer);
            }
            // 更新
            // 构造JSONObject
            JSONObject curJO = new JSONObject();
            curJO.put("id", id);
            curJO.put("answer", answer);
            curJO.put("is_right", is_right);
            curJO.put("category", category);
            curJO.put("note_id", note_id);
            curJO.put("duration", duration);
            userAnswerArray.put(curIndex, curJO);

            String redoSubmit;
            if (redo) {
                redoSubmit = "true";
            } else {
                redoSubmit = "false";
            }

            SharedPreferences.Editor editor = cache.edit();
            editor.putString(CACHE_USER_ANSWER, userAnswerArray.toString());
            editor.putInt(CACHE_PAPER_ID, mActivity.mPaperId);
            editor.putString(CACHE_PAPER_TYPE, mActivity.mPaperType);
            editor.putString(CACHE_REDO, redoSubmit);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取做题答案缓存
     * @param context
     * @return
     */
    public static SharedPreferences getUserAnswerCache(Context context) {
        return context.getSharedPreferences(YAOGUO_MEASURE, Context.MODE_PRIVATE);
    }

    private JSONObject getAnswerObject(UserAnswerEntity entity) {
        JSONObject object = new JSONObject();
        if (entity == null) return object;

        try {
            // 判断对错
            String answer = entity.getAnswer();
            String rightAnswer = entity.getRight_answer();
            if (answer.equals(rightAnswer)) {
                entity.setIs_right(true);
            } else {
                entity.setIs_right(false);
            }

            object.put("id", entity.getId());
            object.put("answer", answer);
            object.put("is_right", entity.is_right());
            object.put("category", entity.getCategory());
            object.put("note_id", entity.getNote_id());
            object.put("duration", entity.getDuration());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * 倒计时启动
     */
    private static void startTimer(final LegacyMeasureActivity activity) {
        LegacyMeasureActivity.mMins = LegacyMeasureActivity.mDuration / 60;
        LegacyMeasureActivity.mSec = LegacyMeasureActivity.mDuration % 60;

        if (activity.mTimer != null) {
            activity.mTimer.cancel();
            activity.mTimer = null;
        }

        activity.mTimer = new Timer();
        activity.mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                LegacyMeasureActivity.mSec--;
                LegacyMeasureActivity.mDuration--;
                if (LegacyMeasureActivity.mSec < 0) {
                    LegacyMeasureActivity.mMins--;
                    LegacyMeasureActivity.mSec = 59;
                    LegacyMeasureActivity.mHandler.sendEmptyMessage(LegacyMeasureActivity.TIME_ON);
                    if (LegacyMeasureActivity.mMins < 0) {
                        activity.mTimer.cancel();
                        LegacyMeasureActivity.mHandler.sendEmptyMessage(LegacyMeasureActivity.TIME_OUT);
                    }
                } else {
                    LegacyMeasureActivity.mHandler.sendEmptyMessage(LegacyMeasureActivity.TIME_ON);
                }
            }
        }, 0, 1000);
    }

    /**
     * 倒计时2
     */
    private static void time() {
        Message message = LegacyMeasureActivity.mHandler.obtainMessage(LegacyMeasureActivity.TIME_ON_MOCK);
        LegacyMeasureActivity.mHandler.sendMessage(message);
    }

    /**
     * 提交答案(从做题页面Alert处的提交)
     *
     * @param activity LegacyMeasureActivity
     */
    public static void submitPaper(LegacyMeasureActivity activity) {
        int duration_total = 0;
        HashMap<String, Object> userAnswerMap;
        JSONArray questions = new JSONArray();
        String status;
        String redoSubmit;
        if (activity.mRedo) {
            redoSubmit = "true";
        } else {
            redoSubmit = "false";
        }

        if (activity.mUserAnswerList == null) return;

        int size = activity.mUserAnswerList.size();
        for (int i = 0; i < size; i++) {
            try {
                userAnswerMap = activity.mUserAnswerList.get(i);

                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                boolean is_right = false;
                int category = (int) userAnswerMap.get("category_id");
                int note_id = (int) userAnswerMap.get("note_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");

                // 判断对错
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = true;
                }

                // 统计总时长
                duration_total = duration_total + duration;

                JSONObject joQuestion = new JSONObject();
                joQuestion.put("id", id);
                joQuestion.put("answer", answer);
                joQuestion.put("is_right", is_right);
                joQuestion.put("category", category);
                joQuestion.put("note_id", note_id);
                joQuestion.put("duration", duration);
                questions.put(joQuestion);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (LegacyMeasureActivity.mockpre) {
            status = "done";
        } else {
            status = "undone";
        }

        new QRequest(activity).submitPaper(
                ParamBuilder.submitPaper(
                        String.valueOf(activity.mPaperId),
                        String.valueOf(activity.mPaperType),
                        redoSubmit,
                        String.valueOf(duration_total),
                        questions.toString(),
                        status)
        );
        ToastManager.showToast(activity, "交卷啦");
        LegacyMeasureModel.clearUserAnswerCache(activity);
    }

    /**
     * 拼接用户答案
     *
     * @param questions      题目
     * @param answers        用户答案
     * @param userAnswerList 用户答案List
     */
    public static void jointUserAnswer(ArrayList<QuestionM> questions,
                                       ArrayList<AnswerM> answers,
                                       ArrayList<HashMap<String, Object>> userAnswerList) {
        int size = questions.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();
            QuestionM question = questions.get(i);

            if (question != null) {
                map.put("id", question.getId());
                map.put("right_answer", question.getAnswer());
                map.put("note_id", question.getNote_id());
                map.put("category_id", question.getCategory_id());
                map.put("category_name", question.getCategory_name());
            } else {
                map.put("id", 0);
                map.put("right_answer", "right_answer");
                map.put("note_id", 0);
                map.put("category_id", 0);
                map.put("category_name", "科目");
            }

            if (answers == null || i >= answers.size() || answers.get(i) == null) {
                map.put("duration", 0);
                map.put("answer", "");
            } else {
                AnswerM answer = answers.get(i);
                map.put("duration", answer.getDuration());
                map.put("answer", answer.getAnswer());
            }

            if (userAnswerList != null) userAnswerList.add(map);
        }
    }

    /**
     * 直接提交答案
     *
     * @param activity AnswerSheetActivity
     */
    public static void autoSubmitPaper(LegacyMeasureActivity activity) {
        // 重置数据
        activity.mRightNum = 0;
        int duration_total = 0;
        HashMap<String, Object> userAnswerMap;
        JSONArray questions = new JSONArray();

        boolean redo = activity.getIntent().getBooleanExtra("redo", false);

        String redoSubmit;
        if (redo) {
            redoSubmit = "true";
        } else {
            redoSubmit = "false";
        }

        activity.mCategoryMap = new HashMap<>();

        if (activity.mUserAnswerList == null) return;

        activity.mTotalNum = activity.mUserAnswerList.size();
        for (int i = 0; i < activity.mTotalNum; i++) {
            try {
                userAnswerMap = activity.mUserAnswerList.get(i);

                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                boolean is_right = false;
                int category = (int) userAnswerMap.get("category_id");
                String category_name = (String) userAnswerMap.get("category_name");
                int note_id = (int) userAnswerMap.get("note_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");

                // 判断对错
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = true;
                    activity.mRightNum++;
                }

                // 统计总时长
                duration_total = duration_total + duration;

                JSONObject joQuestion = new JSONObject();
                joQuestion.put("id", id);
                joQuestion.put("answer", answer);
                joQuestion.put("is_right", is_right);
                joQuestion.put("category", category);
                joQuestion.put("note_id", note_id);
                joQuestion.put("duration", duration);
                questions.put(joQuestion);

                // 统计科目信息
                if (category_name != null
                        && activity.mCategoryMap.containsKey(category_name)) {
                    // 更新Map
                    HashMap<String, Object> map = activity.mCategoryMap.get(category_name);

                    int medium;

                    // 正确题目的数量
                    if (is_right) {
                        medium = (int) map.get("right_num");
                        medium++;
                        map.put("right_num", medium);
                    }

                    // 总数
                    medium = (int) map.get("total_num");
                    medium++;
                    map.put("total_num", medium);

                    // 总时长
                    medium = (int) map.get("duration_total");
                    medium = medium + duration;
                    map.put("duration_total", medium);

                    // 保存
                    activity.mCategoryMap.put(category_name, map);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    if (is_right) {
                        map.put("right_num", 1);
                    } else {
                        map.put("right_num", 0);
                    }
                    map.put("total_num", 1);
                    map.put("duration_total", duration);
                    activity.mCategoryMap.put(category_name, map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        activity.mPaperId = activity.getIntent().getIntExtra("paper_id", 0);

        ProgressDialogManager.showProgressDialog(activity, false);
        new QRequest(activity, activity).submitPaper(
                ParamBuilder.submitPaper(
                        String.valueOf(activity.mPaperId),
                        String.valueOf(activity.mPaperType),
                        redoSubmit,
                        String.valueOf(duration_total),
                        questions.toString(),
                        "done")
        );
        LegacyMeasureModel.clearUserAnswerCache(activity);
    }

    /**
     * 处理服务器时间回调
     * @param activity LegacyMeasureActivity
     * @param resp ServerCurrentTimeResp
     */
    public static void dealServerCurrentTimeResp(LegacyMeasureActivity activity,
                                                 ServerCurrentTimeResp resp) {
        if (resp == null || resp.getResponse_code() != 1) return;

        String serverTime = resp.getCurrent_time();

        if (serverTime == null || serverTime.length() == 0) return;
        if (LegacyMeasureActivity.mMockTime == null || LegacyMeasureActivity.mMockTime.length() == 0) return;

        long seconds = 0;

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition parsePosition = new ParsePosition(0);
            Date time = formatter.parse(LegacyMeasureActivity.mMockTime, parsePosition);
            seconds = time.getTime() - Long.parseLong(serverTime) * 1000;
        } catch (Exception e) {
            // Empty
        }

        seconds = seconds / 1000;

        if (activity.mPressBack) {
            // 如果是按了返回键获取的系统时间
            if (seconds > -(30 * 60)) {
                ToastManager.showToast(activity, "开考30分钟后才可以交卷");
            } else {
                activity.saveTest();
            }

            activity.mPressBack = false;

        } else {
            // 不是按了返回键（倒计时结束）
            if (seconds <= 0) {
                // 时间到
                AlertManager.mockTimeOutAlert(activity);
                LegacyMeasureModel.autoSubmitPaper(activity);
                if (activity.getSupportActionBar() == null) return;
                activity.getSupportActionBar().setTitle("00:00");
            }
        }
    }

    /**
     * 选项点击动作
     * @param textView 选项
     */
    public static void optionOnClickAction(LegacyMeasureAdapter adapter, final TextView textView) {
        if (mOptionClick) return;

        mOptionClick = true;
        adapter.resetOption();
        textView.setSelected(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.performClick();
                mOptionClick = false;
            }
        }, 100);
    }

    public void checkCache() {
        SharedPreferences cache = getUserAnswerCache(mContext);
        int paperId = cache.getInt(CACHE_PAPER_ID, 0);
        if (paperId == 0) return;

        String paperType = cache.getString(CACHE_PAPER_TYPE, "");
        String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
        String redo = cache.getString(CACHE_REDO, "true");
        if ("vip".equals(paperType)) return;

        if ("mock".equals(paperType)) {
            new QRequest(mContext, this).getMockPreExamInfo(String.valueOf(paperId));
        } else {
            // 提交做题数据
            new QRequest(mContext, this).cacheSubmitPaper(
                    ParamBuilder.submitPaper(
                            String.valueOf(paperId),
                            paperType,
                            redo,
                            String.valueOf(countDuration(userAnswer)),
                            userAnswer,
                            "undone")
            );
            clearUserAnswerCache(mContext);
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void updateMockTime(String mock_time) {
        SharedPreferences cache = getUserAnswerCache(mContext);
        SharedPreferences.Editor editor = cache.edit();
        editor.putString(CACHE_MOCK_TIME, mock_time);
        editor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    public void updatePaperName(String paper_name) {
        SharedPreferences cache = getUserAnswerCache(mContext);
        SharedPreferences.Editor editor = cache.edit();
        editor.putString(CACHE_PAPER_NAME, paper_name);
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
                            SharedPreferences cache = getUserAnswerCache(mContext);
                            int paperId = cache.getInt(CACHE_PAPER_ID, 0);
                            String userAnswer = cache.getString(CACHE_USER_ANSWER, "");
                            String mockTime = cache.getString(CACHE_MOCK_TIME, "");
                            String paperName = cache.getString(CACHE_PAPER_NAME, "");
                            // 跳转
                            Intent intent = new Intent(mContext, LegacyMeasureActivity.class);
                            intent.putExtra("from", "mockpre");
                            intent.putExtra("paper_id", paperId);
                            intent.putExtra("paper_type", "mock");
                            intent.putExtra("mock_time", mockTime);
                            intent.putExtra("paper_name", paperName);
                            intent.putExtra("cache_userAnswer", userAnswer);
                            intent.putExtra("redo", false);
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
                            SharedPreferences cache = getUserAnswerCache(mContext);
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

    @SuppressLint("CommitPrefEdits")
    public static void clearUserAnswerCache(Context context) {
        SharedPreferences cache = getUserAnswerCache(context);
        SharedPreferences.Editor editor = cache.edit();
        editor.putInt(CACHE_PAPER_ID, 0);
        editor.putString(CACHE_PAPER_TYPE, "");
        editor.putString(CACHE_USER_ANSWER, "");
        editor.putString(CACHE_REDO, "");
        editor.putString(CACHE_MOCK_TIME, "");
        editor.putString(CACHE_PAPER_NAME, "");
        editor.commit();
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

//    private void showCacheSubmitAlert() {
//        if (mContext == null || ((Activity) mContext).isFinishing()) return;
//        String msg = "你有一次未完成的练习";
//        String p = "去看记录";
//        new AlertDialog.Builder(mContext)
//                .setMessage(msg)
//                .setPositiveButton(p,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // 跳转至学习记录
//                                if (mContext instanceof MainActivity) {
//                                    ((MainActivity) mContext).changeFragment(5);
//                                }
//                                dialog.dismiss();
//                            }
//                        }).show();
//    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("cache_submit_paper".equals(apiName)) {
            // 缓存提交
            CommonResp resp = GsonManager.getModel(response, CommonResp.class);
//            if (resp == null || resp.getResponse_code() != 1) return;
//            showCacheSubmitAlert();
        } else if ("mockpre_exam_info".equals(apiName)) {
//            MockPreResp mockPreResp = GsonManager.getModel(response.toString(), MockPreResp.class);
//            if (mockPreResp == null || mockPreResp.getResponse_code() != 1) return;
//            String status = mockPreResp.getMock_status();
//            if ("finish".equals(status)) {
//                showMockCacheSubmitTimeOutAlert();
//            } else {
//                showMockCacheSubmitAlert();
//            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }

}
