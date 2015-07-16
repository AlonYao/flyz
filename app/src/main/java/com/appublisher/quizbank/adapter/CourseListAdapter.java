package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            viewHolder.llZhibokeDesc =
                    (LinearLayout) convertView.findViewById(R.id.courselist_item_zhiboke_desc);
            viewHolder.tvLubokeDesc =
                    (TextView) convertView.findViewById(R.id.courselist_item_luboke_desc);
            viewHolder.tvZhibokeDesc =
                    (TextView) convertView.findViewById(R.id.courselist_item_zhiboke_desc_tv);
            viewHolder.ivBuyFlag =
                    (ImageView) convertView.findViewById(R.id.courselist_item_buyflag);
            viewHolder.tvStatus =
                    (TextView) convertView.findViewById(R.id.courselist_item_status);
            viewHolder.tvPersons =
                    (TextView) convertView.findViewById(R.id.courselist_item_persons);

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

        // 是否购买
        if (course.is_purchased()) {
            /** 已购买 **/
            // 课程标记
            viewHolder.ivBuyFlag.setVisibility(View.VISIBLE);

            // 已购人数
            viewHolder.tvPersons.setVisibility(View.GONE);

            // 课程状态
            if ("live".equals(course.getType())) {
                // 直播课
                if ("on_air".equals(course.getStatus())) {
                    // 正在上课
                    viewHolder.tvStatus.setText("正在上课");
                    viewHolder.tvStatus.setTextColor(
                            mActivity.getResources().getColor(R.color.course_inprogress));
                } else if ("unstart".equals(course.getStatus())) {
                    // 即将开始
                    viewHolder.tvStatus.setText("即将开始");
                    viewHolder.tvStatus.setTextColor(
                            mActivity.getResources().getColor(R.color.course_soon));
                } else if ("replay".equals(course.getStatus())) {
                    // 已结束
                    viewHolder.tvStatus.setText("已结束");
                    viewHolder.tvStatus.setTextColor(
                            mActivity.getResources().getColor(R.color.course_end));
                }

            } else if ("vod".equals(course.getType())) {
                // 录播课
                viewHolder.tvStatus.setText("观看视频");
                viewHolder.tvStatus.setTextColor(
                        mActivity.getResources().getColor(R.color.course_watch));
            }

        } else {
            /** 未购买 **/
            // 课程标记
            viewHolder.ivBuyFlag.setVisibility(View.GONE);

            // 已购人数
            viewHolder.tvPersons.setVisibility(View.VISIBLE);
            viewHolder.tvPersons.setText(String.valueOf(course.getPersons_num()) + "人已购");

            // 课程价格
            viewHolder.tvStatus.setText("¥ " + String.valueOf(course.getPrice()));
            viewHolder.tvStatus.setTextColor(
                    mActivity.getResources().getColor(R.color.course_price));
        }

        if ("live".equals(course.getType())) {
            /** 直播课 **/
            // 课程标记
            changeTextDrawable(R.drawable.course_zhiboke_flag, viewHolder.tvTitle);

            // 课程描述
            viewHolder.llZhibokeDesc.setVisibility(View.VISIBLE);
            viewHolder.tvLubokeDesc.setVisibility(View.GONE);

            // 讲师
            String teacher = "";
            ArrayList<String> teachers = course.getLectors();
            int size = teachers == null ? 0 : teachers.size();
            for (int i = 0; i < size; i++) {
                teacher = teacher + teachers.get(i) + " ";
            }
            viewHolder.tvTeacher.setText(teacher);

            // 描述详情
            setZhibokeDesc(course, viewHolder.tvZhibokeDesc);

        } else if ("vod".equals(course.getType())) {
            /** 录播课 **/
            // 课程标记
            changeTextDrawable(R.drawable.course_luboke_flag, viewHolder.tvTitle);

            // 课程描述
            viewHolder.llZhibokeDesc.setVisibility(View.GONE);
            viewHolder.tvLubokeDesc.setVisibility(View.VISIBLE);
            viewHolder.tvLubokeDesc.setText(course.getIntroduction());
        }
    }

    /**
     * 设置直播课描述
     * @param course 课程数据对象
     * @param textView TextView控件
     */
    private void setZhibokeDesc(CourseM course, TextView textView) {
        if (course == null) return;

        String desc = "";

        try {
            if (course.getStart_time() != null && course.getStart_time().length() >= 10
                    && course.getEnd_time() != null && course.getStart_time().length() >= 10) {
                String start = course.getStart_time().substring(5, 10).replace("-", "月") + "日";
                String end = course.getEnd_time().substring(5, 10).replace("-", "月") + "日";
                desc = desc + start + "-" + end;
            }

            desc = desc + "  " + String.valueOf(course.getPeriods()) + "课时";
        } catch (Exception e) {
            // Empty
        }

        textView.setText(desc);
    }

    /**
     * 改变图标
     * @param img 图标
     * @param textView Textview控件
     */
    private void changeTextDrawable(int img, TextView textView) {
        Drawable drawable = mActivity.getResources().getDrawable(img);
        if (drawable == null) return;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvTeacher;
        TextView tvLubokeDesc;
        TextView tvZhibokeDesc;
        TextView tvStatus;
        TextView tvPersons;
        ImageView ivBuyFlag;
        LinearLayout llZhibokeDesc;
    }
}
