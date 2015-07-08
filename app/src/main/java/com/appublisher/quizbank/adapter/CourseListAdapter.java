package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.course.CourseM;

import java.util.ArrayList;

/**
 * 课程列表Adapter容器
 */
public class CourseListAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<CourseM> mCourses;

    public CourseListAdapter(Activity activity, ArrayList<CourseM> courses) {
        mActivity = activity;
        mCourses = courses;
    }

    @Override
    public int getCount() {
        return mCourses == null ? 0 : mCourses.size();
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
                    R.layout.course_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvTitle =
                    (TextView) convertView.findViewById(R.id.courselist_item_title);
            viewHolder.tvTeacher =
                    (TextView) convertView.findViewById(R.id.courselist_item_teacher);

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
     * @param position 课程item位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mCourses == null || position >= mCourses.size()) return;

        CourseM course = mCourses.get(position);

        if (course == null) return;

        // 课程名称
        viewHolder.tvTitle.setText(course.getName());

        // 课程讲师
        String teacher = "";
        ArrayList<String> teachers = course.getLectors();
        int size = teachers == null ? 0 : teachers.size();
        for (int i = 0; i < size; i++) {
            teacher = teacher + teachers.get(i) + " ";
        }
        viewHolder.tvTeacher.setText(teacher);
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvTeacher;
    }
}
