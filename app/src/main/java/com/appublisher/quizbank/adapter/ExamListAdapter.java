package com.appublisher.quizbank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;

import java.util.List;

/**
 * 考试列表容器
 */
public class ExamListAdapter extends BaseAdapter{

    private Context mContext;
    private List<ExamItemModel> mExams;
    private int mSelectedPosition = -1;

    /**
     * 构造函数
     * @param context  上下文
     */
    public ExamListAdapter(Context context, List<ExamItemModel> exams) {
        this.mContext = context;
        this.mExams = exams;
    }

    @Override
    public int getCount() {
        return mExams.size();
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

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.exam_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvExamItem = (TextView) convertView.findViewById(R.id.exam_item_tv);
            viewHolder.ivSelect = (ImageView) convertView.findViewById(R.id.exam_item_iv);
            viewHolder.vLine = convertView.findViewById(R.id.exam_item_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置内容
        ExamItemModel exam = mExams.get(position);
        if (exam != null) {
            viewHolder.tvExamItem.setText(exam.getName());
            // 设置选中效果
            if (mSelectedPosition == position) {
                viewHolder.ivSelect.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivSelect.setVisibility(View.GONE);
            }

            // 最后一项不显示分割线
            if (position == mExams.size() - 1) {
                viewHolder.vLine.setVisibility(View.GONE);
            } else {
                viewHolder.vLine.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvExamItem;
        ImageView ivSelect;
        View vLine;
    }

    /**
     * 设置被选中的项目的位置
     * @param position 被选中项目的位置
     */
    public void setSelectedPosition(int position) {
        this.mSelectedPosition = position;
    }

}
