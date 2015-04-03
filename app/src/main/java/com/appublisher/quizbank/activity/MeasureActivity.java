package com.appublisher.quizbank.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;

/**
 * 做题
 */
public class MeasureActivity extends ActionBarActivity {

    private int screenWidth;
    private int screenHeight;
    private int lastX;
    private int lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // ToolBar
        CommonModel.setToolBar(this);

        // View 初始化
        ImageView iv = (ImageView) findViewById(R.id.measuer_iv);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    /**
                     * layout(l,t,r,b)
                     * l  Left position, relative to parent
                     t  Top position, relative to parent
                     r  Right position, relative to parent
                     b  Bottom position, relative to parent
                     * */
                    case MotionEvent.ACTION_MOVE:
                        int dx =(int)event.getRawX() - lastX;
                        int dy =(int)event.getRawY() - lastY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if(left < 0){
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if(right > screenWidth){
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if(top < 0){
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if(bottom > screenHeight){
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
