package com.appublisher.quizbank.model.offline.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.offline.activity.OfflineClassActivity;
import com.appublisher.quizbank.model.offline.netdata.PurchasedClassM;

import java.util.ArrayList;

/**
 * 已购课程列表Adapter
 */
public class PurchasedClassesAdapter extends BaseAdapter{

    private OfflineClassActivity mActivity;
    private ArrayList<PurchasedClassM> mClasses;

    public PurchasedClassesAdapter(OfflineClassActivity activity,
                                   ArrayList<PurchasedClassM> classes) {
        mActivity = activity;
        mClasses = classes;
    }

    @Override
    public int getCount() {
        return mClasses == null ? 0 : mClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.purchased_classes_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvTitle =
                    (TextView) convertView.findViewById(R.id.item_purchased_classes_title);
            viewHolder.tvStatus =
                    (TextView) convertView.findViewById(R.id.item_purchased_classes_status);
            viewHolder.ivStatus =
                    (ImageView) convertView.findViewById(R.id.item_purchased_classes_status_iv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置内容
        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     * @param viewHolder ViewHolder
     * @param position 位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mClasses == null || position >= mClasses.size()) return;

        PurchasedClassM classM = mClasses.get(position);
        if (classM == null) return;

        String date = "";
        try {
            String startTime = classM.getStart_time();
            date = startTime.substring(0, 10).replaceAll("-", ".").substring(5, 10);

            String firstNum = date.substring(0, 1);

            if ("0".equals(firstNum)) {
                date = date.substring(1, 5);
            }

        } catch (Exception e) {
            // Empty
        }

        String title = date + " " + classM.getLector() + "：" + classM.getName();
        viewHolder.tvTitle.setText(title);
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvStatus;
        ImageView ivStatus;
    }

}
