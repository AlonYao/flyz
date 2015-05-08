package com.appublisher.quizbank.model;

import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.Logger;

import java.util.ArrayList;

/**
 * MeasureAnalysisActivity Model
 */
public class MeasureAnalysisModel {

    private static int mCurPosition;
    private static boolean mIsShowAlert;

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

                Logger.i(String.valueOf(mCurPosition));

                if(mCurPosition == questions.size() - 1 && positionOffsetPixels == 0) {
                    if (!mIsShowAlert) {
                        mIsShowAlert = true;
                    } else {
                        AlertManager.lastPageAlert(activity);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;

                // 设置页面的状态
                setCurPageStatus(position, activity, answers);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        if(android.os.Build.VERSION.SDK_INT >= 11) {
            activity.invalidateOptionsMenu();
        } else {
            activity.supportInvalidateOptionsMenu();
        }
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
