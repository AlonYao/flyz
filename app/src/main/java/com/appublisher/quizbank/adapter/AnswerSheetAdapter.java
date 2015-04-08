package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appublisher.quizbank.R;

/**
 * 答题卡
 */
public class AnswerSheetAdapter extends BaseAdapter{

    private Activity mActivity;

    public AnswerSheetAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 11;
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
        View view = LayoutInflater.from(mActivity).inflate(R.layout.answer_sheet_item, parent, false);


        return view;
    }
}
