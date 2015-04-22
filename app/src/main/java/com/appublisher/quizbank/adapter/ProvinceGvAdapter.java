package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.wholepage.AreaM;

import java.util.ArrayList;

/**
 * 整卷练习GridView容器
 */
public class ProvinceGvAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<AreaM> mAreas;

    public ProvinceGvAdapter(Activity activity, ArrayList<AreaM> areas) {
        mActivity = activity;
        mAreas = areas;
    }

    @Override
    public int getCount() {
        return mAreas.size();
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

        if (mAreas != null && position < mAreas.size()) {
            AreaM area = mAreas.get(position);

            if (area != null) {
                viewHolder.tvItem.setText(area.getName() == null ? "省份" : area.getName());
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvItem;
    }
}
