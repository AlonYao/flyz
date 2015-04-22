package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;

import java.util.ArrayList;

/**
 * 整卷练习GridView容器
 */
public class YearGvAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<Integer> mYears;

    public YearGvAdapter(Activity activity, ArrayList<Integer> years) {
        mActivity = activity;
        mYears = years;
    }

    @Override
    public int getCount() {
        return mYears.size();
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
                    R.layout.wholepage_province_gridview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvItem =
                    (TextView) convertView.findViewById(R.id.wholepage_gridview_item_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mYears != null && position < mYears.size()) {
            int yearInt = mYears.get(position);
            String yearString = yearInt == 0 ? "全部" : String.valueOf(yearInt);
            viewHolder.tvItem.setText(yearString);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvItem;
    }
}
