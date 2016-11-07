package com.appublisher.quizbank.common.measure;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.netdata.MeasureAutoResp;
import com.appublisher.quizbank.common.measure.network.MeasureRequest;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 做题模块：管理类
 */

public class MeasureModel implements RequestCallback{

    private Context mContext;
    private MeasureRequest mRequest;
    public String mPaperType;

    MeasureModel(Context context) {
        mContext = context;
        mRequest = new MeasureRequest(context, this);
    }

    public void getData() {
        if ("auto".equals(mPaperType)) {
            mRequest.getAutoTraining();
        }
    }

    private void hideLoading() {
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    private void dealAutoTrainingResp(JSONObject response) {
        MeasureAutoResp resp = GsonManager.getModel(response, MeasureAutoResp.class);
        if (resp == null || resp.getResponse_code() != 1) return;
        if (!(mContext instanceof MeasureActivity)) return;
        ((MeasureActivity) mContext).showViewPager(resp.getQuestions());
    }

    /**
     * 动态添加富文本
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    @SuppressWarnings("deprecation")
    public static void addRichTextToContainer(final Context context,
                                              LinearLayout container,
                                              String rich,
                                              boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

//        Request request = new Request(activity);

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(context);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(context);
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
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(context.getResources().getColor(R.color.common_text));
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(context);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                // 异步加载图片
//                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
//                final float minHeight = (float) ((dm.heightPixels - 50) * 0.05); // 50是状态栏高度
//
//                ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        Bitmap data = imageContainer.getBitmap();
//
//                        if (data == null) return;
//
//                        // 对小于指定尺寸的图片进行放大(2倍)
//                        int width = data.getWidth();
//                        int height = data.getHeight();
//                        if (height < minHeight) {
//                            Matrix matrix = new Matrix();
//                            matrix.postScale(2.0f, 2.0f);
//                            data = Bitmap.createBitmap(data, 0, 0, width, height, matrix, true);
//                        }
//
//                        imgView.setImageBitmap(data);
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                };
//
//                request.loadImage(segment.text.toString(), imageListener);

                ImageManager.displayImage(segment.text.toString(), imgView);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, ScaleImageActivity.class);
                        intent.putExtra(ScaleImageActivity.INTENT_IMGURL, segment.text.toString());
                        context.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (MeasureRequest.AUTO_TRAINING.equals(apiName)) {
            dealAutoTrainingResp(response);
        }
        hideLoading();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

}
