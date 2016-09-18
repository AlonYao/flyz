package com.appublisher.quizbank.common.vip.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.quizbank.R;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * 小班：本地图片查看
 */
public class VipGalleryAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mPaths;

    public VipGalleryAdapter(Context context, ArrayList<String> paths) {
        this.mContext = context;
        this.mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths == null ? 0 : mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.vip_gallery_item, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.vip_gallery_item_photoview);
        String path = getPath(position);
        if (path != null && path.length() > 0) {
            ImageManager.displayImageFromFile(path, photoView);
        }
        container.addView(view);
        return view;
    }

    private String getPath(int position) {
        if (mPaths == null || position >= mPaths.size()) return null;
        return mPaths.get(position);
    }

    public void deleteView(ViewPager viewPager, int index) {
        viewPager.setAdapter(null);
        mPaths.remove(index);
        viewPager.setAdapter(this);
    }

}
