package com.appublisher.quizbank.common.vip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;

import java.util.List;

/**
 * Created by jinbao on 2016/9/7.
 */
public class VipExerciseAdapter extends BaseAdapter {

    private Context context;
    private List<VipExerciseResp.ExercisesBean> list;

    public VipExerciseAdapter(Context context, List<VipExerciseResp.ExercisesBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.vip_exercise_item, null);
            viewHolder.teacherText = (TextView) convertView.findViewById(R.id.teacher_exercise);
            viewHolder.statusText = (TextView) convertView.findViewById(R.id.status_text);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.time_text);
            viewHolder.classText = (TextView) convertView.findViewById(R.id.class_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VipExerciseResp.ExercisesBean exercisesBean = list.get(position);
        viewHolder.teacherText.setText(exercisesBean.getTeacher_name() + " - " + exercisesBean.getName());
        viewHolder.timeText.setText(exercisesBean.getEnd_time());
        viewHolder.statusText.setText(exercisesBean.getStatus_text());
        viewHolder.classText.setText(exercisesBean.getClass_name());
        return convertView;
    }

    class ViewHolder {
        TextView teacherText;
        TextView timeText;
        TextView statusText;
        TextView classText;
    }

}
