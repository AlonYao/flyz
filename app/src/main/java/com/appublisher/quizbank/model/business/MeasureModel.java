package com.appublisher.quizbank.model.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.ScaleImageActivity;
import com.appublisher.quizbank.adapter.MeasureAdapter;
import com.appublisher.quizbank.dao.PaperDAO;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseEntireResp;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseResp;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.Utils;
import com.google.gson.Gson;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 做题模块
 */
public class MeasureModel {

    /**
     * 获取View高度
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
     * @param activity Activity
     * @param container 富文本控件容器
     * @param rich 富文本
     */
    public static void addRichTextToContainer(final Activity activity,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick,
                                              String questionPosition) {
        if (rich == null || rich.length() <= 0 || questionPosition == null) return;

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
            if (TextUtils.isEmpty(segment.text)) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);

                if (rich.toLowerCase().contains(questionPosition.toLowerCase())) {
                    Spannable word = new SpannableString(segment.text);
                    word.setSpan(
                            new AbsoluteSizeSpan(Utils.sp2px(activity, 22)),
                            0,
                            questionPosition.length() - 1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    word.setSpan(
                            new AbsoluteSizeSpan(Utils.sp2px(activity, 17)),
                            questionPosition.length(),
                            segment.text.length(),
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    textView.setTextColor(activity.getResources().getColor(R.color.setting_text));
                    textView.setText(word);
                } else {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    textView.setTextColor(activity.getResources().getColor(R.color.setting_text));
                    textView.setText(segment.text);
                }

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
                final float minHeight = (float) ((dm.heightPixels - 50)*0.2); // 50是状态栏高度

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
                            data = Bitmap.createBitmap(data, 0, 0 ,width, height, matrix, true);
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
     * @param activity Activity
     * @param container 富文本控件容器
     * @param rich 富文本
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
            if (TextUtils.isEmpty(segment.text)) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(activity.getResources().getColor(R.color.setting_text));
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
                final float minHeight = (float) ((dm.heightPixels - 50)*0.2); // 50是状态栏高度

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
                            data = Bitmap.createBitmap(data, 0, 0 ,width, height, matrix, true);
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
     * @param activity MeasureActivity
     */
    public static void getData(MeasureActivity activity) {
        if (activity.mPaperType == null) return;

        Request request = new Request(activity, activity);

        if (activity.mRedo) {
            // 重新做题
            // 提交时的字段是paperId，所有这里要统一
            activity.mPaperId = activity.mExerciseId;

            ProgressDialogManager.showProgressDialog(activity, true);
            request.getHistoryExerciseDetail(activity.mExerciseId, activity.mPaperType);
        } else {
            // 做新题
            switch (activity.mPaperType) {
                case "auto":
                    ProgressDialogManager.showProgressDialog(activity, true);
                    request.getAutoTraining();
                    break;

                case "note":
                case "error":
                case "collect":
                    switch (activity.mHierarchyLevel) {
                        case 1:
                            ProgressDialogManager.showProgressDialog(activity, true);
                            request.getNoteQuestions(String.valueOf(activity.mHierarchyId), "", "",
                                    activity.mPaperType);

                            break;

                        case 2:
                            ProgressDialogManager.showProgressDialog(activity, true);
                            request.getNoteQuestions("", String.valueOf(activity.mHierarchyId), "",
                                    activity.mPaperType);

                            break;

                        case 3:
                            ProgressDialogManager.showProgressDialog(activity, true);
                            request.getNoteQuestions("", "", String.valueOf(activity.mHierarchyId),
                                    activity.mPaperType);

                            break;
                    }
                    break;

                case "entire":
                case "mokao":
                    ProgressDialogManager.showProgressDialog(activity, true);
                    request.getPaperExercise(activity.mPaperId, activity.mPaperType);
                    break;
            }
        }
    }

    /**
     * 处理历史练习回调(用户已经做过一次后，请求的接口)
     * @param activity MeasureActivity
     * @param response 回调数据
     */
    public static void dealExerciseDetailResp(MeasureActivity activity,
                                                     JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();

        if ("entire".equals(activity.mPaperType)) {
            // 整卷
            HistoryExerciseEntireResp historyExerciseEntireResp =
                    gson.fromJson(response.toString(), HistoryExerciseEntireResp.class);

            if (historyExerciseEntireResp == null
                    || historyExerciseEntireResp.getResponse_code() != 1) return;

            ArrayList<CategoryM> categorys = historyExerciseEntireResp.getCategory();

            if (categorys == null || categorys.size() == 0) return;

            // 整卷
            activity.mQuestions = new ArrayList<>();
            activity.mEntirePaperCategory = new ArrayList<>();
            activity.mUserAnswerList = new ArrayList<>();

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

                // 拼接用户答案
                ArrayList<AnswerM> answers = category.getAnswers();
                jointUserAnswer(categoryQuestions, answers, activity.mUserAnswerList);
            }

            // 倒计时时间
            activity.mDuration = historyExerciseEntireResp.getDuration()
                    - historyExerciseEntireResp.getStart_from();

        } else {
            // 非整卷
            HistoryExerciseResp historyExerciseResp =
                    gson.fromJson(response.toString(), HistoryExerciseResp.class);

            if (historyExerciseResp == null || historyExerciseResp.getResponse_code() != 1) return;

            activity.mQuestions = historyExerciseResp.getQuestions();

            if (activity.mQuestions == null || activity.mQuestions.size() == 0) return;

            // 初始化答案
            activity.mUserAnswerList = new ArrayList<>();
            ArrayList<AnswerM> answers = historyExerciseResp.getAnswers();
            jointUserAnswer(activity.mQuestions, answers, activity.mUserAnswerList);

            // 获取时长
            activity.mDuration =
                    historyExerciseResp.getDuration() - historyExerciseResp.getStart_from();
        }

        // 倒计时
        startTimer(activity);

        // 设置ViewPager
        setViewPager(activity);
    }

    /**
     * 设置ViewPager
     */
    private static void setViewPager(final MeasureActivity activity) {
        MeasureAdapter measureAdapter = new MeasureAdapter(activity);
        activity.mViewPager.setAdapter(measureAdapter);

        activity.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveQuestionTime(activity);
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
    public static void saveQuestionTime(MeasureActivity activity) {
        int duration = (int) ((System.currentTimeMillis() - activity.mCurTimestamp) / 1000);
        activity.mCurTimestamp = System.currentTimeMillis();
        HashMap<String, Object> userAnswerMap =
                activity.mUserAnswerList.get(activity.mCurPosition);

        if (userAnswerMap.containsKey("duration")) {
            duration = duration + (int) userAnswerMap.get("duration");
        }

        userAnswerMap.put("duration", duration);
        activity.mUserAnswerList.set(activity.mCurPosition, userAnswerMap);
    }

    /**
     * 倒计时启动
     */
    private static void startTimer(final MeasureActivity activity) {
        MeasureActivity.mMins = activity.mDuration / 60;
        MeasureActivity.mSec = activity.mDuration % 60;

        if (activity.mTimer != null) {
            activity.mTimer.cancel();
            activity.mTimer = null;
        }

        activity.mTimer = new Timer();
        activity.mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                MeasureActivity.mSec--;
                activity.mDuration--;
                if (MeasureActivity.mSec < 0) {
                    MeasureActivity.mMins--;
                    MeasureActivity.mSec = 59;
                    activity.mHandler.sendEmptyMessage(MeasureActivity.TIME_ON);
                    if (MeasureActivity.mMins < 0) {
                        activity.mTimer.cancel();
                        activity.mHandler.sendEmptyMessage(MeasureActivity.TIME_OUT);
                    }
                } else {
                    activity.mHandler.sendEmptyMessage(MeasureActivity.TIME_ON);
                }
            }
        }, 0, 1000);
    }

    /**
     * 提交答案(从做题页面Alert处的提交)
     * @param activity MeasureActivity
     */
    public static void submitPaper(MeasureActivity activity) {
        int duration_total = 0;
        HashMap<String, Object> userAnswerMap;
        JSONArray questions = new JSONArray();

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

        new Request(activity).submitPaper(
                ParamBuilder.submitPaper(
                        String.valueOf(activity.mPaperId),
                        String.valueOf(activity.mPaperType),
                        redoSubmit,
                        String.valueOf(duration_total),
                        questions.toString(),
                        "undone")
        );
    }

    /**
     * 拼接用户答案
     * @param questions 题目
     * @param answers 用户答案
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
}