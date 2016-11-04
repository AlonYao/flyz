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
import com.appublisher.quizbank.activity.LegacyMeasureAnalysisActivity;
import com.appublisher.quizbank.model.business.LegacyMeasureModel;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 题目解析容器
 */
public class LegacyMeasureAnalysisAdapter extends PagerAdapter {

    private LegacyMeasureAnalysisActivity mActivity;
    private int mLastY;
    private ArrayList<QuestionM> mQuestions;
    private ArrayList<AnswerM> mAnswers;

    /**
     * 页面控件
     */
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;
    private ImageView mIvNoAnswer;

    public LegacyMeasureAnalysisAdapter(LegacyMeasureAnalysisActivity activity,
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
            LegacyMeasureModel.addRichTextToContainer(mActivity, llMaterial, material, true);

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
                + "/" + String.valueOf(mQuestions.size()) + "#%";
        questionContent = questionPosition + (questionContent == null ? "" : questionContent);

        LegacyMeasureModel.addRichTextToContainer(
                mActivity, llQuestionContent, questionContent, true, questionPosition);

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

        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionAContainer, optionA, true);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionBContainer, optionB, true);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionCContainer, optionC, true);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionDContainer, optionD, true);

        // 选项Textview控件
        mTvOptionA = (TextView) view.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) view.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) view.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) view.findViewById(R.id.measure_option_d_tv);

        // 未做标示
        mIvNoAnswer = (ImageView) view.findViewById(R.id.measure_analysis_noanswer);

        // 设置正确答案
        String rightAnswer = question.getAnswer();
        if (rightAnswer != null && rightAnswer.length() != 0) {
            setOptionBackground(rightAnswer, true);
        }
        // 处理用户答案
        dealUserAnswer(position);

        // 正确答案
        TextView tvRightAnswer = (TextView) view.findViewById(R.id.measure_analysis_rightanswer);
        String sRight = "【正确答案】 " + (rightAnswer == null ? "" : rightAnswer) + "；";
        if (mAnswers != null
                && position < mAnswers.size()
                && mAnswers.get(position) != null
                && mAnswers.get(position).getAnswer() != null
                && !mAnswers.get(position).getAnswer().equals("")) {
            sRight = sRight + "你的选择是" + mAnswers.get(position).getAnswer();
        }
        tvRightAnswer.setText(sRight);
        // 解析
        LinearLayout llMeasureAnalysis =
                (LinearLayout) view.findViewById(R.id.measure_analysis_container);

        LegacyMeasureModel.addRichTextToContainer(
                mActivity, llMeasureAnalysis, "【解析】 " + question.getAnalysis(), true);

        // 解析 知识点&来源&统计
        TextView tvNote = (TextView) view.findViewById(R.id.measure_analysis_note);
        TextView tvSource = (TextView) view.findViewById(R.id.measure_analysis_source);
        final TextView tvCategory = (TextView) view.findViewById(R.id.measure_analysis_accuracy);
        tvNote.setText("【知识点】 " + question.getNote_name());
        tvSource.setText("【来源】 " + question.getSource());

        showSummary(question, tvCategory);

        container.addView(view);
        return view;
    }

    /**
     * 显示统计信息
     * @param question QuestionM
     * @param textView TextView
     */
    private void showSummary(QuestionM question, TextView textView) {
        float summaryAccuracy = question.getSummary_accuracy();
        int summary_count = question.getSummary_count();
        String summary_fallible = question.getSummary_fallible();
        String data = "";

        if (summary_count != 0) {
            data = data + "全站作答" + String.valueOf(summary_count) + "次";
        }

        if (summaryAccuracy != 0) {
            if (data.length() != 0) data = data + "，";
            summaryAccuracy = summaryAccuracy*100;
            BigDecimal bigDecimal = new BigDecimal(summaryAccuracy);
            summaryAccuracy = bigDecimal.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
            data = data + "正确率" + summaryAccuracy + "%";
        }

        if (summary_fallible != null && summary_fallible.length() != 0) {
            if (data.length() != 0) data = data + "，";
            data = data + "易错项为" + summary_fallible;
        }

        if (data.length() == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            data = "【统计】 " + data;
            textView.setText(data);
        }
    }

    /**
     * 处理用户答案
     *
     * @param position 当前页面
     */
    private void dealUserAnswer(int position) {
        if (mAnswers == null || position >= mAnswers.size()) return;

        AnswerM answer = mAnswers.get(position);

        if (answer == null) return;

        String userAnswer = answer.getAnswer();
        boolean isRight = answer.isIs_right();

        if (isRight) {
            mIvNoAnswer.setVisibility(View.GONE);
        } else {
            setOptionBackground(userAnswer, false);

            if (userAnswer == null || userAnswer.length() == 0) {
                mIvNoAnswer.setVisibility(View.VISIBLE);
            } else {
                mIvNoAnswer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置选项背景颜色
     *
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
//    @Override
//    public int getItemPosition(Object object) {
//        return  POSITION_NONE;
//    }
}
