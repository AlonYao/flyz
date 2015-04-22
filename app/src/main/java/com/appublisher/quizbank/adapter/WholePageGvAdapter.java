package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;

/**
 * 整卷练习GridView容器
 */
public class WholePageGvAdapter extends BaseAdapter {

    Activity mActivity;

    public WholePageGvAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 100;
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

        viewHolder.tvItem.setText("黑龙江");

        return convertView;
    }

    private class ViewHolder {
        TextView tvItem;
    }
}
