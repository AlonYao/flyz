package com.appublisher.quizbank.model.business;

import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MeasureAnalysisActivity Model
 */
public class MeasureAnalysisModel {

    private static int mCurPosition;

    public static boolean mIsShowAlert;

    /**
     * 设置ViewPager
     * @param activity MeasureAnalysisActivity
     * @param questions 问题
     * @param answers 答案
     */
    public static void setViewPager(final MeasureAnalysisActivity activity,
                                    final ArrayList<QuestionM> questions,
                                    final ArrayList<AnswerM> answers) {
        if (questions == null || questions.size() == 0) return;

        MeasureAnalysisAdapter adapter = new MeasureAnalysisAdapter(
                activity,
                questions,
                answers);
        activity.mViewPager.setAdapter(adapter);

        mIsShowAlert = false;

        // 更新第一个页面的状态
        mCurPosition = 0;
        setCurPageStatus(mCurPosition, activity, answers);

        activity.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 最后一页再往后滑，弹出末题引导
                if(mCurPosition == questions.size() - 1
                        && positionOffsetPixels == 0
                        && !"study_record".equals(activity.mFrom)
                        && !"collect_or_error".equals(activity.mFrom)) {

                    if (!mIsShowAlert) {
                        mIsShowAlert = true;
                    } else {
                        AlertManager.lastPageAlert(activity);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Favorite", activity.mUmengFavorite);
                if (activity.mUmengDelete != null)
                    map.put("Delete", activity.mUmengDelete);
                MobclickAgent.onEvent(activity, "ReviewDetail", map);

                mCurPosition = position;

                // 设置页面的状态
                setCurPageStatus(position, activity, answers);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 拼接用户答案（用于展示答题卡）
        activity.mUserAnswerList = new ArrayList<>();
        if ("entire".equals(activity.mAnalysisType)) {
            // 筛选科目信息
            activity.mEntirePaperCategory = new ArrayList<>();

            int size = questions.size();
            for (int i = 0; i < size; i++) {
                QuestionM question = questions.get(i);
                if (question == null) continue;

                String categoryName = question.getCategory_name();
                if (categoryName == null || categoryName.length() == 0) continue;

                int sizeCategory =  activity.mEntirePaperCategory.size();
                if (sizeCategory == 0) {
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put(categoryName, 1);
                    activity.mEntirePaperCategory.add(map);
                } else {
                    boolean hasCategory = false;
                    HashMap<String, Integer> map;

                    // 开始匹配
                    for (int j = 0; j < sizeCategory; j++) {
                        map = activity.mEntirePaperCategory.get(j);

                        if (map == null || !map.containsKey(categoryName)) continue;

                        int count = map.get(categoryName);
                        count++;
                        map.put(categoryName, count);
                        activity.mEntirePaperCategory.set(j, map);

                        hasCategory = true;
                    }

                    // 如果没有匹配到
                    if (!hasCategory) {
                        map = new HashMap<>();
                        map.put(categoryName, 1);
                        activity.mEntirePaperCategory.add(map);
                    }
                }
            }
        }

        MeasureModel.jointUserAnswer(questions, answers, activity.mUserAnswerList);
    }

    /**
     * 设置页面的状态
     * @param position 页面的位置
     * @param activity MeasureAnalysisActivity
     * @param answers 用户答案
     */
    private static void setCurPageStatus(int position,
                                         MeasureAnalysisActivity activity,
                                         ArrayList<AnswerM> answers) {
        if (position >= answers.size()) return;

        AnswerM answer = answers.get(position);

        if (answer == null) return;

        activity.mCurQuestionId = answer.getId();
        activity.mCurAnswerModel = answer;

        // 更新Menu
        Utils.updateMenu(activity);
    }

    /**
     * 设置收藏
     * @param activity MeasureAnalysisActivity
     * @param item MenuItem
     */
    public static void setCollect(MeasureAnalysisActivity activity, MenuItem item) {
        activity.mCollect = "collect";

        if (activity.mCurAnswerModel != null) activity.mCurAnswerModel.setIs_collected(true);

        item.setIcon(R.drawable.measure_analysis_collected);
    }

    /**
     * 设置未收藏
     * @param activity MeasureAnalysisActivity
     * @param item MenuItem
     */
    public static void setUnCollect(MeasureAnalysisActivity activity, MenuItem item) {
        activity.mCollect = "cancel";

        if (activity.mCurAnswerModel != null) activity.mCurAnswerModel.setIs_collected(false);

        item.setIcon(R.drawable.measure_analysis_uncollect);
    }
}
