package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.MeasureModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.utils.Logger;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ScrollView svTop = (ScrollView) findViewById(R.id.measuer_top);
        LinearLayout llQuestionContent = (LinearLayout) findViewById(R.id.measuer_question_content);

        Request request = new Request(this);

        // 获取屏幕宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50; // 50是状态栏高度

        // 题干
        String rich = "把1月和2月的利润代入公式，我们可以得到<img=http://dl.cdn.appublisher.com/yimgs/4/gjkodixmzjizdmz.png></img> ，解得<img=http://dl.cdn.appublisher.com/yimgs/4/wq3mjhjywjhyzzj.png></img>。故1—12月的累积利润为<img=http://dl.cdn.appublisher.com/yimgs/4/dg1y2e3mgm0mdq0.png></img> ，平均利润为<img=http://dl.cdn.appublisher.com/yimgs/4/2m4mzg3zjjiownk.png></img>。因此，本题答案选择C选项。";

        // 通过迭代装饰方式构造解析器。
        IParser parser = new ImageParser(this);

        // 执行解析并返回解析文本段队列。
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据。
        FlowLayout flowLayout = new FlowLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
//            layout.setBackgroundResource(R.drawable.list_item_background);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        int i = 0;
        for (final ParseManager.ParsedSegment segment : segments) {
            if (TextUtils.isEmpty(segment.text)) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView;
                textView = new TextView(this);
                LayoutParams p = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
//                    p.bottomMargin = getResources().getDimensionPixelSize(R.dimen.rich_element_margin);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(getResources().getColor(R.color.setting_text));
                textView.setClickable(false);
                flowLayout.addView(textView);
                textView.setText(segment.text);

                ++i;

            } else if (MatchInfo.MatchType.Image == segment.type) {
                ImageView imgView;
                imgView = new ImageView(this);
                LayoutParams p = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

//					p.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.rich_element_margin);
//					imgView.setMaxHeight(mContext.getResources().getDimensionPixelSize(R.dimen.image_max_height));
                imgView.setLayoutParams(p);

                flowLayout.addView(imgView);

                // 异步加载图片
                request.loadImage(segment.text.toString(), imgView);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(MeasureActivity.this, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        startActivity(intent);
                    }
                });

                // 如果图片太小，扩大两倍
                int imgWidth = MeasureModel.getViewWidth(imgView);
                int imgHeight = MeasureModel.getViewHeight(imgView);

                if (imgHeight < screenHeight / 10) {
                    imgWidth = imgWidth * 2;
                    imgHeight = imgHeight * 2;
                    MeasureModel.setViewWidthAndHeight(imgView, imgWidth, imgHeight);
                }

                ++i;
            }
        }

        llQuestionContent.addView(flowLayout);

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
