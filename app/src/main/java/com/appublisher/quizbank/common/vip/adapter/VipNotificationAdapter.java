package com.appublisher.quizbank.common.vip.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipNotificationResp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by jinbao on 2016/8/30.
 */
public class VipNotificationAdapter extends BaseAdapter {

    private Context context;
    private List<VipNotificationResp.NotificationsBean> list;

    public VipNotificationAdapter(Context context, List<VipNotificationResp.NotificationsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.vip_notification_item, null);
            viewHolder.notiText = (TextView) convertView.findViewById(R.id.noti_text);
            viewHolder.avatar = (RoundedImageView) convertView.findViewById(R.id.avatar);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.time_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VipNotificationResp.NotificationsBean notificationsBean = list.get(position);
        int type = notificationsBean.getType();
        String text = "";
        if (type == 1) {
            text = notificationsBean.getSender().getName() + "老师批改了你的作业，去看看";
        } else if (type == 2) {
            text = notificationsBean.getSender().getName() + "同学评论了你的作业，去瞧瞧";
        } else if (type == 4) {
            text = notificationsBean.getSender().getName() + "老师驳回了你的作业，去看看";
        } else if (type == 3) {
            text = notificationsBean.getTitle();
        }
        viewHolder.notiText.setText(text);
        new Request(context).loadImage(notificationsBean.getSender().getAvatar(),viewHolder.avatar);
        viewHolder.timeText.setText(notificationsBean.getSend_time());
        ViewHelper.setAlpha(convertView, 1.0f);
        if (notificationsBean.isIs_read()) {
            ViewHelper.setAlpha(convertView, 0.5f);
        }
        return convertView;
    }

    class ViewHolder {
        TextView notiText;
        RoundedImageView avatar;
        TextView timeText;
    }
}
