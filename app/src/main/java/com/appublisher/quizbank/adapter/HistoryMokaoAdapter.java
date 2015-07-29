package com.appublisher.quizbank.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.HistoryMokaoActivity;
import com.appublisher.quizbank.model.netdata.historymokao.HistoryMokaoM;
import com.appublisher.quizbank.utils.Utils;

import java.util.ArrayList;

/**
 * 历史模考列表容器
 */
public class HistoryMokaoAdapter extends BaseAdapter{

    private HistoryMokaoActivity mActivity;
    ArrayList<HistoryMokaoM> mHistoryMokaos;

    public HistoryMokaoAdapter(HistoryMokaoActivity activity,
                               ArrayList<HistoryMokaoM> historyMokaos) {
        mActivity = activity;
        mHistoryMokaos = historyMokaos;
    }

    @Override
    public int getCount() {
        return mHistoryMokaos.size();
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
                    R.layout.historymokao_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.historymokao_item_name);
            viewHolder.tvNum = (TextView) convertView.findViewById(R.id.historymokao_item_num);
            viewHolder.ivStatus =
                    (ImageView) convertView.findViewById(R.id.historymokao_item_status);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mHistoryMokaos != null && position < mHistoryMokaos.size()) {
            HistoryMokaoM historyMokao = mHistoryMokaos.get(position);

            if (historyMokao != null) {
                int num = historyMokao.getPersons_num();
                String status = historyMokao.getStatus();

                viewHolder.tvName.setText(historyMokao.getName());
                viewHolder.tvNum.setText(String.valueOf(num)
                        + "人参加,击败" + Utils.rateToPercent(historyMokao.getDefeat()) + "%");

                if ("done".equals(status)) {
                    viewHolder.ivStatus.setImageResource(R.drawable.historymokao_done);
                } else if ("undone".equals(status)) {
                    viewHolder.ivStatus.setImageResource(R.drawable.historymokao_undone);
                } else {
                    viewHolder.ivStatus.setImageResource(R.color.transparency);
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvNum;
        ImageView ivStatus;
    }
}
