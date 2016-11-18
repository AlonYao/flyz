package com.appublisher.quizbank.common.interview.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

public class InterviewMaterialDetailActivity extends BaseActivity {

    private String materialString;
    private LinearLayout materialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_material_detail);

        setToolBar(this);

        materialString = getIntent().getStringExtra("material");

        materialView = (LinearLayout) findViewById(R.id.interview_material);

        if (materialString != null && !"".equals(materialString)) {
            addRichTextToContainer(this, materialView, materialString, true);
        }
    }

    /**
     * 动态添加富文本
     *
     * @param activity  Activity
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    public static void addRichTextToContainer(final Activity activity,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(activity);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(activity);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        flowLayout.setLayoutParams(params);
        flowLayout.setGravity(Gravity.CENTER_VERTICAL);

        for (final ParseManager.ParsedSegment segment : segments) {
            if (segment.text == null || segment.text.length() == 0) {
                continue;
            }

            if (MatchInfo.MatchType.None == segment.type) {
                TextView textView = new TextView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(activity.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(activity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度

                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap data = imageContainer.getBitmap();

                        if (data == null) return;

                        // 对小于指定尺寸的图片进行放大(2倍)
                        int width = data.getWidth();
                        int height = data.getHeight();
                        if (height < minHeight) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(2.0f, 2.0f);
                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
                        }

                        imgView.setImageBitmap(data);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                };

                request.loadImage(segment.text.toString(), imageListener);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, ScaleImageActivity.class);
                        intent.putExtra("imgUrl", segment.text.toString());
                        activity.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }
}
