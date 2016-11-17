package com.appublisher.quizbank.common.interview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;

import java.util.List;

/**
 * Created by jinbao on 2016/11/17.
 */

public class FilterAreaAdapter extends BaseAdapter {

    private Context context;
    private List<InterviewFilterResp.AreaBean> list;

    public FilterAreaAdapter(Context context, List<InterviewFilterResp.AreaBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pop_filter_item, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(list.get(position).getArea());
        return convertView;
    }

    class ViewHolder {
        private TextView textView;
    }
}
