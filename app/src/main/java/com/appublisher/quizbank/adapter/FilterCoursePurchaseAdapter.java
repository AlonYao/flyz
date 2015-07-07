package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.course.FilterAreaM;

import java.util.ArrayList;

/**
 * Filter：课程购买状态Adapter
 */
public class FilterCoursePurchaseAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<String> mFilterPurchase;

    public FilterCoursePurchaseAdapter(Activity activity, ArrayList<String> filterPurchase) {
        mActivity = activity;
        mFilterPurchase = filterPurchase;
    }

    @Override
    public int getCount() {
        return mFilterPurchase == null ? 0 : mFilterPurchase.size();
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
                    R.layout.course_filter_gv_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvItem =
                    (TextView) convertView.findViewById(R.id.course_filter_gv_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     * @param viewHolder ViewHolder
     * @param position 位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mFilterPurchase == null || position >= mFilterPurchase.size()) return;
        viewHolder.tvItem.setText(mFilterPurchase.get(position));
    }

    class ViewHolder {
        TextView tvItem;
    }
}
