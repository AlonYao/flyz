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

import com.appublisher.lib_basic.Logger;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureModel;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.bean.MeasureSubmitBean;
import com.appublisher.quizbank.common.measure.fragment.MeasureSheetFragment;

import java.util.List;

/**
 * 答题卡
 */
public class MeasureSheetAdapter extends BaseAdapter{

    private Context mContext;
    private MeasureSheetFragment mFragment;
    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureSubmitBean> mSubmits;

    public MeasureSheetAdapter(MeasureSheetFragment fragment,
                               List<MeasureQuestionBean> questions) {
        mFragment = fragment;
        mContext = fragment.getActivity();
        mQuestions = questions;
        mSubmits = MeasureModel.getCacheUserAnswer(mContext);
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

        // 做题记录标记
        if (hasRecord(questionBean.getQuestion_order() - 1)) {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_selected);
            viewHolder.tvNum.setTextColor(Color.WHITE);
        } else {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_unselect);
            viewHolder.tvNum.setTextColor(ContextCompat.getColor(mContext, R.color.measure_text));
        }

        // 点击跳转
        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("11111111111111");
//                mActivity.mViewPager.setCurrentItem(questionBean.getQuestion_index());
                mFragment.skip(questionBean.getQuestion_index());
            }
        });
    }

    private boolean hasRecord(int order) {
        if (mSubmits == null || order >= mSubmits.size()) return false;
        MeasureSubmitBean submitBean = mSubmits.get(order);
        return submitBean != null && submitBean.getAnswer().length() > 0;
    }

    private class ViewHolder {
        ImageView ivBg;
        TextView tvNum;
    }
}
