package com.appublisher.quizbank.common.vip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

/**
 * Created by jinbao on 2016/9/6.
 */
public class VipExerciseFilterAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;


    public VipExerciseFilterAdapter(Context context,List<String> list){
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
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.vip_filter_item,null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(list.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView textView;
    }
}
