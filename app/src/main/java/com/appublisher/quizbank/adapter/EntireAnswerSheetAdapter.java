package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 答题卡
 */
public class EntireAnswerSheetAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<HashMap<String, Object>> mUserAnswerList;
    private int mOffset;

    public EntireAnswerSheetAdapter(Activity activity,
                                    ArrayList<HashMap<String, Object>> userAnswerList,
                                    int offset) {
        mActivity = activity;
        mUserAnswerList = userAnswerList;
        mOffset = offset;
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

        viewHolder.tvNum.setText(String.valueOf(mOffset + position + 1));

        HashMap<String, Object> userAnswerMap = mUserAnswerList.get(position);
        if (userAnswerMap.containsKey("answer")
                && userAnswerMap.get("answer") != null
                && !userAnswerMap.get("answer").equals("")) {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_selected);
            viewHolder.tvNum.setTextColor(Color.WHITE);
        } else {
            viewHolder.ivBg.setImageResource(R.drawable.answer_sheet_unselect);
            viewHolder.tvNum.setTextColor(mActivity.getResources().getColor(R.color.setting_text));
        }

        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MeasureActivity.class);
                intent.putExtra("position", mOffset + position);
                mActivity.setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
                mActivity.finish();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView ivBg;
        TextView tvNum;
    }
}
