package com.appublisher.quizbank.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.Logger;

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
        ImageView iv = (ImageView) findViewById(R.id.measure_iv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ScrollView svTop = (ScrollView) findViewById(R.id.measure_top);
        LinearLayout llQuestionContent = (LinearLayout) findViewById(R.id.measure_question_content);
        LinearLayout llOptionAContainer = (LinearLayout) findViewById(R.id.measure_option_a_container);
        LinearLayout llOptionBContainer = (LinearLayout) findViewById(R.id.measure_option_b_container);
        LinearLayout llOptionCContainer = (LinearLayout) findViewById(R.id.measure_option_c_container);
        LinearLayout llOptionDContainer = (LinearLayout) findViewById(R.id.measure_option_d_container);
        LinearLayout llQuestionMaterial = (LinearLayout) findViewById(R.id.measure_material);
        LinearLayout llQuestionContentM = (LinearLayout) findViewById(R.id.measure_question_content_m);
        LinearLayout llOptionAContainerM = (LinearLayout) findViewById(R.id.measure_option_a_container_m);
        LinearLayout llOptionBContainerM = (LinearLayout) findViewById(R.id.measure_option_b_container_m);
        LinearLayout llOptionCContainerM = (LinearLayout) findViewById(R.id.measure_option_c_container_m);
        LinearLayout llOptionDContainerM = (LinearLayout) findViewById(R.id.measure_option_d_container_m);

        Request request = new Request(this);

        // 获取屏幕宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50; // 50是状态栏高度

        String rich = "把1月和2月的利润代入公式，我们可以得到<img=http://dl.cdn.appublisher.com/yimgs/4/gjkodixmzjizdmz.png></img> ，解得<img=http://dl.cdn.appublisher.com/yimgs/4/wq3mjhjywjhyzzj.png></img>。故1—12月的累积利润为<img=http://dl.cdn.appublisher.com/yimgs/4/dg1y2e3mgm0mdq0.png></img> ，平均利润为<img=http://dl.cdn.appublisher.com/yimgs/4/2m4mzg3zjjiownk.png></img>。因此，本题答案选择C选项。";
        /** 带材料的题目 */
        MeasureModel.addRichTextToContainer(this, llQuestionMaterial, rich);
        MeasureModel.addRichTextToContainer(this, llQuestionContentM, rich);
        MeasureModel.addRichTextToContainer(this, llOptionAContainerM, rich);
        MeasureModel.addRichTextToContainer(this, llOptionBContainerM, rich);
        MeasureModel.addRichTextToContainer(this, llOptionCContainerM, rich);
        MeasureModel.addRichTextToContainer(this, llOptionDContainerM, rich);

        /** 不带材料的题目 */
        // 题干
        MeasureModel.addRichTextToContainer(this, llQuestionContent, rich);

        // 选项
        MeasureModel.addRichTextToContainer(this, llOptionAContainer, rich);
        MeasureModel.addRichTextToContainer(this, llOptionBContainer, rich);
        MeasureModel.addRichTextToContainer(this, llOptionCContainer, rich);
        MeasureModel.addRichTextToContainer(this, llOptionDContainer, rich);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        toolbar.measure(w, h);
        int height = toolbar.getMeasuredHeight();
        int width = toolbar.getMeasuredWidth();

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

                        ViewGroup.LayoutParams layoutParams = svTop.getLayoutParams();
                        layoutParams.height = svTop.getHeight() + dy;
                        svTop.setLayoutParams(layoutParams);

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
