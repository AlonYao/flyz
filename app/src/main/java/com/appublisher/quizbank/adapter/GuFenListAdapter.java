package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.mock.GufenM;
import com.appublisher.quizbank.model.netdata.mock.MockGufenResp;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史模考列表容器
 */
public class GuFenListAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<GufenM.PaperListBean> mMockPapers;

    public GuFenListAdapter(Activity activity, List<GufenM.PaperListBean> mockPapers) {
        mActivity = activity;
        mMockPapers = mockPapers;
    }

    @Override
    public int getCount() {
        return mMockPapers == null ? 0 : mMockPapers.size();
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
                    R.layout.mock_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.mock_item_name);
            viewHolder.ivStatus =
                    (ImageView) convertView.findViewById(R.id.mock_item_status);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(viewHolder, position);

        return convertView;
    }

    /**
     * 设置内容
     *
     * @param viewHolder ViewHolder
     * @param position   position
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mMockPapers == null || position >= mMockPapers.size()) return;

        GufenM.PaperListBean mockPaper = mMockPapers.get(position);

        if (mockPaper == null) return;

        viewHolder.tvName.setText(mockPaper.getName());
    }

    private class ViewHolder {
        TextView tvName;
        ImageView ivStatus;
    }
}
