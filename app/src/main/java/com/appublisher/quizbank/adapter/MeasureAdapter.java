package com.appublisher.quizbank.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.model.MeasureModel;

/**
 * 做题模块
 */
public class MeasureAdapter extends PagerAdapter{

    private MeasureActivity mActivity;
    private int mLastY;

    public MeasureAdapter(MeasureActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return 10;
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
        boolean hasMaterial = true;
        View view = null;
        if (hasMaterial) {
            view = LayoutInflater.from(mActivity).inflate(
                    R.layout.measure_item_hasmaterial, container, false);

            ImageView ivPull = (ImageView) view.findViewById(R.id.measure_iv);
            LinearLayout llMaterial = (LinearLayout) view.findViewById(R.id.measure_material);
            LinearLayout llOptionAContainer = (LinearLayout) view.findViewById(
                    R.id.measure_option_a_container_m);
            LinearLayout llOptionBContainer = (LinearLayout) view.findViewById(
                    R.id.measure_option_b_container_m);
            LinearLayout llOptionCContainer = (LinearLayout) view.findViewById(
                    R.id.measure_option_c_container_m);
            LinearLayout llOptionDContainer = (LinearLayout) view.findViewById(
                    R.id.measure_option_d_container_m);
            final ScrollView svTop = (ScrollView) view.findViewById(R.id.measure_top);

            ivPull.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();

                    switch (action) {

                        case MotionEvent.ACTION_DOWN:
                            mLastY = (int) event.getRawY();
                            break;

                        /**
                         * layout(l,t,r,b)
                         * l  Left position, relative to parent
                         t  Top position, relative to parent
                         r  Right position, relative to parent
                         b  Bottom position, relative to parent
                         * */
                        case MotionEvent.ACTION_MOVE:
                            int dy =(int)event.getRawY() - mLastY;

                            int top = v.getTop() + dy;
                            int bottom = v.getBottom() + dy;

                            if(top < 0){
                                top = 0;
                                bottom = top + v.getHeight();
                            }
                            if(bottom > mActivity.mScreenHeight){
                                bottom = mActivity.mScreenHeight;
                                top = bottom - v.getHeight();
                            }
                            v.layout(v.getLeft(), top, v.getRight(), bottom);

                            ViewGroup.LayoutParams layoutParams = svTop.getLayoutParams();
                            layoutParams.height = svTop.getHeight() + dy;
                            svTop.setLayoutParams(layoutParams);

                            mLastY = (int) event.getRawY();

                            break;

                        case MotionEvent.ACTION_UP:
                            break;
                    }

                    return false;
                }
            });

            String rich = "把1月和2月的利润代入公式，我们可以得到<img=http://dl.cdn.appublisher.com/yimgs/4/gjkodixmzjizdmz.png></img> ，解得<img=http://dl.cdn.appublisher.com/yimgs/4/wq3mjhjywjhyzzj.png></img>。故1—12月的累积利润为<img=http://dl.cdn.appublisher.com/yimgs/4/dg1y2e3mgm0mdq0.png></img> ，平均利润为<img=http://dl.cdn.appublisher.com/yimgs/4/2m4mzg3zjjiownk.png></img>。因此，本题答案选择C选项。";

            MeasureModel.addRichTextToContainer(mActivity, llMaterial, rich);
            MeasureModel.addRichTextToContainer(mActivity, llOptionAContainer, rich);
            MeasureModel.addRichTextToContainer(mActivity, llOptionBContainer, rich);
            MeasureModel.addRichTextToContainer(mActivity, llOptionCContainer, rich);
            MeasureModel.addRichTextToContainer(mActivity, llOptionDContainer, rich);
        } else {

        }

        container.addView(view);

        return view;
    }
}
