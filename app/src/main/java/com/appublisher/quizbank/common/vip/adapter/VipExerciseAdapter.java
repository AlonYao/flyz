package com.appublisher.quizbank.common.vip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.Utils;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;
import com.nineoldandroids.view.ViewHelper;

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
            viewHolder.classText = (TextView) convertView.findViewById(R.id.class_name);
            viewHolder.courseText = (TextView) convertView.findViewById(R.id.course_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VipExerciseResp.ExercisesBean exercisesBean = list.get(position);
        viewHolder.teacherText.setText(exercisesBean.getTeacher_name() + " - " + exercisesBean.getName());
        viewHolder.timeText.setText(exercisesBean.getEnd_time());
        viewHolder.statusText.setText(exercisesBean.getStatus_text());
        viewHolder.classText.setText(exercisesBean.getClass_name());
        viewHolder.courseText.setText(exercisesBean.getCourse_name());
        //未完成状态显示倒计时时间
        if (exercisesBean.getStatus() == 0) {
            long time = Utils.getSecondsByDateMinusNow(exercisesBean.getEnd_time()) / (60 * 60);
            if (time >= 24) {
                long day = time / 24;
                viewHolder.statusText.setText(day + "天后到期");
            } else if (time > 0) {
                viewHolder.statusText.setText(time + "小时后到期");
            } else {
                time = (Utils.getSecondsByDateMinusNow(exercisesBean.getEnd_time()) % (60 * 60)) / 60;
                if (time > 1) {
                    viewHolder.statusText.setText(time + "分钟后到期");
                } else if (time > 0 && time < 1) {
                    viewHolder.statusText.setText("1分钟后到期");
                }
            }
        }

        ViewHelper.setAlpha(convertView, 1.0f);
        final int type = exercisesBean.getExercise_type();
        if (type == 1 || type == 2 || type == 3 || type == 5 || type == 6 | type == 7 || type == 8 || type == 9) {
        } else {
            ViewHelper.setAlpha(convertView, 0.5f);
        }
        return convertView;
    }

    class ViewHolder {
        TextView teacherText;
        TextView timeText;
        TextView statusText;
        TextView classText;
        TextView courseText;
    }

}
