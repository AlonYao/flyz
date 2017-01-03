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
    private final String mType;
    private final int VIEW_TYPE = 3;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;

    public HistoryPapersListAdapter(Activity activity, ArrayList<HistoryPaperM> historyPapers, String type) {
        mActivity = activity;
        mHistoryPapers = historyPapers;
        mType = type;
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

    //每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        if ("write".equals(mType)) {
            return TYPE_1;
        }else if ("interview".equals(mType)) {
            return TYPE_2;
        }
        return super.getItemViewType(position);
    }

    //返回样式的数量
    @Override
    public int getViewTypeCount () {
        return 2;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int type = getItemViewType(position);

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
        } else {
            ivLogo.setImageResource(R.drawable.record_special);
        }
    }

    private class ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }
    private class ViewHolder2 {
        ImageView ivLogo;
        TextView tvName;
        TextView tvContent;
        TextView tvDate;
    }

}
