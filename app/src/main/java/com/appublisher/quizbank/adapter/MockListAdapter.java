package com.appublisher.quizbank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.mock.MockPreResp;

import java.util.List;

/**
 * Created by jinbao on 2016/11/8.
 */

public class MockListAdapter extends BaseAdapter {

    private Context context;
    private List<MockPreResp.MockListBean> list;

    public MockListAdapter(Context context, List<MockPreResp.MockListBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.mock_list_item, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.mock_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MockPreResp.MockListBean mockListBean = list.get(position);
        if (mockListBean == null) return convertView;
        String text = "总体量" + mockListBean.getQuestion_num() + "题 (" + mockListBean.getPaper_name() + ")";
        viewHolder.textView.setText(text);
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
