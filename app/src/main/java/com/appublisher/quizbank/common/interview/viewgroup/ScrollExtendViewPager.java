package com.appublisher.quizbank.common.interview.viewgroup;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ScrollExtendViewPager extends ViewPager {

    private boolean isScoll;

    public ScrollExtendViewPager(Context context) {
        super(context);
    }

    public ScrollExtendViewPager(Context context, AttributeSet attrs) {
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
