package com.appublisher.quizbank.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * app引导页容器
 */
public class AppGuideAdapter extends PagerAdapter{

    private List<View> mViewList;

    public AppGuideAdapter(List<View> viewList) {
        this.mViewList = viewList;
    }

    @Override
    public int getCount() {
        return mViewList.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position < mViewList.size()) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < mViewList.size()) {
            container.removeView(mViewList.get(position));
        }
    }
}
