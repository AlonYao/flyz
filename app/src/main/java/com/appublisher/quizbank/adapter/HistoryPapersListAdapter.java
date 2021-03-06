package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.lib_basic.Utils;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;

import java.util.ArrayList;

/**
 * 学习历史List容器
 */
public class HistoryPapersListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<HistoryPaperM> mHistoryPapers;

    public HistoryPapersListAdapter(Activity activity, ArrayList<HistoryPaperM> historyPapers) {
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

        WriteViewHolder mWriteViewHolder;
        // 笔试页面
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(          // 笔试页面item
                    R.layout.studyrecord_lv_item, parent, false);
            mWriteViewHolder = new WriteViewHolder();
            mWriteViewHolder.ivLogo = (ImageView) convertView.findViewById(R.id.studyrecord_logo);
            mWriteViewHolder.tvName = (TextView) convertView.findViewById(R.id.studyrecord_name);
            mWriteViewHolder.tvContent = (TextView) convertView.findViewById(R.id.studyrecord_content);
            mWriteViewHolder.tvDate = (TextView) convertView.findViewById(R.id.studyrecord_date);

            convertView.setTag(mWriteViewHolder);
        } else {
            mWriteViewHolder = (WriteViewHolder) convertView.getTag();
        }
        // 给控件赋值
        if (mHistoryPapers != null && mHistoryPapers.size() > position) {
            HistoryPaperM historyPaper = mHistoryPapers.get(position);
            if (historyPaper != null) {
                // Logo
                setLogo(mWriteViewHolder.ivLogo, historyPaper.getPaper_type());
                // 标题
                mWriteViewHolder.tvName.setText(historyPaper.getName());
                // 正确率&完成状态
                String status = historyPaper.getStatus();
                if ("done".equals(status)) {
                    mWriteViewHolder.tvContent.setText("正确率"
                            + Utils.rateToPercent(historyPaper.getAccuracy())
                            + "%");
                } else {
                    mWriteViewHolder.tvContent.setText("未完成");
                }
                // 时间
                mWriteViewHolder.tvDate.setText(historyPaper.getAction_time());
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
        if ("entire".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_entire);
        } else if ("mokao".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_mokao);
        } else if ("note".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_special);
        } else if ("auto".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_quick);
        } else if ("error".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_wrong);
        } else if ("collect".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_collect);
        } else if ("evaluate".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_gufen);
        } else if ("mock".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.record_mock);
        }else {
            ivLogo.setImageResource(R.drawable.record_special);
        }
    }

    private class WriteViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }

}
