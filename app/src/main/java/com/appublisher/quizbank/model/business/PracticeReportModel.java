package com.appublisher.quizbank.model.business;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.model.entity.MeasureEntity;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseResp;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习报告模块 Model
 */
public class PracticeReportModel {

    private static PracticeReportActivity mActivity;

    /**
     * 获取数据
     * @param activity PracticeReportActivity
     */
    public static void getData(final PracticeReportActivity activity) {
        mActivity = activity;

        activity.mRightNum = activity.getIntent().getIntExtra("right_num", 0);
        activity.mTotalNum = activity.getIntent().getIntExtra("total_num", 0);
        //noinspection unchecked
        activity.mCategoryMap = (HashMap<String, HashMap<String, Object>>)
                        activity.getIntent().getSerializableExtra("category");
        //noinspection unchecked
        activity.mNotes = (ArrayList<NoteM>) activity.getIntent().getSerializableExtra("notes");

        //noinspection unchecked
        activity.mQuestions = (ArrayList<QuestionM>)
                activity.getIntent().getSerializableExtra("questions");

        //noinspection unchecked
        activity.mUserAnswerList = (ArrayList<HashMap<String, Object>>)
                activity.getIntent().getSerializableExtra("user_answer");

        // 显示内容
        setContent();
    }

    /**
     * 设置内容
     */
    private static void setContent() {
        // 如果是今日Mini模考，显示击败信息
        if ("mokao".equals(mActivity.mPaperType)) {
            mActivity.mRlMiniMokao.setVisibility(View.VISIBLE);
            String defeat = "击败"
                    + Utils.rateToPercent(mActivity.mMeasureEntity.getDefeat())
                    + "%的考生";
            Spannable word = new SpannableString(defeat);
            word.setSpan(
                    new AbsoluteSizeSpan(Utils.sp2px(mActivity, 22)),
                    2,
                    defeat.indexOf("的考生"),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word.setSpan(
                    new ForegroundColorSpan(
                            mActivity.getResources().getColor(R.color.practice_report_all)),
                    2,
                    defeat.indexOf("的考生"),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mActivity.mTvMiniMokaoRank.setText(word);
        } else {
            mActivity.mRlMiniMokao.setVisibility(View.GONE);
        }

        // 添加科目
        addCategory();

        if ("evaluate".equals(mActivity.mPaperType)) {
            // 估分：显示往年分数线、你的估分，不显示做对/全部、知识点变化
            mActivity.mLlRatio.setVisibility(View.GONE);
            mActivity.mLlNoteContainer.setVisibility(View.GONE);
            mActivity.mLlBorderLine.setVisibility(View.VISIBLE);
        } else {
            mActivity.mLlRatio.setVisibility(View.VISIBLE);
            mActivity.mLlNoteContainer.setVisibility(View.VISIBLE);
            mActivity.mLlBorderLine.setVisibility(View.GONE);

            // 正确率
            mActivity.mTvRightNum.setText(String.valueOf(mActivity.mRightNum));
            mActivity.mTvTotalNum.setText(String.valueOf(mActivity.mTotalNum));

            // 知识点变化
            addNote();
        }

        // 全部解析
        mActivity.mTvAll.setOnClickListener(allOnClick);

        // 错题解析
        if (mActivity.mRightNum == mActivity.mTotalNum) {
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
            if (mActivity.mQuestions == null || mActivity.mQuestions.size() == 0) return;

            int size = mActivity.mQuestions.size();
            ArrayList<AnswerM> answers = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                AnswerM answerItem = new AnswerM();

                HashMap<String, Object> userAnswerMap = mActivity.mUserAnswerList.get(i);
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

            // Umeng
            mActivity.mUmengStatus = "2";

            // 跳转
            mActivity.mIsFromError = false;
            skipToMeasureAnalysisActivity(mActivity.mQuestions, answers);
        }
    };

    /**
     * 错题点击事件
     */
    private static View.OnClickListener errorOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mActivity.mUserAnswerList == null || mActivity.mUserAnswerList.size() == 0) return;

            ArrayList<QuestionM> errorQuestions = new ArrayList<>();
            ArrayList<AnswerM> errorAnswers = new ArrayList<>();

            int size = mActivity.mUserAnswerList.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, Object> userAnswerMap = mActivity.mUserAnswerList.get(i);

                if (userAnswerMap == null) continue;

                String userAnswer = (String) userAnswerMap.get("answer");
                String rightAnswer = (String) userAnswerMap.get("right_answer");

                if (userAnswer != null && rightAnswer != null
                        && !userAnswer.equals(rightAnswer)) {
                    AnswerM answerItem = new AnswerM();
                    answerItem.setId((int) userAnswerMap.get("id"));
                    answerItem.setAnswer(userAnswer);
                    answerItem.setIs_right(false);

                    if (mActivity.mQuestions == null || i >= mActivity.mQuestions.size()) {
                        errorQuestions.add(new QuestionM());
                    } else {
                        errorQuestions.add(mActivity.mQuestions.get(i));
                    }

                    errorAnswers.add(answerItem);
                }
            }

            // Umeng
            mActivity.mUmengStatus = "3";

            // 跳转
            mActivity.mIsFromError = true;
            skipToMeasureAnalysisActivity(errorQuestions, errorAnswers);
        }
    };

    /**
     * 跳转到做题解析页面
     * @param questions 题目
     * @param answers 答案
     */
    private static void skipToMeasureAnalysisActivity(ArrayList<QuestionM> questions,
                                                      ArrayList<AnswerM> answers) {
        Intent intent = new Intent(mActivity, MeasureAnalysisActivity.class);
        intent.putExtra("questions", questions);
        intent.putExtra("answers", answers);
        intent.putExtra("paper_name", mActivity.mPaperName);
        intent.putExtra("analysis_type", mActivity.mPaperType);
        intent.putExtra("hierarchy_id", mActivity.mHierarchyId);
        intent.putExtra("hierarchy_level", mActivity.mHierarchyLevel);
        intent.putExtra("from", mActivity.mFrom);
        intent.putExtra("is_from_error", mActivity.mIsFromError);

        // Umeng
        intent.putExtra("umeng_entry", mActivity.mUmengEntry);
        intent.putExtra("umeng_timestamp", mActivity.mUmengTimestamp);
        if ("study_record".equals(mActivity.mFrom)) {
            intent.putExtra("umeng_entry_review", "Record");
        } else if ("mokao_homepage".equals(mActivity.mFrom)
                || "mokao_history_list".equals(mActivity.mFrom)) {
            intent.putExtra("umeng_entry_review", "MokaoList");
        } else {
            intent.putExtra("umeng_entry_review", "Report");
        }

        mActivity.startActivity(intent);

        mActivity.finish();
    }

    /**
     * 添加知识点
     */
    private static void addNote() {
        if (mActivity.mNotes == null || mActivity.mNotes.size() == 0) {
            mActivity.mIvNoteNoChange.setVisibility(View.VISIBLE);
        } else {
            mActivity.mIvNoteNoChange.setVisibility(View.GONE);

            int size = mActivity.mNotes.size();
            for (int i = 0; i < size; i++) {
                NoteM note = mActivity.mNotes.get(i);
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
        if (mActivity.mCategoryMap == null) return;

        for (Object o : mActivity.mCategoryMap.entrySet()) {
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
        // 解析HistoryExerciseResp模型
        if (response == null) return;
        mActivity = activity;
        if (Globals.gson == null) Globals.gson = GsonManager.initGson();
        HistoryExerciseResp historyExerciseResp =
                Globals.gson.fromJson(response.toString(), HistoryExerciseResp.class);
        if (historyExerciseResp == null || historyExerciseResp.getResponse_code() != 1) return;

        // 成员变量赋值
        activity.mQuestions = historyExerciseResp.getQuestions();
        ArrayList<CategoryM> categorys = historyExerciseResp.getCategory();
        if (activity.mMeasureEntity == null) activity.mMeasureEntity = new MeasureEntity();
        activity.mMeasureEntity.setDefeat(historyExerciseResp.getDefeat());

        if (activity.mQuestions != null && categorys == null) {
            // 非整卷
            ArrayList<AnswerM> answers = historyExerciseResp.getAnswers();
            jointUserAnswer(answers);
        } else if (activity.mQuestions == null && categorys != null) {
            // 整卷
            activity.mQuestions = new ArrayList<>();

            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                CategoryM category = categorys.get(i);

                if (category == null) continue;

                ArrayList<QuestionM> categoryQuestions = category.getQuestions();
//                String categoryName = category.getName();

                if (categoryQuestions == null || categoryQuestions.size() == 0) continue;

                activity.mQuestions.addAll(categoryQuestions);

                // 拼接用户答案
                ArrayList<AnswerM> answers = category.getAnswers();
                jointUserAnswer(answers);
            }
        }

        // 知识点变化
        activity.mNotes = historyExerciseResp.getNotes();

        // 拼接科目
        jointCategoryMap();

        // 设置内容
        setContent();
    }

    /**
     * 拼接科目
     */
    private static void jointCategoryMap() {
        if (mActivity.mUserAnswerList == null) return;

        mActivity.mTotalNum = mActivity.mUserAnswerList.size();

        HashMap<String, Object> userAnswerMap;
        mActivity.mCategoryMap = new HashMap<>();

        for (int i = 0; i < mActivity.mTotalNum; i++) {
            userAnswerMap = mActivity.mUserAnswerList.get(i);

            String category_name = (String) userAnswerMap.get("category_name");
            String answer = (String) userAnswerMap.get("answer");
            String right_answer = (String) userAnswerMap.get("right_answer");
            int duration = (int) userAnswerMap.get("duration");
            boolean is_right = false;

            // 判断对错
            if (answer != null && right_answer != null
                    && !"".equals(answer) && answer.equals(right_answer)) {
                is_right = true;
                mActivity.mRightNum++;
            }

            // 统计科目信息
            if (category_name != null
                    && mActivity.mCategoryMap.containsKey(category_name)) {
                // 更新Map
                HashMap<String, Object> map = mActivity.mCategoryMap.get(category_name);

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
                mActivity.mCategoryMap.put(category_name, map);
            } else {
                HashMap<String, Object> map = new HashMap<>();
                if (is_right) {
                    map.put("right_num", 1);
                } else {
                    map.put("right_num", 0);
                }
                map.put("total_num", 1);
                map.put("duration_total", duration);
                mActivity.mCategoryMap.put(category_name, map);
            }
        }
    }

    /**
     * 拼接用户答案
     * @param answers 用户答案
     */
    private static void jointUserAnswer(ArrayList<AnswerM> answers) {
        mActivity.mUserAnswerList = new ArrayList<>();

        int size = mActivity.mQuestions.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<>();
            QuestionM question = mActivity.mQuestions.get(i);

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

            mActivity.mUserAnswerList.add(map);
        }
    }

    /**
     * 显示试卷类型
     * @param activity PracticeReportActivity
     */
    public static void showPaperType(PracticeReportActivity activity) {
        String sPaperType = "";

        if ("auto".equals(activity.mPaperType)) {
            sPaperType = "快速智能练习";
        } else if ("note".equals(activity.mPaperType)) {
            sPaperType = "专项练习";
        } else if ("mokao".equals(activity.mPaperType)) {
            sPaperType = "天天模考";
        } else if ("collect".equals(activity.mPaperType)) {
            sPaperType = "收藏夹练习";
        } else if ("error".equals(activity.mPaperType)) {
            sPaperType = "错题本练习";
        } else if ("entire".equals(activity.mPaperType)) {
            sPaperType = "真题演练";
        }

        activity.mTvPaperType.setText(sPaperType);
    }

    /**
     * 显示试卷描述
     * @param activity PracticeReportActivity
     */
    public static void showPaperDesc(PracticeReportActivity activity) {
        if ("auto".equals(activity.mPaperType) || "mokao".equals(activity.mPaperType)) {
            // 显示日期
            if (activity.mPaperTime != null && activity.mPaperTime.length() > 10) {
                activity.mTvPaperName.setText(
                        activity.mPaperTime.substring(0, 10).replaceAll("-", "/"));
            } else {
                activity.mTvPaperName.setText(activity.mPaperTime);
            }
        } else {
            // 显示试卷名称
            activity.mTvPaperName.setText(activity.mPaperName);
        }
    }
}
