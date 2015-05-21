package com.appublisher.quizbank.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.AppGuideAdapter;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class AppGuideActivity extends Activity {

    private LinearLayout mLlDots;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_guide);

        // view初始化
        mViewPager = (ViewPager) findViewById(R.id.appguide_viewpager);
        mLlDots = (LinearLayout) findViewById(R.id.appguide_dots);

        initPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("AppGuideActivity");
        MobclickAgent.onResume(this);

        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng
        MobclickAgent.onPageEnd("AppGuideActivity");
        MobclickAgent.onPause(this);

        TCAgent.onPause(this);
    }

    private void initPager() {
        List<View> viewList = new ArrayList<>();

        int[] images = new int[] { R.drawable.app_guide_1, R.drawable.app_guide_2,
                R.drawable.app_guide_3};

        for (int image : images) {
            viewList.add(initView(image));
        }

        initDots(images.length);

        mViewPager.setAdapter(new AppGuideAdapter(viewList));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mLlDots.getChildCount(); i++) {
                    if (i == position) {
                        mLlDots.getChildAt(i).setSelected(true);
                    } else {
                        mLlDots.getChildAt(i).setSelected(false);
                    }
                }
                if(position == mLlDots.getChildCount()){
                    finish();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void initDots(int length) {
        for (int j = 0; j < length; j++) {
            mLlDots.addView(initDot());
        }
        mLlDots.getChildAt(0).setSelected(true);
    }

    private View initDot() {
        return LayoutInflater.from(getApplicationContext()).inflate(R.layout.app_guide_dot, null);
    }

    private View initView(int image) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.app_guide_item, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.appguide_item_img);
        imageView.setImageResource(image);
        return view;
    }
}
