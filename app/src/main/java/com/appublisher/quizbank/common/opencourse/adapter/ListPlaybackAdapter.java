package com.appublisher.quizbank.common.opencourse.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appublisher.quizbank.common.opencourse.netdata.OpenCoursePlaybackItem;

import java.util.ArrayList;

/**
 * 公开课模块回放列表Adapter
 */
public class ListPlaybackAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<OpenCoursePlaybackItem> mPlaybacks;

    public ListPlaybackAdapter(Context context, ArrayList<OpenCoursePlaybackItem> playbacks) {
        this.mContext = context;
        this.mPlaybacks = playbacks;
    }

    @Override
    public int getCount() {
//        return mPlaybacks == null ? 0 : mPlaybacks.size();
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
