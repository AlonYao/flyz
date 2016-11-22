package com.appublisher.quizbank.model.business;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.entity.measure.MeasureEntity;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
import com.appublisher.quizbank.model.netdata.historyexercise.HistoryExerciseResp;
import com.appublisher.quizbank.model.netdata.historyexercise.ScoreM;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.CategoryM;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.PopupWindowManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习报告模块 Model
 */
public class PracticeReportModel {

    private PracticeReportActivity mActivity;

    public PracticeReportModel(Context context) {
        mActivity = (PracticeReportActivity) context;
    }

    /**
     * 获取数据
     */
    public void getData() {
        mActivity.mRightNum = mActivity.getIntent().getIntExtra("right_num", 0);
        mActivity.mTotalNum = mActivity.getIntent().getIntExtra("total_num", 0);
        //noinspection unchecked
        mActivity.mCategoryMap = (HashMap<String, HashMap<String, Object>>)
                mActivity.getIntent().getSerializableExtra("category");
        //noinspection unchecked
        mActivity.mNotes = (ArrayList<NoteM>) mActivity.getIntent().getSerializableExtra("notes");

        //noinspection unchecked
        mActivity.mQuestions = (ArrayList<QuestionM>)
                mActivity.getIntent().getSerializableExtra("questions");

        //noinspection unchecked
        mActivity.mUserAnswerList = (ArrayList<HashMap<String, Object>>)
                mActivity.getIntent().getSerializableExtra("user_answer");

        // 显示内容
        setContent();
    }

    /**
     * 设置内容
     */
    public void setContent() {
        // 如果是今日Mini模考，显示击败信息
        if ("mokao".equals(mActivity.mPaperType)) {
            mActivity.mRlMiniMokao.setVisibility(View.VISIBLE);
            mActivity.mDefeat = mActivity.mMeasureEntity.getDefeat();
            String defeat = "击败"
                    + Utils.rateToPercent(mActivity.mDefeat)
                    + "%的考生";
            Spannable word = new SpannableString(defeat);
            word.setSpan(
                    new AbsoluteSizeSpan(Utils.sp2px(mActivity, 22)),
                    2,
                    defeat.indexOf("的考生"),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //noinspection deprecation
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
            mActivity.mLlBorderLine.setVisibility(View.VISIBLE); // 分数线
            mActivity.mLlEvaluate.setVisibility(View.VISIBLE); // 你的成绩

            // 预估分
            mActivity.mScore = mActivity.mMeasureEntity.getScore();
            mActivity.mTvEvaluateNum.setText(String.valueOf(mActivity.mScore));

            // 往年分数线
            setBorderLine();

        } else if ("entire".equals(mActivity.mPaperType)) {
            // 整卷：显示成绩、全站统计信息、科目、往年分数线
            // 你的成绩
            mActivity.mLlEvaluate.setVisibility(View.VISIBLE);
            mActivity.mScore = mActivity.mMeasureEntity.getScore();
            mActivity.mTvEvaluateNum.setText(String.valueOf(mActivity.mScore));

            // 全站统计信息
            showEntireInfo();

            // 分数线
            mActivity.mLlBorderLine.setVisibility(View.VISIBLE);
            setBorderLine();

        } else if ("mock".equals(mActivity.mPaperType)) {
            // 模考：显示成绩、科目、知识点变化
            mActivity.mLlEvaluate.setVisibility(View.VISIBLE);
            mActivity.mLlNoteContainer.setVisibility(View.VISIBLE);

            // 预估分
            mActivity.mScore = mActivity.mMeasureEntity.getScore();
            mActivity.mTvEvaluateNum.setText(String.valueOf(mActivity.mScore));

            // 知识点变化
            addNote();

        } else {
            mActivity.mLlRatio.setVisibility(View.VISIBLE);
            mActivity.mLlNoteContainer.setVisibility(View.VISIBLE);

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
     * 显示全站统计信息
     */
    private void showEntireInfo() {
        if (mActivity.mMeasureEntity == null) return;

        mActivity.mRlEntireInfo.setVisibility(View.VISIBLE);

        String text = Utils.rateToPercent(mActivity.mMeasureEntity.getDefeat()) + "%";
        mActivity.mTvEntireInfoRank.setText(text);
        mActivity.mTvEntireInfoScore.setText(
                String.valueOf(mActivity.mMeasureEntity.getAvg_score()));
    }

    /**
     * 往年分数线
     */
    private void setBorderLine() {
        if (mActivity.mMeasureEntity == null || mActivity.mMeasureEntity.getScores() == null)
            return;

        ArrayList<ScoreM> scores = mActivity.mMeasureEntity.getScores();
        int size = scores.size();
        for (int i = 0; i < size; i++) {
            ScoreM score = scores.get(i);
            if (score == null) continue;

            View child = LayoutInflater.from(mActivity).inflate(
                    R.layout.practice_report_borderline_item,
                    mActivity.mLlBorderLine, false);

            TextView tvName =
                    (TextView) child.findViewById(R.id.item_borderline_name);
            TextView tvNum =
                    (TextView) child.findViewById(R.id.item_borderline_num);
            View line = child.findViewById(R.id.item_borderline_line);

            tvName.setText(score.getName());
            tvNum.setText(String.valueOf(score.getScore()));

            if (i == size - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }

            mActivity.mLlBorderLine.addView(child);
        }

        // 说明文字
        TextView textView = new TextView(mActivity);
        textView.setText(mActivity.getString(R.string.practice_report_borderline_desc));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
                Utils.dip2px(mActivity, 36),
                Utils.dip2px(mActivity, 5),
                Utils.dip2px(mActivity, 36),
                0);
        textView.setLayoutParams(layoutParams);
        mActivity.mLlBorderLine.addView(textView);
    }

    /**
     * 1.5练习报告页版本更新
     */
    public void updateNotice() {
        //1.5版本加排名信息提示
        if ("entire".equals(mActivity.mPaperType)) {
            boolean isFirstStart = Globals.sharedPreferences.getBoolean("firstNotice", true);
            boolean detailCategory = Globals.sharedPreferences.getBoolean("rankInfo", true);
            if (!isFirstStart && detailCategory) {
                PopupWindowManager.showUpdatePracticeReport(mActivity.parentView, mActivity);
            }
        }
    }

    /**
     * 全部点击事件
     */
    private View.OnClickListener allOnClick = new View.OnClickListener() {
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
    private View.OnClickListener errorOnClick = new View.OnClickListener() {
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
     *
     * @param questions 题目
     * @param answers   答案
     */
    private void skipToMeasureAnalysisActivity(ArrayList<QuestionM> questions,
                                               ArrayList<AnswerM> answers) {
        Intent intent = new Intent(mActivity, MeasureAnalysisActivity.class);
        intent.putExtra("questions", questions);
        intent.putExtra("answers", answers);
        intent.putExtra("paper_name", mActivity.mPaperName);
        intent.putExtra("analysis_type", mActivity.mPaperType);
        intent.putExtra("hierarchy_id", mActivity.mHierarchyId);
        intent.putExtra("hierarchy_level", mActivity.mHierarchyLevel);
        intent.putExtra("from", mActivity.mFrom);

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
    private void addNote() {
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
    private void addCategory() {
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
     *
     * @param level 知识点等级
     * @param view  知识点等级view
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
     *
     * @param response 回调数据
     */
    public void dealHistoryExerciseDetailResp(JSONObject response) {
        // 解析HistoryExerciseResp模型
        if (response == null) return;
        HistoryExerciseResp historyExerciseResp =
                GsonManager.getModel(response.toString(), HistoryExerciseResp.class);
        if (historyExerciseResp == null || historyExerciseResp.getResponse_code() != 1) return;

        // 成员变量赋值
        mActivity.mQuestions = historyExerciseResp.getQuestions();
        ArrayList<CategoryM> categorys = historyExerciseResp.getCategory();
        if (mActivity.mMeasureEntity == null) mActivity.mMeasureEntity = new MeasureEntity();
        mActivity.mMeasureEntity.setDefeat(historyExerciseResp.getDefeat());
        mActivity.mMeasureEntity.setScore(historyExerciseResp.getScore());
        mActivity.mMeasureEntity.setScores(historyExerciseResp.getScores());
        mActivity.mMeasureEntity.setAvg_score(historyExerciseResp.getAvg_score());

        if (mActivity.mQuestions != null && categorys == null) {
            // 非整卷
            ArrayList<AnswerM> answers = historyExerciseResp.getAnswers();
            jointUserAnswer(answers);
        } else if (mActivity.mQuestions == null && categorys != null) {
            // 整卷
            mActivity.mQuestions = new ArrayList<>();
            ArrayList<AnswerM> answers = new ArrayList<>();

            int size = categorys.size();
            for (int i = 0; i < size; i++) {
                CategoryM category = categorys.get(i);
                if (category == null) continue;

                ArrayList<QuestionM> categoryQuestions = category.getQuestions();
                if (categoryQuestions == null || categoryQuestions.size() == 0) continue;

                mActivity.mQuestions.addAll(categoryQuestions);
                answers.addAll(category.getAnswers());
            }

            jointUserAnswer(answers);
        }

        // 知识点变化
        mActivity.mNotes = historyExerciseResp.getNotes();

        // 拼接科目
        jointCategoryMap();

        // 设置内容
        setContent();
    }

    /**
     * 拼接科目
     */
    public void jointCategoryMap() {
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
     *
     * @param answers 用户答案
     */
    public void jointUserAnswer(ArrayList<AnswerM> answers) {
        if (mActivity.mUserAnswerList == null)
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
     */
    public void showPaperType() {
        String sPaperType = "";

        if ("auto".equals(mActivity.mPaperType)) {
            sPaperType = "快速智能练习";
        } else if ("note".equals(mActivity.mPaperType)) {
            sPaperType = "专项练习";
        } else if ("mokao".equals(mActivity.mPaperType)) {
            sPaperType = "mini模考";
        } else if ("collect".equals(mActivity.mPaperType)) {
            sPaperType = "收藏夹练习";
        } else if ("error".equals(mActivity.mPaperType)) {
            sPaperType = "错题本练习";
        } else if ("entire".equals(mActivity.mPaperType)) {
            sPaperType = "真题演练";
        } else if ("evaluate".equals(mActivity.mPaperType)) {
            sPaperType = "估分";
        } else if ("mock".equals(mActivity.mPaperType)) {
            sPaperType = "模考";
        }

        mActivity.mTvPaperType.setText(sPaperType);
    }

    /**
     * 显示试卷描述
     */
    public void showPaperDesc() {
        if ("auto".equals(mActivity.mPaperType)) {
            // 显示日期
            if (mActivity.mPaperTime != null && mActivity.mPaperTime.length() > 10) {
                mActivity.mTvPaperName.setText(
                        mActivity.mPaperTime.substring(0, 10).replaceAll("-", "/"));
            } else {
                mActivity.mTvPaperName.setText(mActivity.mPaperTime);
            }
        } else {
            // 显示试卷名称
            mActivity.mTvPaperName.setText(mActivity.mPaperName);
        }
    }

    /**
     * 设置友盟分享
     */
    public void setUmengShare() {

        // 练习报告
        GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
        String baseUrl = "http://m.zhiboke.net/#/live/practiceReport?";
        if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
            baseUrl = globalSettingsResp.getReport_share_url();
        }
        int exerciseId = mActivity.mExerciseId == 0 ? mActivity.mPaperId : mActivity.mExerciseId;
        String paperName = mActivity.mTvPaperName.getText() == null ? "" : mActivity.mTvPaperName.getText().toString();

        baseUrl = baseUrl + "user_id=" + LoginModel.getUserId()
                + "&user_token=" + LoginModel.getUserToken()
                + "&exercise_id=" + exerciseId
                + "&paper_type=" + mActivity.mPaperType
                + "&name=" + paperName;

        // 练习报告
        String content;
        if ("mokao".equals(mActivity.mPaperType)) {
            content = "刚刚打败了全国"
                    + Utils.rateToPercent(mActivity.mDefeat)
                    + "%的小伙伴，学霸非我莫属！";
        } else if ("evaluate".equals(mActivity.mPaperType)) {
            content = mActivity.mPaperName
                    + "我估计能"
                    + mActivity.mScore
                    + "分，快来看看~";
        } else if ("mock".equals(mActivity.mPaperType)) {
            content = "我在"
                    + mActivity.mPaperName
                    + "中拿了"
                    + mActivity.mScore
                    + "分，棒棒哒！";
        } else {
            content = "刷了一套题，正确率竟然达到了"
                    + Utils.getPercent1(mActivity.mRightNum, mActivity.mTotalNum)
                    + "呢~";
        }
        Resources resources = mActivity.getResources();
        //noinspection ConstantConditions
        UmengManager.UMShareEntity umShareEntity = new UmengManager.UMShareEntity()
                .setTitle(resources.getString(R.string.share_title))
                .setText(content)
                .setTargetUrl(baseUrl)
                .setSinaWithoutTargetUrl(true)
                .setUmImage(new UMImage(mActivity, Utils.getBitmapByView(mActivity.mSvMain)));
        UmengManager.shareAction(mActivity, umShareEntity, UmengManager.APP_TYPE_QUIZBANK, new UmengManager.PlatformInter() {
            @Override
            public void platform(SHARE_MEDIA platformType) {

            }
        });
    }
}
