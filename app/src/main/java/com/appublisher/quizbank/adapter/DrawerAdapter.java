package com.appublisher.quizbank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // view初始化
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_item, null);

            viewHolder = new ViewHolder();
            viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.drawer_item_iv);
            viewHolder.tvItem = (TextView) convertView.findViewById(R.id.drawer_item_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivItem.setImageResource(mItemImgs[position]);
        viewHolder.tvItem.setText(mItemNames[position]);

        return convertView;
    }

    private class ViewHolder {
        ImageView ivItem;
        TextView tvItem;
    }
}
