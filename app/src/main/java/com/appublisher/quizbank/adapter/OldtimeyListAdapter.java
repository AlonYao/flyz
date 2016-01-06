package com.appublisher.quizbank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.common.opencourse.netdata.StaticCourseM;

import java.util.ArrayList;

/**
 * 往期回放列表
 */
public class OldtimeyListAdapter extends BaseAdapter{

    private OpenCourseUnstartActivity mActivity;
    private ArrayList<StaticCourseM> mStaticCourses;

    public OldtimeyListAdapter(OpenCourseUnstartActivity activity,
                               ArrayList<StaticCourseM> staticCourses) {
        mActivity = activity;
        mStaticCourses = staticCourses;
    }

    @Override
    public int getCount() {
        return mStaticCourses == null ? 0 : mStaticCourses.size();
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
                    R.layout.opencourse_oldtimey_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_oldtimey_name);
            viewHolder.tvTeacher = (TextView) convertView.findViewById(R.id.item_oldtimey_teacher);
            viewHolder.ivImg = (ImageView) convertView.findViewById(R.id.item_oldtimey_img);

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
     * @param position 位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mStaticCourses == null || position >= mStaticCourses.size()) return;

        StaticCourseM staticCourse = mStaticCourses.get(position);

        if (staticCourse == null) return;

        mActivity.mRequest.loadImage(staticCourse.getCover_pic(), viewHolder.ivImg);
        viewHolder.tvName.setText(staticCourse.getName());
        viewHolder.tvTeacher.setText(String.valueOf(staticCourse.getLector()));
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvTeacher;
        ImageView ivImg;
    }
}
