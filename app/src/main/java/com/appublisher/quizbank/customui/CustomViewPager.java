package com.appublisher.quizbank.customui;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.appublisher.lib_basic.Logger;

/**
 * Created by jinbao on 2016/9/20.
 */

public class CustomViewPager extends ViewPager {

    private boolean SLIDE_TO_LEFT = true;
    private boolean SLIDE_TO_RIGHT = true;
    private boolean IS_SCROLL = true;
    float beginX = 0;
    float beginY = 0;
    float moveX;
    float moveY;

    public void setSLIDE_TO_LEFT(boolean SLIDE_TO_LEFT) {
        this.SLIDE_TO_LEFT = SLIDE_TO_LEFT;
    }

    public void setSLIDE_TO_RIGHT(boolean SLIDE_TO_RIGHT) {
        this.SLIDE_TO_RIGHT = SLIDE_TO_RIGHT;
    }

    public void setIS_SCROLL(boolean IS_SCROLL) {
        this.IS_SCROLL = IS_SCROLL;
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginX = ev.getX();
                beginY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX() - beginX;
                moveY = ev.getY() - beginY;

                if (Math.abs(moveY) > Math.abs(moveX)) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    if (moveX < 0 && !SLIDE_TO_RIGHT) {
                        return false;
                    } else if (moveX > 0 && !SLIDE_TO_LEFT) {
                        return false;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

}
