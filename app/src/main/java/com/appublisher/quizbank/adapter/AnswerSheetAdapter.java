package com.appublisher.quizbank.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 答题卡
 */
public class AnswerSheetAdapter extends BaseAdapter{

    private AnswerSheetActivity mActivity;
    private ArrayList<HashMap<String, Object>> mUserAnswerList;

    public AnswerSheetAdapter(AnswerSheetActivity activity,
                              ArrayList<HashMap<String, Object>> userAnswerList) {
        mActivity = activity;
        mUserAnswerList = userAnswerList;
    }

    @Override
    public int getCount() {
        return mUserAnswerList.size();
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.answer_sheet_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.answer_sheet_item_bg);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.answer_sheet_item_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNum.setText(String.valueOf(position + 1));

        HashMap<String, Object> userAnswerMap = mUserAnswerList.get(position);

        if ("analysis".equals(mActivity.mFrom)) {
            // 解析
            boolean isRight = false;

            if (userAnswerMap.containsKey("answer")
                    && userAnswerMap.containsKey("right_answer")
                    && userAnswerMap.get("answer") != null
                    && userAnswerMap.get("right_answer").equals(userAnswerMap.get("answer"))) {
                isRight = true;
            }

            if (isRight) {
                viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_right);
            } else {
                viewHolder.ivBg.setImageResource(R.drawable.measure_analysis_wrong);
            }

            viewHolder.tvNum.setTextColor(Color.WHITE);

        } else {
            // 非解析
            if (userAnswerMap.containsKey("answer")
                    && userAnswerMap.get("answer") != null
                    && !userAnswerMap.get("answer").equals("")) {
                viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_selected);
                viewHolder.tvNum.setTextColor(Color.WHITE);
            } else {
                viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_unselect);
                viewHolder.tvNum.setTextColor(
                        mActivity.getResources().getColor(R.color.common_text));
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView ivBg;
        TextView tvNum;
    }
}
