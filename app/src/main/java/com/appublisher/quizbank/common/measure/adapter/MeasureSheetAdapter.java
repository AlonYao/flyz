package com.appublisher.quizbank.common.measure.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureModel;
import com.appublisher.quizbank.common.measure.activity.MeasureActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;

import java.util.List;

/**
 * 答题卡
 */
public class MeasureSheetAdapter extends BaseAdapter{

    private MeasureActivity mActivity;
    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureSubmitBean> mSubmits;

    public MeasureSheetAdapter(MeasureActivity activity,
                               List<MeasureQuestionBean> questions) {
        mActivity = activity;
        mQuestions = questions;
        mSubmits = MeasureModel.getCacheUserAnswer(activity);
    }

    @Override
    public int getCount() {
        return mQuestions == null ? 0 : mQuestions.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.answer_sheet_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.answer_sheet_item_bg);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.answer_sheet_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(viewHolder, position);

//        viewHolder.tvNum.setText(String.valueOf(mOffset + position + 1));
//
//        HashMap<String, Object> userAnswerMap = mUserAnswerList.get(position);
//        if ("analysis".equals(mActivity.mFrom)) {
//            // 解析
//            boolean isRight = false;
//
//            if (userAnswerMap.containsKey("answer")
//                    && userAnswerMap.containsKey("right_answer")
//                    && userAnswerMap.get("answer") != null
//                    && userAnswerMap.get("right_answer").equals(userAnswerMap.get("answer"))) {
//                isRight = true;
//            }
//
//            if (isRight) {
//                viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_right);
//            } else {
//                viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_wrong);
//            }
//
//            viewHolder.tvNum.setTextColor(Color.WHITE);
//        } else {
//            // 非解析
//            if (userAnswerMap.containsKey("answer")
//                    && userAnswerMap.get("answer") != null
//                    && !userAnswerMap.get("answer").equals("")) {
//                viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_selected);
//                viewHolder.tvNum.setTextColor(Color.WHITE);
//            } else {
//                viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_unselect);
//                viewHolder.tvNum.setTextColor(
//                        mActivity.getResources().getColor(R.color.common_text));
//            }
//        }
//
//        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Class<?> cls;
//
//                if ("analysis".equals(mActivity.mFrom)) {
//                    cls = LegacyMeasureAnalysisActivity.class;
//                } else {
//                    cls = LegacyMeasureActivity.class;
//                }
//
//                Intent intent = new Intent(mActivity, cls);
//                intent.putExtra("position", mOffset + position);
//                mActivity.setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
//                mActivity.finish();
//            }
//        });

        return convertView;
    }

    private void setContent(ViewHolder viewHolder, int position) {
        if (mQuestions == null || position >= mQuestions.size()) return;
        final MeasureQuestionBean questionBean = mQuestions.get(position);
        if (questionBean == null) return;

        // 题号
        viewHolder.tvNum.setText(String.valueOf(questionBean.getQuestion_order()));

        // 做题记录标记
        if (hasRecord(questionBean.getQuestion_order() - 1)) {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_selected);
            viewHolder.tvNum.setTextColor(Color.WHITE);
        } else {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_unselect);
            viewHolder.tvNum.setTextColor(ContextCompat.getColor(mActivity, R.color.measure_text));
        }

        // 点击跳转
        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, cls);
//                intent.putExtra("position", mOffset + position);
//                mActivity.setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
//                mActivity.finish();
                mActivity.mViewPager.setCurrentItem(questionBean.getQuestion_index());
            }
        });
    }

    private boolean hasRecord(int order) {
        if (mSubmits == null || order >= mSubmits.size()) return false;
        MeasureSubmitBean submitBean = mSubmits.get(order);
        if (submitBean == null) return false;
        return submitBean.getAnswer().length() > 0;
    }

    private class ViewHolder {
        ImageView ivBg;
        TextView tvNum;
    }
}
