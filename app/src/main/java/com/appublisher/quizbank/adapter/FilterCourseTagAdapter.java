package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.netdata.course.FilterTagM;

import java.util.ArrayList;

/**
 * Filter课程标签Adapter
 */
public class FilterCourseTagAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<FilterTagM> mFilterTags;

    public FilterCourseTagAdapter(Activity activity, ArrayList<FilterTagM> filterTags) {
        mActivity = activity;
        mFilterTags = filterTags;
    }

    @Override
    public int getCount() {
        return mFilterTags == null ? 0 : mFilterTags.size();
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
                    R.layout.course_filter_gv_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvItem =
                    (TextView) convertView.findViewById(R.id.course_filter_gv_item);

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
     * @param position 位置
     */
    private void setContent(ViewHolder viewHolder, int position) {
        if (mFilterTags == null || position >= mFilterTags.size()) return;

        FilterTagM filterTag = mFilterTags.get(position);

        if (filterTag == null) return;

        viewHolder.tvItem.setText(filterTag.getCategory_name());
    }

    class ViewHolder {
        TextView tvItem;
    }
}
