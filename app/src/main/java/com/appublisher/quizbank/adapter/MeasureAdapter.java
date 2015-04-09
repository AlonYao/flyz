package com.appublisher.quizbank.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;
import com.appublisher.quizbank.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 做题模块
 */
public class MeasureAdapter extends PagerAdapter{

    private MeasureActivity mActivity;
    private int mLastY;
    private SparseBooleanArray mIsItemLoad;
    private HashMap<String, Object> mUserAnswerMap;
    private ArrayList<QuestionM> mQuestions;

    /** 页面控件 */
    private CheckBox mCbOptionA;
    private CheckBox mCbOptionB;
    private CheckBox mCbOptionC;
    private CheckBox mCbOptionD;

    public MeasureAdapter(MeasureActivity activity, ArrayList<QuestionM> questions) {
        mActivity = activity;
        mIsItemLoad = new SparseBooleanArray();
        mQuestions = questions;
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!mIsItemLoad.get(position, false)) {
            mIsItemLoad.clear();
            mIsItemLoad.put(position, true);

            // 更新成员变量
//            boolean hasMaterial = true;
            View view = (View) object;

            mCbOptionA = (CheckBox) view.findViewById(R.id.measure_option_a_cb);
            mCbOptionB = (CheckBox) view.findViewById(R.id.measure_option_b_cb);
            mCbOptionC = (CheckBox) view.findViewById(R.id.measure_option_c_cb);
            mCbOptionD = (CheckBox) view.findViewById(R.id.measure_option_d_cb);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        QuestionM question = mQuestions.get(position);

        if (question == null) return new View(mActivity);

        // 材料
        String material = question.getMaterial();

//        String rich = "把1月和2月的利润代入公式，我们可以得到<img=http://dl.cdn.appublisher.com/" +
//                "yimgs/4/gjkodixmzjizdmz.png></img> ，解得<img=http://dl.cdn.appublisher.com/" +
//                "yimgs/4/wq3mjhjywjhyzzj.png></img>。故1—12月的累积利润为<img=http://dl.cdn." +
//                "appublisher.com/yimgs/4/dg1y2e3mgm0mdq0.png></img> ，平均利润为<img=http://dl." +
//                "cdn.appublisher.com/yimgs/4/2m4mzg3zjjiownk.png></img>。因此，本题答案选择C选项。";

        View view;
        if (material != null && material.length() > 0) {
            // 题目带材料
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.measure_item_hasmaterial, container, false);

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
                    R.layout.measure_item_withoutmaterial, container, false);
        }

        // 题干
        LinearLayout llQuestionContent = (LinearLayout) view.findViewById(
                R.id.measure_question_content);

        String questionContent = question.getQuestion();
        String questionPosition = String.valueOf(position + 1)
                + "/" + String.valueOf(mActivity.mUserAnswerList.size()) + " ";
        questionContent = questionPosition + (questionContent == null ? "" : questionContent);

        MeasureModel.addRichTextToContainer(mActivity, llQuestionContent, questionContent);

        // 选项
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

        mCbOptionA = (CheckBox) view.findViewById(R.id.measure_option_a_cb);
        mCbOptionB = (CheckBox) view.findViewById(R.id.measure_option_b_cb);
        mCbOptionC = (CheckBox) view.findViewById(R.id.measure_option_c_cb);
        mCbOptionD = (CheckBox) view.findViewById(R.id.measure_option_d_cb);

        // 设置按钮
        setOption(position);

        mCbOptionA.setOnClickListener(optionClick);
        mCbOptionB.setOnClickListener(optionClick);
        mCbOptionC.setOnClickListener(optionClick);
        mCbOptionD.setOnClickListener(optionClick);

        container.addView(view);
        return view;
    }

    /**
     * 选项点击事件
     */
    private View.OnClickListener optionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetOption();
            mUserAnswerMap = mActivity.mUserAnswerList.get(mActivity.mCurPosition);

            boolean hasAnswer = false;

            if (mUserAnswerMap.containsKey("answer")
                    && mUserAnswerMap.get("answer") != null
                    && !mUserAnswerMap.get("answer").equals("")) hasAnswer = true;

            switch (v.getId()) {
                case R.id.measure_option_a_cb:
                    mCbOptionA.setChecked(true);
                    mUserAnswerMap.put("answer", "A");

                    break;

                case R.id.measure_option_b_cb:
                    mCbOptionB.setChecked(true);
                    mUserAnswerMap.put("answer", "B");

                    break;

                case R.id.measure_option_c_cb:
                    mCbOptionC.setChecked(true);
                    mUserAnswerMap.put("answer", "C");

                    break;

                case R.id.measure_option_d_cb:
                    mCbOptionD.setChecked(true);
                    mUserAnswerMap.put("answer", "D");

                    break;
            }

            mActivity.mUserAnswerList.set(mActivity.mCurPosition, mUserAnswerMap);

            if (hasAnswer) return;

            if (mActivity.mCurPosition + 1 < mActivity.mUserAnswerList.size()) {
                mActivity.mViewPager.setCurrentItem(mActivity.mCurPosition + 1);
            } else {
                Intent intent = new Intent(mActivity, AnswerSheetActivity.class);
                intent.putExtra("user_answer", mActivity.mUserAnswerList);
                mActivity.startActivityForResult(intent, ActivitySkipConstants.ANSWER_SHEET_SKIP);
            }
        }
    };

    /**
     * 重置按钮状态
     */
    private void resetOption() {
        mCbOptionA.setChecked(false);
        mCbOptionB.setChecked(false);
        mCbOptionC.setChecked(false);
        mCbOptionD.setChecked(false);
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
                mCbOptionA.setChecked(true);
                break;

            case "B":
                mCbOptionB.setChecked(true);
                break;

            case "C":
                mCbOptionC.setChecked(true);
                break;

            case "D":
                mCbOptionD.setChecked(true);
                break;
        }
    }
}
