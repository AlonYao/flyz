package com.appublisher.quizbank.model.offline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.offline.activity.OfflineActivity;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCourseM;

import java.util.ArrayList;

/**
 * 已购课程列表Adapter
 */
public class PurchasedCoursesAdapter extends BaseAdapter{

    private OfflineActivity mActivity;
    private ArrayList<PurchasedCourseM> mCourses;

    public PurchasedCoursesAdapter(OfflineActivity activity, ArrayList<PurchasedCourseM> courses) {
        mActivity = activity;
        mCourses = courses;
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

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.purchased_courses_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_purchased_title);
            viewHolder.tvTeacher = (TextView) convertView.findViewById(R.id.item_purchased_teacher);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置内容
        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     * @param viewHolder ViewHolder
     * @param position 位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mCourses == null || position >= mCourses.size()) return;

        PurchasedCourseM course = mCourses.get(position);
        if (course == null) return;

        viewHolder.tvTitle.setText(course.getName());
        viewHolder.tvTeacher.setText(course.getLector());
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvTeacher;
    }

}
