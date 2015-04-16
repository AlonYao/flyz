package com.appublisher.quizbank.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.util.ArrayList;

/**
 * 题目解析容器
 */
public class MeasureAnalysisAdapter extends PagerAdapter{

    private MeasureAnalysisActivity mActivity;
    private int mLastY;
    private ArrayList<QuestionM> mQuestions;
    private ArrayList<AnswerM> mAnswers;

    /** 页面控件 */
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;

    public MeasureAnalysisAdapter(MeasureAnalysisActivity activity,
                                  ArrayList<QuestionM> questions,
                                  ArrayList<AnswerM> answers) {
        mActivity = activity;
        mQuestions = questions;
        mAnswers = answers;
    }

    @Override
    public int getCount() {
        return mQuestions.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        QuestionM question = mQuestions.get(position);

        if (question == null) return new View(mActivity);

        // 材料
        String material = question.getMaterial();

        View view;
        if (material != null && material.length() > 0) {
            // 题目带材料
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.measure_analysis_item_hasmaterial, container, false);

            ImageView ivPull = (ImageView) view.findViewById(R.id.measure_iv);
            LinearLayout llMaterial = (LinearLayout) view.findViewById(R.id.measure_material);
            final ScrollView svTop = (ScrollView) view.findViewById(R.id.measure_top);

            // 材料
            MeasureModel.addRichTextToContainer(mActivity, llMaterial, material);

            ivPull.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    switch (action) {

                        case MotionEvent.ACTION_DOWN:
                            mLastY = (int) event.getRawY();
                            break;

                        /**
                         * layout(l,t,r,b)
                         * l  Left position, relative to parent
                         t  Top position, relative to parent
                         r  Right position, relative to parent
                         b  Bottom position, relative to parent
                         * */
                        case MotionEvent.ACTION_MOVE:
                            int dy = (int) event.getRawY() - mLastY;

                            int top = v.getTop() + dy;
                            int bottom = v.getBottom() + dy;

                            if (top < 0) {
                                top = 0;
                                bottom = top + v.getHeight();
                            }
                            if (bottom > mActivity.mScreenHeight) {
                                bottom = mActivity.mScreenHeight;
                                top = bottom - v.getHeight();
                            }
                            v.layout(v.getLeft(), top, v.getRight(), bottom);

                            ViewGroup.LayoutParams layoutParams = svTop.getLayoutParams();
                            layoutParams.height = svTop.getHeight() + dy;
                            svTop.setLayoutParams(layoutParams);

                            mLastY = (int) event.getRawY();

                            break;

                        case MotionEvent.ACTION_UP:
                            break;
                    }

                    return false;
                }
            });

        } else {
            // 题目不带材料
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.measure_analysis_item_withoutmaterial, container, false);
        }

        // 题干
        LinearLayout llQuestionContent = (LinearLayout) view.findViewById(
                R.id.measure_question_content);

        String questionContent = question.getQuestion();
        String questionPosition = String.valueOf(position + 1)
                + "/" + String.valueOf(mQuestions.size()) + " ";
        questionContent = questionPosition + (questionContent == null ? "" : questionContent);

        MeasureModel.addRichTextToContainer(mActivity, llQuestionContent, questionContent);

        // 设置选项内容
        LinearLayout llOptionAContainer = (LinearLayout) view.findViewById(
                R.id.measure_option_a_container);
        LinearLayout llOptionBContainer = (LinearLayout) view.findViewById(
                R.id.measure_option_b_container);
        LinearLayout llOptionCContainer = (LinearLayout) view.findViewById(
                R.id.measure_option_c_container);
        LinearLayout llOptionDContainer = (LinearLayout) view.findViewById(
                R.id.measure_option_d_container);

        String optionA = question.getOption_a();
        String optionB = question.getOption_b();
        String optionC = question.getOption_c();
        String optionD = question.getOption_d();

        MeasureModel.addRichTextToContainer(mActivity, llOptionAContainer, optionA);
        MeasureModel.addRichTextToContainer(mActivity, llOptionBContainer, optionB);
        MeasureModel.addRichTextToContainer(mActivity, llOptionCContainer, optionC);
        MeasureModel.addRichTextToContainer(mActivity, llOptionDContainer, optionD);

        // 选项Textview控件
        mTvOptionA = (TextView) view.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) view.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) view.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) view.findViewById(R.id.measure_option_d_tv);

        // 设置正确答案
        String rightAnswer = question.getAnswer();
        if (rightAnswer != null && rightAnswer.length() != 0) {
            setOptionBackground(rightAnswer, true);
        }

        // 处理用户答案
        dealUserAnswer(position);

        // 解析
        LinearLayout llMeasureAnalysis =
                (LinearLayout) view.findViewById(R.id.measure_analysis_container);

        MeasureModel.addRichTextToContainer(mActivity, llMeasureAnalysis, question.getAnalysis());

        container.addView(view);
        return view;
    }

    /**
     * 处理用户答案
     * @param position 当前页面
     */
    private void dealUserAnswer(int position) {
        if (mAnswers == null || position >= mAnswers.size()) return;
        AnswerM answer = mAnswers.get(position);

        if (answer == null) return;
        String userAnswer = answer.getAnswer();
        boolean isRight = answer.isIs_right();

        if (!isRight) {
            setOptionBackground(userAnswer, false);
        }
    }

    /**
     * 设置选项背景颜色
     * @param userAnswer 用户答案
     */
    private void setOptionBackground(String userAnswer, boolean isRight) {
        if (userAnswer == null || userAnswer.length() == 0) return;

        int resId = R.drawable.measure_analysis_right;
        if (!isRight) {
            resId = R.drawable.measure_analysis_wrong;
        }

        switch (userAnswer) {
            case "A":
                mTvOptionA.setBackgroundResource(resId);
                mTvOptionA.setTextColor(mActivity.getResources().getColor(R.color.white));
                break;

            case "B":
                mTvOptionB.setBackgroundResource(resId);
                mTvOptionB.setTextColor(mActivity.getResources().getColor(R.color.white));
                break;

            case "C":
                mTvOptionC.setBackgroundResource(resId);
                mTvOptionC.setTextColor(mActivity.getResources().getColor(R.color.white));
                break;

            case "D":
                mTvOptionD.setBackgroundResource(resId);
                mTvOptionD.setTextColor(mActivity.getResources().getColor(R.color.white));
                break;
        }
    }
}
