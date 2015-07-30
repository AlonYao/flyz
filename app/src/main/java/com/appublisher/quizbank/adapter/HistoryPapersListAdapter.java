package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.utils.Utils;

import java.util.ArrayList;

/**
 * 学习历史List容器
 */
public class HistoryPapersListAdapter extends BaseAdapter{

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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.studyrecord_lv_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ivLogo =
                    (ImageView) convertView.findViewById(R.id.studyrecord_logo);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.studyrecord_name);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.studyrecord_content);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.studyrecord_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mHistoryPapers != null && mHistoryPapers.size() > position) {
            HistoryPaperM historyPaper = mHistoryPapers.get(position);

            if (historyPaper != null) {
                // Logo
                setLogo(viewHolder.ivLogo, historyPaper.getPaper_type());

                // 标题
                viewHolder.tvName.setText(historyPaper.getName());

                // 正确率&完成状态
                String status = historyPaper.getStatus();

                if ("done".equals(status)) {
                    viewHolder.tvContent.setText("正确率"
                            + Utils.rateToPercent(historyPaper.getAccuracy())
                            + "%");
                } else {
                    viewHolder.tvContent.setText("未完成");
                }

                // 时间
                viewHolder.tvDate.setText(historyPaper.getAction_time());
            }
        }

        return convertView;
    }

    /**
     * 设置Logo
     * @param ivLogo 控件
     * @param paperType 试卷类型
     */
    private void setLogo(ImageView ivLogo, String paperType) {
        if ("entire".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_entire);
        } else if ("mokao".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_mokao);
        } else if ("note".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_special);
        } else if ("auto".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_auto);
        } else if ("error".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_wrong);
        } else if ("collect".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_collect);
        } else if ("evaluate".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_estimate);
        } else if ("mock".equals(paperType)) {
            ivLogo.setImageResource(R.drawable.studyrecord_mock);
        } else {
            ivLogo.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }
}
