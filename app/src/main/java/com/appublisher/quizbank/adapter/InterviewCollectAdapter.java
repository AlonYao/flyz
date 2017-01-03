package com.appublisher.quizbank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.RecordCollectActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewCollectResp;

import java.util.List;

/**
 * Created by Admin on 2017/1/3.
 */

public class InterviewCollectAdapter extends BaseAdapter{

    private final RecordCollectActivity mActivity;
    private final List<InterviewCollectResp.InterviewM> mList;

    public InterviewCollectAdapter(RecordCollectActivity recordCollectActivity, List<InterviewCollectResp.InterviewM> list) {
        mActivity = recordCollectActivity;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.record_collect_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.record_collect_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mList.get(position).getNote());
        return convertView;
    }

    class ViewHolder {
        private TextView textView;
    }
}
