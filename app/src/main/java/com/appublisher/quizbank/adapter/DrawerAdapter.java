package com.appublisher.quizbank.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.model.db.GlobalSetting;

/**
 * 侧边栏按钮容器
 */
public class DrawerAdapter extends BaseAdapter{

    private Context mContext;

    private int[] mItemNames = new int[] {
            R.string.drawer_homepage,
            R.string.drawer_wholepage,
            R.string.drawer_wrong,
            R.string.drawer_store,
            R.string.drawer_record,
            R.string.drawer_setting,
    };

    private int[] mItemImgs = new int[] {
            R.drawable.drawer_homepage,
            R.drawable.drawer_wholepage,
            R.drawable.drawer_wrong,
            R.drawable.drawer_store,
            R.drawable.drawer_record,
            R.drawable.drawer_setting,
    };

    public DrawerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItemNames.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_item, null);

            viewHolder = new ViewHolder();
            viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.drawer_item_iv);
            viewHolder.tvItem = (TextView) convertView.findViewById(R.id.drawer_item_tv);
            viewHolder.ivRedPoint = (ImageView) convertView.findViewById(R.id.drawer_item_redpoint);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivItem.setImageResource(mItemImgs[position]);
        viewHolder.tvItem.setText(mItemNames[position]);

        // 判断设置按钮是否显示红点
        GlobalSetting globalSetting = GlobalSettingDAO.findById();

        int a = Globals.last_notice_id;

        if (position == 5
                && globalSetting != null
                && globalSetting.latest_notify != Globals.last_notice_id
                && Globals.last_notice_id != 0) {
            viewHolder.ivRedPoint.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivRedPoint.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView ivItem;
        TextView tvItem;
        ImageView ivRedPoint;
    }
}
