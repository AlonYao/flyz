package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.InterviewRecordListItemBean;

import java.util.ArrayList;

/**
 * 学习历史List容器
 */
public class InterviewHistoryPapersListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<InterviewRecordListItemBean> mHistoryPapers;


    public InterviewHistoryPapersListAdapter(Activity activity, ArrayList<InterviewRecordListItemBean> historyPapers) {
        mActivity = activity;
        mHistoryPapers = historyPapers;

    }

    @Override
    public int getCount() {
        return mHistoryPapers.size();
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

        InterviewViewHolder mInterviewViewHolder;
                     // 面试页面
            if(convertView == null){
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.studyrecord_interview_lv_item,parent,false); // 面试页面的item
                mInterviewViewHolder = new InterviewViewHolder();
                mInterviewViewHolder.ivLogo = (ImageView) convertView.findViewById(R.id.studyrecord_logo);
                mInterviewViewHolder.tvName = (TextView) convertView.findViewById(R.id.interview_center_data);
                mInterviewViewHolder.tvDate = (TextView) convertView.findViewById(R.id.interview_right_data);
                convertView.setTag(mInterviewViewHolder);
            }else{
                mInterviewViewHolder = (InterviewViewHolder) convertView.getTag();
            }
            // 给控件赋值
            if (mHistoryPapers != null && mHistoryPapers.size() > position) {
                InterviewRecordListItemBean historyPaper = mHistoryPapers.get(position);
                if (historyPaper != null) {
                    // 设置Logo
                 setLogo(mInterviewViewHolder.ivLogo, historyPaper.getType());
                 setText(mInterviewViewHolder.tvName, historyPaper.getType());

                 mInterviewViewHolder.tvDate.setText(historyPaper.getTime());

                }
            }
        return convertView;
    }

    /**
     * 设置Logo
     *
     * @param ivLogo    控件
     * @param paperType 试卷类型
     */
    private void setLogo(ImageView ivLogo, String paperType) {
        if("guokao".equals(paperType)){
            ivLogo.setImageResource(R.drawable.studyrecord_interview_guokao);
        }else if("category".equals(paperType)){
            ivLogo.setImageResource(R.drawable.studyrecord_interview_category);
        }else if("teacher".equals(paperType)){
            ivLogo.setImageResource(R.drawable.studyrecord_interview_teacher);
        }else if("history".equals(paperType)){
            ivLogo.setImageResource(R.drawable.record_entire);
        }
    }
    /*
    *   给控件的名称赋值
    * */
    private void setText(TextView textView, String paperType){
        if("guokao".equals(paperType)){
            textView.setText("国考精选");
        }else if("category".equals(paperType)){
            textView.setText("分类突破");
        }else if("teacher".equals(paperType)){
            textView.setText("名师解析");
        }else if("history".equals(paperType)){
            textView.setText("历年真题");
        }
    }

    private class InterviewViewHolder{
        ImageView ivLogo;
        TextView tvName;
        TextView tvDate;
    }

}
