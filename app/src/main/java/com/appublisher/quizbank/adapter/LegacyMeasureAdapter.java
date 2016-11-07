package com.appublisher.quizbank.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.LegacyMeasureActivity;
import com.appublisher.quizbank.model.business.LegacyMeasureModel;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.util.HashMap;

/**
 * 做题模块
 */
public class LegacyMeasureAdapter extends PagerAdapter{

    private LegacyMeasureActivity mActivity;
    private int mLastY;
    private SparseBooleanArray mIsItemLoad;
    private HashMap<String, Object> mUserAnswerMap;

    /** 页面控件 */
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;

    public LegacyMeasureAdapter(LegacyMeasureActivity activity) {
        mActivity = activity;
        mIsItemLoad = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return mActivity.mQuestions.size();
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!mIsItemLoad.get(position, false)) {
            mIsItemLoad.clear();
            mIsItemLoad.put(position, true);

            // 更新成员变量
            View view = (View) object;

            mTvOptionA = (TextView) view.findViewById(R.id.measure_option_a_tv);
            mTvOptionB = (TextView) view.findViewById(R.id.measure_option_b_tv);
            mTvOptionC = (TextView) view.findViewById(R.id.measure_option_c_tv);
            mTvOptionD = (TextView) view.findViewById(R.id.measure_option_d_tv);

            // 更新用户答案
            setOption(position);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        QuestionM question = mActivity.mQuestions.get(position);

        if (question == null) return new View(mActivity);

        // 材料
        String material = question.getMaterial();

        View view;
        if (material != null && material.length() > 0) {
            // 题目带材料
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.measure_item_hasmaterial, container, false);

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
                    R.layout.legacy_measure_item_withoutmaterial, container, false);
        }

        // 题干
        LinearLayout llQuestionContent = (LinearLayout) view.findViewById(
                R.id.measure_question_content);

        String questionContent = question.getQuestion();
        String questionPosition = String.valueOf(position + 1)
                + "/" + String.valueOf(mActivity.mUserAnswerList.size()) + "#%";
        questionContent = questionPosition + (questionContent == null ? "" : questionContent);

        LegacyMeasureModel.addRichTextToContainer(
                mActivity, llQuestionContent, questionContent, true, questionPosition);

        // 选项
        LinearLayout llOptionAContainer = (LinearLayout) view.findViewById(
                R.id.legacy_measure_option_a_container);
        LinearLayout llOptionBContainer = (LinearLayout) view.findViewById(
                R.id.legacy_measure_option_b_container);
        LinearLayout llOptionCContainer = (LinearLayout) view.findViewById(
                R.id.legacy_measure_option_c_container);
        LinearLayout llOptionDContainer = (LinearLayout) view.findViewById(
                R.id.legacy_measure_option_d_container);

        String optionA = question.getOption_a();
        String optionB = question.getOption_b();
        String optionC = question.getOption_c();
        String optionD = question.getOption_d();

        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionAContainer, optionA, false);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionBContainer, optionB, false);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionCContainer, optionC, false);
        LegacyMeasureModel.addRichTextToContainer(mActivity, llOptionDContainer, optionD, false);

        mTvOptionA = (TextView) view.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) view.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) view.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) view.findViewById(R.id.measure_option_d_tv);

        LinearLayout llOptionA = (LinearLayout) view.findViewById(R.id.measure_option_a);
        LinearLayout llOptionB = (LinearLayout) view.findViewById(R.id.measure_option_b);
        LinearLayout llOptionC = (LinearLayout) view.findViewById(R.id.measure_option_c);
        LinearLayout llOptionD = (LinearLayout) view.findViewById(R.id.measure_option_d);

        // 设置按钮
        setOption(position);

        mTvOptionA.setOnClickListener(optionClick);
        mTvOptionB.setOnClickListener(optionClick);
        mTvOptionC.setOnClickListener(optionClick);
        mTvOptionD.setOnClickListener(optionClick);

        // 选中行执行点击
        llOptionA.setOnClickListener(optionClick);
        llOptionB.setOnClickListener(optionClick);
        llOptionC.setOnClickListener(optionClick);
        llOptionD.setOnClickListener(optionClick);

        container.addView(view);
        return view;
    }

    /**
     * 选项点击事件
     */
    private View.OnClickListener optionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.measure_option_a:
                    LegacyMeasureModel.optionOnClickAction(LegacyMeasureAdapter.this, mTvOptionA);
                    break;

                case R.id.measure_option_b:
                    LegacyMeasureModel.optionOnClickAction(LegacyMeasureAdapter.this, mTvOptionB);
                    break;

                case R.id.measure_option_c:
                    LegacyMeasureModel.optionOnClickAction(LegacyMeasureAdapter.this, mTvOptionC);
                    break;

                case R.id.measure_option_d:
                    LegacyMeasureModel.optionOnClickAction(LegacyMeasureAdapter.this, mTvOptionD);
                    break;

                default:
                    updateUserAnswer(v);
                    pageSkip(); // 页面跳转
                    break;
            }
        }
    };

    /**
     * 页面跳转
     */
    private void pageSkip() {
        if (mActivity.mCurPosition + 1 < mActivity.mUserAnswerList.size()) {
            mActivity.mViewPager.setCurrentItem(mActivity.mCurPosition + 1);
        } else {
            mActivity.skipToAnswerSheet();
        }
    }

    /**
     * 更新用户答案
     * @param v Option View
     */
    private void updateUserAnswer(View v) {
        resetOption();
        mUserAnswerMap = mActivity.mUserAnswerList.get(mActivity.mCurPosition);

        if (mUserAnswerMap == null) return;

        switch (v.getId()) {
            case R.id.measure_option_a_tv:
                mTvOptionA.setSelected(true);
                mUserAnswerMap.put("answer", "A");
                break;

            case R.id.measure_option_b_tv:
                mTvOptionB.setSelected(true);
                mUserAnswerMap.put("answer", "B");
                break;

            case R.id.measure_option_c_tv:
                mTvOptionC.setSelected(true);
                mUserAnswerMap.put("answer", "C");
                break;

            case R.id.measure_option_d_tv:
                mTvOptionD.setSelected(true);
                mUserAnswerMap.put("answer", "D");
                break;
        }

        mActivity.mUserAnswerList.set(mActivity.mCurPosition, mUserAnswerMap);
    }

    /**
     * 重置按钮状态
     */
    public void resetOption() {
        mTvOptionA.setSelected(false);
        mTvOptionB.setSelected(false);
        mTvOptionC.setSelected(false);
        mTvOptionD.setSelected(false);
    }

    /**
     * 设置按钮状态
     */
    private void setOption(int position) {
        resetOption();
        mUserAnswerMap = mActivity.mUserAnswerList.get(position);

        if (!mUserAnswerMap.containsKey("answer")) return;

        String userAnswer = (String) mUserAnswerMap.get("answer");

        if (userAnswer == null) return;

        switch (userAnswer) {
            case "A":
                mTvOptionA.setSelected(true);
                break;

            case "B":
                mTvOptionB.setSelected(true);
                break;

            case "C":
                mTvOptionC.setSelected(true);
                break;

            case "D":
                mTvOptionD.setSelected(true);
                break;
        }
    }
}
