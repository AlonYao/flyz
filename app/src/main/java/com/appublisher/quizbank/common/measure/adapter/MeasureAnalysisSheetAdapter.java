package com.appublisher.quizbank.common.measure.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureAnalysisSheetFragment;

import java.util.List;

/**
 * 做题模块
 */

public class MeasureAnalysisSheetAdapter extends BaseAdapter{

    private Context mContext;
    private MeasureAnalysisSheetFragment mFragment;
    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureAnswerBean> mAnswers;

    public MeasureAnalysisSheetAdapter(MeasureAnalysisSheetFragment fragment,
                                       List<MeasureQuestionBean> questions,
                                       List<MeasureAnswerBean> answers) {
        mContext = fragment.getActivity();
        mFragment = fragment;
        mQuestions = questions;
        mAnswers = answers;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.answer_sheet_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.answer_sheet_item_bg);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.answer_sheet_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(viewHolder, position);

        return convertView;
    }

    private void setContent(ViewHolder viewHolder, int position) {
        if (mQuestions == null || position >= mQuestions.size()) return;
        final MeasureQuestionBean questionBean = mQuestions.get(position);
        if (questionBean == null) return;

        // 题号
        viewHolder.tvNum.setText(String.valueOf(questionBean.getQuestion_order()));
        viewHolder.tvNum.setTextColor(Color.WHITE);

        // 显示对错
        if (isRight(position)) {
            viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_right);
        } else {
            viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_wrong);
        }

        // 点击跳转
        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.skip(questionBean.getQuestion_index());
            }
        });
    }

    private boolean isRight(int position) {
        if (mAnswers == null || position >= mAnswers.size()) return false;

        MeasureAnswerBean answerBean = mAnswers.get(position);
        return answerBean != null && answerBean.is_right();
    }

    private class ViewHolder {
        ImageView ivBg;
        TextView tvNum;
    }
}
