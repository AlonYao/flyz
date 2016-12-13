package com.appublisher.quizbank.common.interview.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;

import java.util.List;

/**
 * Created by jinbao on 2016/11/17.
 */

public class FilterYearAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> list;

    public FilterYearAdapter(Context context, List<Integer> list) {
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

            Drawable drawable = viewHolder.textView.getBackground();
            if (drawable instanceof GradientDrawable) {
                //noinspection deprecation
                ((GradientDrawable) drawable).setColor(
                        context.getResources().getColor(R.color.common_bg));
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String text;
        if (position == 0) {
            text = "全部";
        } else {
            text = list.get(position) + "年";
        }
        viewHolder.textView.setText(text);
        return convertView;
    }

    class ViewHolder {
        private TextView textView;
    }
}
