package com.appublisher.quizbank.common.interview.viewgroup;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ScrollExtendViewPager extends ViewPager {

    private boolean isScroll;

    public ScrollExtendViewPager(Context context) {
        super(context);
    }

    public ScrollExtendViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean setScroll(boolean isScroll){
        this.isScroll = isScroll;
        return isScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isScroll && super.onInterceptTouchEvent(event);
    }


}
