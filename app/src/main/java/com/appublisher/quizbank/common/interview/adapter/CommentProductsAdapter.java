package com.appublisher.quizbank.common.interview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.CommentProductM;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentProductsResp;

/**
 * Created by jinbao on 2017/1/23.
 */

public class CommentProductsAdapter extends BaseAdapter {

    private InterviewCommentProductsResp commentProductsResp;
    private Context context;

    public void setCommentProductsResp(InterviewCommentProductsResp commentProductsResp) {
        this.commentProductsResp = commentProductsResp;
    }

    public CommentProductsAdapter(Context context) {
        this.context = context;

    }


    @Override
    public int getCount() {
        return commentProductsResp == null ? 0 : commentProductsResp.getList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.interview_comment_product_item, null);
            viewHolder = new ViewHolder();
            viewHolder.commentTimesTv = (TextView) convertView.findViewById(R.id.comment_times);
            viewHolder.firstBuyTv = (TextView) convertView.findViewById(R.id.first_buy);
            viewHolder.discountTv = (TextView) convertView.findViewById(R.id.discount);
            viewHolder.priceTv = (TextView) convertView.findViewById(R.id.price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CommentProductM commentProductM = commentProductsResp.getList().get(position);
        if (commentProductM != null) {
            if (position == 0) {
                viewHolder.firstBuyTv.setVisibility(View.INVISIBLE);
                viewHolder.discountTv.setVisibility(View.INVISIBLE);
                if (commentProductsResp.isFirst_buy()) {
                    viewHolder.firstBuyTv.setText("首次尝鲜价，原价" + commentProductM.getOrigin_price() + "元");
                    viewHolder.firstBuyTv.setVisibility(View.VISIBLE);
                }
            }

            viewHolder.discountTv.setText(commentProductM.getDiscount() + "折");
            if ("10".equals(commentProductM.getDiscount())) {
                viewHolder.discountTv.setVisibility(View.INVISIBLE);
            }
            viewHolder.commentTimesTv.setText(commentProductM.getProduct_name());
            viewHolder.priceTv.setText(commentProductM.getPrice() + "元");
        }
        return convertView;
    }

    class ViewHolder {
        TextView commentTimesTv;
        TextView firstBuyTv;
        TextView discountTv;
        TextView priceTv;
    }
}
