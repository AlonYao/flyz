package com.appublisher.quizbank.common.vip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * 小班：单题突破 问题tab 学生评论
 */

public class VipDTTPReviewAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<VipDTTPResp.UserAnswerBean.ReviewsBean> mReviews;

    public VipDTTPReviewAdapter(Context context,
                                ArrayList<VipDTTPResp.UserAnswerBean.ReviewsBean> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public int getCount() {
        return mReviews == null ? 0 : mReviews.size();
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
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.vip_dttp_review_student_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivAvatar = (RoundedImageView)
                    convertView.findViewById(R.id.vip_dttp_review_student_avatar);
            viewHolder.tvName = (TextView)
                    convertView.findViewById(R.id.vip_dttp_review_student_name);
            viewHolder.tvDate = (TextView)
                    convertView.findViewById(R.id.vip_dttp_review_student_date);
            viewHolder.tvLevel = (TextView)
                    convertView.findViewById(R.id.vip_dttp_review_student_level);
            viewHolder.tvRemark = (TextView)
                    convertView.findViewById(R.id.vip_dttp_review_student_remark);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置内容
        setContent(position, viewHolder);

        return convertView;
    }

    private void setContent(int position, ViewHolder viewHolder) {
        if (mReviews == null || mReviews.size() <= position) return;
        VipDTTPResp.UserAnswerBean.ReviewsBean review = mReviews.get(position);
        if (review == null) return;

        // 头像&昵称
        VipDTTPResp.UserAnswerBean.ReviewsBean.StudentBean studentBean = review.getStudent();
        if (studentBean != null) {
            ImageManager.displayImage(studentBean.getAvatar(), viewHolder.ivAvatar);
            viewHolder.tvName.setText(studentBean.getName());
        }

        viewHolder.tvDate.setText(review.getReview_time());
        viewHolder.tvLevel.setText(review.getReview_level());
        viewHolder.tvRemark.setText(review.getReview_postil());
    }

    private class ViewHolder {
        RoundedImageView ivAvatar;
        TextView tvName;
        TextView tvDate;
        TextView tvLevel;
        TextView tvRemark;
    }

}
