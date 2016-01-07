package com.appublisher.quizbank.common.opencourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseListItem;

import java.util.ArrayList;

/**
 * 公开课Adapter（非回放课程）
 */
public class ListOpencourseAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OpenCourseListItem> mCourses;

    public ListOpencourseAdapter(Context context, ArrayList<OpenCourseListItem> courses) {
        this.mContext = context;
        this.mCourses = courses;
    }

    @Override
    public int getCount() {
        return mCourses == null ? 0 : mCourses.size();
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
                    R.layout.item_opencourse, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.item_opencourse_desc);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_opencourse_name);
            viewHolder.ivFlagLeft =
                    (ImageView) convertView.findViewById(R.id.item_opencourse_flag_left);
            viewHolder.ivFlagRight =
                    (ImageView) convertView.findViewById(R.id.item_opencourse_flag_right);
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
        if (mCourses == null || position >= mCourses.size()) return;

        OpenCourseListItem course = mCourses.get(position);
        if (course == null) return;

        String desc = course.getLector() + "  " + course.getDate();
        viewHolder.tvDesc.setText(desc);

        viewHolder.tvName.setText(course.getName());

        if (course.is_onair()) {
            // 如果正在直播
            viewHolder.ivFlagLeft.setVisibility(View.VISIBLE);
            viewHolder.ivFlagRight.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivFlagLeft.setVisibility(View.INVISIBLE);
            viewHolder.ivFlagRight.setVisibility(View.INVISIBLE);
        }
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvDesc;
        ImageView ivFlagLeft;
        ImageView ivFlagRight;
    }
}
