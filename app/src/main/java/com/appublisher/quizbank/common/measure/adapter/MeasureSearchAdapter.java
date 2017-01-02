package com.appublisher.quizbank.common.measure.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;
import com.nostra13.universalimageloader.core.assist.FailReason;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜题模块
 */

public class MeasureSearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<MeasureSearchResp.SearchItemBean> mList;

    public MeasureSearchAdapter(Context context, List<MeasureSearchResp.SearchItemBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.measure_search_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mQuestionContainer =
                    (LinearLayout) convertView.findViewById(R.id.measure_search_question_container);
            viewHolder.mAnalysisContainer =
                    (LinearLayout) convertView.findViewById(R.id.measure_search_analysis_container);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(position, viewHolder);

        return convertView;
    }

    private void setContent(int position, ViewHolder viewHolder) {
        if (mList == null || position >= mList.size()) return;
        MeasureSearchResp.SearchItemBean itemBean = mList.get(position);
        if (itemBean == null) return;

        // 题目
        showQuestion(itemBean, viewHolder.mQuestionContainer);

        // 解析
        showAnalysis(itemBean, viewHolder);
    }

    private void showAnalysis(MeasureSearchResp.SearchItemBean itemBean, ViewHolder viewHolder) {

    }

    private void showQuestion(MeasureSearchResp.SearchItemBean itemBean,
                              LinearLayout questionContainer) {
        if (itemBean == null) return;
        String question = itemBean.getQuestion();
        String a = itemBean.getOption_a();
        String b = itemBean.getOption_b();
        String c = itemBean.getOption_c();
        String d = itemBean.getOption_d();

        String text = question + "\n\n"
                + "A." + a + "\n\n"
                + "B." + b + "\n\n"
                + "C." + c + "\n\n"
                + "D." + d;

        addRichTextToContainer(questionContainer, text, true);
    }

    private static class ViewHolder {
        LinearLayout mQuestionContainer;
        LinearLayout mAnalysisContainer;
    }

    /**
     * 动态添加富文本
     * @param container 富文本控件容器
     * @param rich      富文本
     */
    @SuppressWarnings("deprecation")
    private void addRichTextToContainer(LinearLayout container,
                                        String rich,
                                        boolean textClick) {
        if (rich == null || rich.length() <= 0) return;

        // 通过迭代装饰方式构造解析器
        IParser parser = new ImageParser(mContext);

        // 执行解析并返回解析文本段队列
        ParseManager manager = new ParseManager();
        ArrayList<ParseManager.ParsedSegment> segments = manager.parse(parser, rich);

        // 用 Holder 模式更新列表数据
        FlowLayout flowLayout = new FlowLayout(mContext);
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
                TextView textView = new TextView(mContext);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(p);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                textView.setTextColor(mContext.getResources().getColor(R.color.common_text));
                textView.setLineSpacing(0, 1.4f);
                textView.setText(segment.text);
                flowLayout.addView(textView);

                // text长按复制
                if (textClick) {
                    CommonModel.setTextLongClickCopy(textView);
                }

            } else if (MatchInfo.MatchType.Image == segment.type) {
                final ImageView imgView = new ImageView(mContext);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(p);

                imgView.setImageResource(R.drawable.measure_loading_img);

                flowLayout.addView(imgView);

                ImageManager.displayImage(segment.text.toString(), imgView,
                        new ImageManager.LoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                // Empty
                            }

                            @Override
                            public void onLoadingFailed(String imageUri,
                                                        View view,
                                                        FailReason failReason) {
                                // Empty
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view,
                                                          Bitmap loadedImage) {
                                if (loadedImage == null) return;

                                // 对小于指定尺寸的图片进行放大(2.5倍)
                                int width = loadedImage.getWidth();
                                int height = loadedImage.getHeight();
                                if (height < Utils.sp2px(mContext, 17) * 2) {
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(2.5f, 2.5f);
                                    loadedImage = Bitmap.createBitmap(
                                            loadedImage, 0, 0, width, height, matrix, true);
                                }

                                imgView.setImageBitmap(loadedImage);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                // Empty
                            }
                        });

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, ScaleImageActivity.class);
                        intent.putExtra(ScaleImageActivity.INTENT_IMGURL, segment.text.toString());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        container.addView(flowLayout);
    }
}
