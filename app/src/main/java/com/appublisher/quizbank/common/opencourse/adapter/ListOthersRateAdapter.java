package com.appublisher.quizbank.common.opencourse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.netdata.RateListOthersItem;
import com.appublisher.quizbank.network.Request;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * 公开课模块：其他用户评价
 */
public class ListOthersRateAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<RateListOthersItem> mItems;
    private Request mRequest;

    public ListOthersRateAdapter(Context context, ArrayList<RateListOthersItem> items) {
        this.mContext = context;
        this.mItems = items;
        this.mRequest = new Request(context);
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
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

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_rate, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rb = (RatingBar) convertView.findViewById(R.id.item_rate_rb);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_rate_username);
            viewHolder.tvComment = (TextView) convertView.findViewById(R.id.item_rate_comment);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.item_rate_date);
            viewHolder.ivAvatar =
                    (RoundedImageView) convertView.findViewById(R.id.item_rate_avatar);
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
     * @param position position
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mItems == null || position >= mItems.size()) return;

        RateListOthersItem item = mItems.get(position);
        if (item == null) return;

        viewHolder.rb.setRating(item.getScore());
        viewHolder.tvName.setText(item.getNickname());
        viewHolder.tvComment.setText(item.getComment());
        viewHolder.tvDate.setText(item.getRate_time());

        if (item.getAvatar() != null && item.getAvatar().length() > 0) {
            mRequest.loadImage(item.getAvatar(), viewHolder.ivAvatar);
        }
    }

    private class ViewHolder {
        RatingBar rb;
        TextView tvName;
        TextView tvComment;
        TextView tvDate;
        RoundedImageView ivAvatar;
    }
}
