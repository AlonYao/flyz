package com.appublisher.quizbank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appublisher.lib_basic.AppDownload;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_course.CourseWebViewActivity;
import com.appublisher.lib_login.model.business.LoginModel;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.netdata.CarouselM;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jinbao on 2016/11/21.
 */

public class CarouselAdapter extends PagerAdapter {
    private Context context;
    private List<CarouselM> list;

    public CarouselAdapter(Context context, List<CarouselM> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_viewpager_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        ImageManager.displayImage(list.get(position).getImg_url(), imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((list.size() - 1) < position) return;
                CarouselM carouselM = list.get(position);

                String targetType = carouselM.getTarget_type();
                String targetContent = carouselM.getTarget_content();
                if (targetContent == "" || targetType == "") return;

                if ("url".equals(targetType)) {
                    final Intent intent = new Intent(context, CourseWebViewActivity.class);
                    intent.putExtra("url", targetContent);
                    context.startActivity(intent);
                } else if ("app".equals(targetType)) {
                    if (targetContent.contains("market@")) {
                        // 跳转到市场
                        CommonModel.skipToGrade(
                                (Activity) context, targetContent.replace("market@", ""));

                    } else if (targetContent.contains("courselist")) {
                        // 跳转到课程中心模块
                        if ((Activity) context instanceof MainActivity)
                            ((MainActivity) context).courseRadioButton.setChecked(true);

                    } else if (targetContent.contains("zhiboke@")) {
                        // 跳转至课程详情页面
                        Intent intent = new Intent(context, CourseWebViewActivity.class);
                        intent.putExtra("url", targetContent.replace("zhiboke@", "")
                                + "&user_id=" + LoginModel.getUserId()
                                + "&user_token=" + LoginModel.getUserToken());
                        intent.putExtra("bar_title", "快讯");
                        intent.putExtra("from", "course");
                        context.startActivity(intent);
                    }

                } else if ("apk".equals(targetType)) {
                    AppDownload.downloadApk(context, targetContent);
                }

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("CarouselFigure", targetContent);
                UmengManager.onEvent(context, "Home", map);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
