package com.appublisher.quizbank.common.opencourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;

import java.util.ArrayList;

/**
 * 公开课评价弹窗评论内容Gird
 */
public class GridOpencourseGradeAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> mTags;

    public GridOpencourseGradeAdapter(Context context, ArrayList<String> tags) {
        this.mContext = context;
        this.mTags = tags;
    }

    @Override
    public int getCount() {
        return mTags == null ? 0 : mTags.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_alert_grade, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.item_alert_tv);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     * @param viewHolder ViewHolder
     * @param position position
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mTags == null || position >= mTags.size()) return;
        viewHolder.tvContent.setText(mTags.get(position));
    }

    private class ViewHolder {
        TextView tvContent;
    }

}
