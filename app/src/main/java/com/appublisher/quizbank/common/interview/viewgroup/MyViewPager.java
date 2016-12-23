package com.appublisher.quizbank.common.interview.viewgroup;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Admin on 2016/12/23.
 */

public class MyViewPager extends ViewPager {

    private boolean isScoll;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean setScroll(boolean isScroll){
        this.isScoll = isScroll;
        return isScoll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isScoll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isScoll && super.onInterceptTouchEvent(event);
    }


}
