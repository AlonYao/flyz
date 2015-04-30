package com.appublisher.quizbank.model;

import android.support.v4.view.ViewPager;

import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.adapter.MeasureAnalysisAdapter;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
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
    public static void setViewPager(MeasureAnalysisActivity activity,
                                    final ArrayList<QuestionM> questions,
                                    ArrayList<AnswerM> answers) {
        if (questions == null || questions.size() == 0) return;

        MeasureAnalysisAdapter adapter = new MeasureAnalysisAdapter(
                activity,
                questions,
                answers);
        activity.mViewPager.setAdapter(adapter);

        mIsShowAlert = false;

        activity.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                if(mCurPosition == questions.size() - 1 && positionOffsetPixels == 0) {
                    if (!mIsShowAlert) {
                        mIsShowAlert = true;
                    } else {
                        Logger.i("show Alert");
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
