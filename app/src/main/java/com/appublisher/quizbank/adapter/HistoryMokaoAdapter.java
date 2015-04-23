package com.appublisher.quizbank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.HistoryMokaoActivity;

/**
 * 历史模考列表容器
 */
public class HistoryMokaoAdapter extends BaseAdapter{

    private HistoryMokaoActivity mActivity;

    public HistoryMokaoAdapter(HistoryMokaoActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 0;
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
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.wholepage_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvItem =
                    (TextView) convertView.findViewById(R.id.wholepage_list_item_tv);
            viewHolder.line = convertView.findViewById(R.id.wholepage_list_item_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvItem;
        View line;
    }
}
