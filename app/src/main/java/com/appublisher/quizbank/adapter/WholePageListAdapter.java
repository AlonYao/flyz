package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.wholepage.EntirePaperM;

import java.util.ArrayList;

/**
 * 整卷练习list容器
 */
public class WholePageListAdapter extends BaseAdapter{

    Activity mActivity;
    ArrayList<EntirePaperM> mEntirePapers;

    public WholePageListAdapter(Activity activity, ArrayList<EntirePaperM> entirePapers) {
        mActivity = activity;
        mEntirePapers = entirePapers;
    }

    @Override
    public int getCount() {
        return mEntirePapers.size();
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
                    R.layout.wholepage_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvItem =
                    (TextView) convertView.findViewById(R.id.wholepage_list_item_tv);
            viewHolder.line = convertView.findViewById(R.id.wholepage_list_item_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mEntirePapers != null && mEntirePapers.size() > position) {
            EntirePaperM entirePaper = mEntirePapers.get(position);

            if (entirePaper != null) {
                viewHolder.tvItem.setText(entirePaper.getName());
            }

            if (position == mEntirePapers.size() - 1) {
                viewHolder.line.setVisibility(View.GONE);
            } else {
                viewHolder.line.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvItem;
        View line;
    }
}
