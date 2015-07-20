package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.mock.MockPaperM;

import java.util.ArrayList;

/**
 * 历史模考列表容器
 */
public class MockListAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<MockPaperM> mMockPapers;

    public MockListAdapter(Activity activity, ArrayList<MockPaperM> mockPapers) {
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
     * @param viewHolder ViewHolder
     * @param position position
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mMockPapers == null || position >= mMockPapers.size()) return;

        MockPaperM mockPaper = mMockPapers.get(position);

        if (mockPaper == null) return;

        String status = mockPaper.getStatus();

        viewHolder.tvName.setText(mockPaper.getName());

        if ("done".equals(status)) {
            viewHolder.ivStatus.setImageResource(R.drawable.historymokao_done);
        } else if ("undone".equals(status)) {
            viewHolder.ivStatus.setImageResource(R.drawable.historymokao_undone);
        } else {
            viewHolder.ivStatus.setImageResource(R.color.transparency);
        }
    }

    private class ViewHolder {
        TextView tvName;
        ImageView ivStatus;
    }
}
