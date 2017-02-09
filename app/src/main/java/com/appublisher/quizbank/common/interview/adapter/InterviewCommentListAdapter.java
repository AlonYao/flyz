package com.appublisher.quizbank.common.interview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentM;

import java.util.List;

/**
 * Created by jinbao on 2017/2/9.
 */

public class InterviewCommentListAdapter extends BaseAdapter {
    private Context context;
    private List<InterviewCommentM> list;

    public InterviewCommentListAdapter(Context context, List<InterviewCommentM> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.interview_comment_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.questionContent = (TextView) convertView.findViewById(R.id.question_content);
            viewHolder.noteName = (TextView) convertView.findViewById(R.id.note_name);
            viewHolder.statusTv = (TextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.questionContent.setText(list.get(position).getQuestion());
        viewHolder.noteName.setText(list.get(position).getNote_name());
        viewHolder.statusTv.setText(list.get(position).getComment_status());

        return convertView;
    }

    class ViewHolder {
        TextView questionContent;
        TextView noteName;
        TextView statusTv;
    }
}
