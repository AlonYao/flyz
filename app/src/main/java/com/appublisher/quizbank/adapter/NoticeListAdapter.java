package com.appublisher.quizbank.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.SystemNoticeActivity;
import com.appublisher.quizbank.model.netdata.notice.NoticeM;

/**
 * 系统通知
 */
public class NoticeListAdapter extends BaseAdapter{

    private SystemNoticeActivity mActivity;

    public NoticeListAdapter(SystemNoticeActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public int getCount() {
        return mActivity.mNotices.size();
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

        // view初始化
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(mActivity).inflate(R.layout.notice_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvNotice = (TextView) convertView.findViewById(R.id.notice_item_tv);
            viewHolder.ivRedPoint = (ImageView) convertView.findViewById(R.id.notice_item_redpoint);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position < mActivity.mNotices.size()) {
            NoticeM notice = mActivity.mNotices.get(position);

            if (notice != null) {
                String title = notice.getTitle();
                String status = notice.getStatus();

                viewHolder.tvNotice.setText(title);

                if ("done".equals(status)) {
                    viewHolder.ivRedPoint.setVisibility(View.GONE);
                    viewHolder.tvNotice.setTextColor(
                            ContextCompat.getColor(mActivity, R.color.grey));
                } else {
                    viewHolder.ivRedPoint.setVisibility(View.VISIBLE);
                    viewHolder.tvNotice.setTextColor(
                            ContextCompat.getColor(mActivity, R.color.common_text));
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvNotice;
        ImageView ivRedPoint;
    }
}
