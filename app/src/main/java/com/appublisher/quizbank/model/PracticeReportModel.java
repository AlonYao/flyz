package com.appublisher.quizbank.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseResp;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.GsonManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习报告模块 Model
 */
public class PracticeReportModel {

    private static PracticeReportActivity mActivity;
    private static HashMap<String, HashMap<String, Object>> mCategoryMap;
    private static ArrayList<NoteM> mNotes;
    private static ArrayList<QuestionM> mQuestions;
    private static ArrayList<HashMap<String, Object>> mUserAnswerList;
    private static int mRightNum;
    private static int mTotalNum;

    /**
     * 获取数据
     * @param activity PracticeReportActivity
     */
    public static void getData(final PracticeReportActivity activity) {
        mActivity = activity;

        activity.mPaperName = activity.getIntent().getStringExtra("paper_name");
        activity.mPaperType = activity.getIntent().getStringExtra("paper_type");
        mRightNum = activity.getIntent().getIntExtra("right_num", 0);
        mTotalNum = activity.getIntent().getIntExtra("total_num", 0);
        //noinspection unchecked
        mCategoryMap = (HashMap<String, HashMap<String, Object>>)
                        activity.getIntent().getSerializableExtra("category");
        //noinspection unchecked
        mNotes = (ArrayList<NoteM>) activity.getIntent().getSerializableExtra("notes");
        //noinspection unchecked
        mQuestions = (ArrayList<QuestionM>) activity.getIntent().getSerializableExtra("questions");

        //noinspection unchecked
        mUserAnswerList = (ArrayList<HashMap<String, Object>>)
                activity.getIntent().getSerializableExtra("user_answer");

        // 显示内容
        setContent();
    }

    /**
     * 设置内容
     */
    private static void setContent() {
        mActivity.mTvPaperName.setText(mActivity.mPaperName);
        mActivity.mTvRightNum.setText(String.valueOf(mRightNum));
        mActivity.mTvTotalNum.setText(String.valueOf(mTotalNum));

        // 添加科目
        addCategory();

        // 添加知识点变化
        addNote();

        // 全部解析
        mActivity.mTvAll.setOnClickListener(allOnClick);

        // 错题解析
        if (mRightNum == mTotalNum) {
            // 没有错题
            mActivity.mTvError.setOnClickListener(null);
            mActivity.mTvError.setBackgroundResource(R.color.practice_report_error_gray);
        } else {
            mActivity.mTvError.setBackgroundResource(R.color.practice_report_error);
            mActivity.mTvError.setOnClickListener(errorOnClick);
        }
    }

    /**
     * 全部点击事件
     */
    private static View.OnClickListener allOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mQuestions == null || mQuestions.size() == 0) return;

            int size = mQuestions.size();
            ArrayList<AnswerM> answers = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                AnswerM answerItem = new AnswerM();

                HashMap<String, Object> userAnswerMap = mUserAnswerList.get(i);
                if (userAnswerMap == null) {
                    answerItem.setId(0);
                    answerItem.setAnswer("");
                    answerItem.setIs_right(false);
                } else {
                    answerItem.setId((int) userAnswerMap.get("id"));

                    String userAnswer = (String) userAnswerMap.get("answer");
                    answerItem.setAnswer(userAnswer);

                    String rightAnswer = (String) userAnswerMap.get("right_answer");

                    if (userAnswer != null && rightAnswer != null
                            && userAnswer.equals(rightAnswer)) {
                        answerItem.setIs_right(true);
                    } else {
                        answerItem.setIs_right(false);
                    }
                }

                answers.add(answerItem);
            }

            Intent intent = new Intent(mActivity, MeasureAnalysisActivity.class);
            intent.putExtra("questions", mQuestions);
            intent.putExtra("answers", answers);
            intent.putExtra("analysis_type", mActivity.mPaperType);
            mActivity.startActivity(intent);

            mActivity.finish();
        }
    };

    /**
     * 错题点击事件
     */
    private static View.OnClickListener errorOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUserAnswerList == null || mUserAnswerList.size() == 0) return;

            ArrayList<QuestionM> errorQuestions = new ArrayList<>();
            ArrayList<AnswerM> errorAnswers = new ArrayList<>();

            int size = mUserAnswerList.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, Object> userAnswerMap = mUserAnswerList.get(i);

                if (userAnswerMap == null) continue;

                String userAnswer = (String) userAnswerMap.get("answer");
                String rightAnswer = (String) userAnswerMap.get("right_answer");

                if (userAnswer != null && rightAnswer != null
                        && !userAnswer.equals(rightAnswer)) {
                    AnswerM answerItem = new AnswerM();
                    answerItem.setId((int) userAnswerMap.get("id"));
                    answerItem.setAnswer(userAnswer);
                    answerItem.setIs_right(false);

                    if (mQuestions == null || i >= mQuestions.size()) {
                        errorQuestions.add(new QuestionM());
                    } else {
                        errorQuestions.add(mQuestions.get(i));
                    }

                    errorAnswers.add(answerItem);
                }
            }

            Intent intent = new Intent(mActivity, MeasureAnalysisActivity.class);
            intent.putExtra("questions", errorQuestions);
            intent.putExtra("answers", errorAnswers);
            mActivity.startActivity(intent);

            mActivity.finish();
        }
    };

    /**
     * 添加知识点
     */
    private static void addNote() {
        if (mNotes == null || mNotes.size() == 0) {
            mActivity.mTvNoteNoChange.setVisibility(View.VISIBLE);
            mActivity.mLlNoteContainer.setVisibility(View.GONE);
        } else {
            mActivity.mTvNoteNoChange.setVisibility(View.GONE);
            mActivity.mLlNoteContainer.setVisibility(View.VISIBLE);

            int size = mNotes.size();
            for (int i = 0; i < size; i++) {
                NoteM note = mNotes.get(i);
                if (note == null) continue;

                View child = LayoutInflater.from(mActivity).inflate(
                        R.layout.practice_report_note, mActivity.mLlCategoryContainer, false);

                TextView tvNoteName = (TextView) child.findViewById(R.id.practice_report_note);
                ImageView ivLevelPre =
                        (ImageView) child.findViewById(R.id.practice_report_note_pre);
                ImageView ivLevelNow =
                        (ImageView) child.findViewById(R.id.practice_report_note_now);
                ImageView ivLevelChange =
                        (ImageView) child.findViewById(R.id.practice_report_note_change);

                tvNoteName.setText(note.getName());

                int levelPre = note.getFrom();
                int levelNow = note.getTo();

                setLevelImg(levelPre, ivLevelPre);
                setLevelImg(levelNow, ivLevelNow);

                if (levelPre > levelNow) {
                    ivLevelChange.setImageResource(R.drawable.practice_report_down);
                } else {
                    ivLevelChange.setImageResource(R.drawable.practice_report_up);
                }

                mActivity.mLlNoteContainer.addView(child);
            }
        }
    }

    /**
     * 添加科目
     */
    private static void addCategory() {
        if (mCategoryMap == null) return;

        for (Object o : mCategoryMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String categoryName = (String) entry.getKey();
            //noinspection unchecked
            HashMap<String, Object> map = (HashMap<String, Object>) entry.getValue();

            if (categoryName != null && map != null) {
                View child = LayoutInflater.from(mActivity).inflate(
                        R.layout.practice_report_category,
                        mActivity.mLlCategoryContainer, false);

                TextView tvCategoryName =
                        (TextView) child.findViewById(R.id.practice_report_category);
                TextView tvCategoryRightNum =
                        (TextView) child.findViewById(R.id.practice_report_category_rightnum);
                TextView tvCategoryTotalNum =
                        (TextView) child.findViewById(R.id.practice_report_category_totalnum);
                TextView tvCategoryTime =
                        (TextView) child.findViewById(R.id.practice_report_category_time);
                ImageView ivCategoryPb =
                        (ImageView) child.findViewById(R.id.practice_report_category_pb);

                tvCategoryName.setText(categoryName);

                int categoryRightNum = (int) map.get("right_num");
                int categoryTotalNum = (int) map.get("total_num");
                int categoryDuration = (int) map.get("duration_total");

                tvCategoryRightNum.setText(String.valueOf(categoryRightNum));
                tvCategoryTotalNum.setText(String.valueOf(categoryTotalNum));

                if (categoryTotalNum != 0) {
                    int pbLength = (categoryRightNum * 146) / categoryTotalNum;
                    if (pbLength == 0) pbLength++;
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.width = pbLength * 2;
                    lp.height = 10;
                    ivCategoryPb.setLayoutParams(lp);

                    int ratio = categoryDuration / categoryTotalNum;
                    tvCategoryTime.setText(String.valueOf(ratio));
                }

                mActivity.mLlCategoryContainer.addView(child);
            }
        }
    }

    /**
     * 设置知识点变化img
     * @param level 知识点等级
     * @param view 知识点等级view
     */
    private static void setLevelImg(int level, ImageView view) {
        if (level == 0) {
            view.setImageResource(R.drawable.practice_report_level0);
        } else if (level == 1) {
            view.setImageResource(R.drawable.practice_report_level1);
        } else if (level == 2) {
            view.setImageResource(R.drawable.practice_report_level2);
        } else if (level == 3) {
            view.setImageResource(R.drawable.practice_report_level3);
        } else if (level == 4) {
            view.setImageResource(R.drawable.practice_report_level4);
        } else if (level == 5) {
            view.setImageResource(R.drawable.practice_report_level5);
        } else {
            view.setImageResource(R.drawable.practice_report_level0);
        }
    }

    /**
     * 处理练习历史信息接口回调
     * @param response 回调数据
     */
    public static void dealHistoryExerciseDetailResp(PracticeReportActivity activity,
                                                     JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        HistoryExerciseResp historyExerciseResp =
                gson.fromJson(response.toString(), HistoryExerciseResp.class);

        if (historyExerciseResp == null || historyExerciseResp.getResponse_code() != 1) return;

        mQuestions = historyExerciseResp.getQuestions();
        ArrayList<CategoryM> categorys = historyExerciseResp.getCategory();

        mActivity = activity;

        if (mQuestions != null && categorys == null) {
            // 非整卷
            ArrayList<AnswerM> answers = historyExerciseResp.getAnswers();
            jointUserAnswer(answers);
        } else if (mQuestions == null && categorys != null) {
            // 整卷
            mQuestions = new ArrayList<>();

            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                CategoryM category = categorys.get(i);

                if (category == null) continue;

                ArrayList<QuestionM> categoryQuestions = category.getQuestions();
                String categoryName = category.getName();

                if (categoryQuestions == null || categoryQuestions.size() == 0) continue;

                mQuestions.addAll(categoryQuestions);

                // 拼接用户答案
                ArrayList<AnswerM> answers = category.getAnswers();
                jointUserAnswer(answers);
            }
        }

        // 拼接科目
        jointCategoryMap();

        // 设置内容
        setContent();
    }

    /**
     * 拼接科目
     */
    private static void jointCategoryMap() {
        if (mUserAnswerList == null) return;

        mTotalNum = mUserAnswerList.size();

        HashMap<String, Object> userAnswerMap;
        mCategoryMap = new HashMap<>();

        for (int i = 0; i < mTotalNum; i++) {
            userAnswerMap = mUserAnswerList.get(i);

            String category_name = (String) userAnswerMap.get("category_name");
            String answer = (String) userAnswerMap.get("answer");
            String right_answer = (String) userAnswerMap.get("right_answer");
            int duration = (int) userAnswerMap.get("duration");
            boolean is_right = false;

            // 判断对错
            if (answer != null && right_answer != null
                    && !"".equals(answer) && answer.equals(right_answer)) {
                is_right = true;
                mRightNum++;
            }

            // 统计科目信息
            if (category_name != null
                    && mCategoryMap.containsKey(category_name)) {
                // 更新Map
                HashMap<String, Object> map = mCategoryMap.get(category_name);

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
                mCategoryMap.put(category_name, map);
            } else {
                HashMap<String, Object> map = new HashMap<>();
                if (is_right) {
                    map.put("right_num", 1);
                } else {
                    map.put("right_num", 0);
                }
                map.put("total_num", 1);
                map.put("duration_total", duration);
                mCategoryMap.put(category_name, map);
            }
        }
    }

    /**
     * 拼接用户答案
     * @param answers 用户答案
     */
    private static void jointUserAnswer(ArrayList<AnswerM> answers) {
        mUserAnswerList = new ArrayList<>();

        int size = mQuestions.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();
            QuestionM question = mQuestions.get(i);

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

            mUserAnswerList.add(map);
        }
    }
}
