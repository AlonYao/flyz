package com.appublisher.quizbank.common.opencourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassItem;

import java.util.ArrayList;

/**
 * 公开课模块：我的评价（从课程中心进入）
 */
public class ListMyCourseGradeAdapter extends BaseAdapter{

    Context mContext;
    ArrayList<OpenCourseUnrateClassItem> mClasses;

    public ListMyCourseGradeAdapter(Context context, ArrayList<OpenCourseUnrateClassItem> classes) {
        this.mContext = context;
        this.mClasses = classes;
    }

    @Override
    public int getCount() {
        return mClasses == null ? 0 : mClasses.size();
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
                    R.layout.item_unrate_class_course, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.unrate_desc);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.unrate_date);
            viewHolder.tvCourseName =
                    (TextView) convertView.findViewById(R.id.unrate_desc_coursename);
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
        if (mClasses == null || position >= mClasses.size()) return;

        OpenCourseUnrateClassItem item = mClasses.get(position);
        if (item == null) return;

        String desc = item.getLector() + "-" + item.getClass_name();
        viewHolder.tvDesc.setText(desc);

        viewHolder.tvDate.setText(getCourseDate(item));

        viewHolder.tvCourseName.setText(item.getCourse_name());
    }

    /**
     * 获取日期
     * @param item OpenCourseUnrateClassItem
     */
    private String getCourseDate(OpenCourseUnrateClassItem item) {
        if (item == null) return "";

        try {
            return item.getStart_time().substring(5, 16)
                    + "-" + item.getEnd_time().substring(11, 16);
        } catch (Exception e) {
            return "";
        }
    }

    private class ViewHolder {
        TextView tvDesc;
        TextView tvDate;
        TextView tvCourseName;
    }

}
