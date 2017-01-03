package com.appublisher.quizbank.common.measure.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.ImageManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.activity.MeasureSearchActivity;
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
    private boolean mIsAnalysisShow = false;

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
            viewHolder.mTvNote = (TextView) convertView.findViewById(R.id.measure_search_note);
            viewHolder.mTvSource = (TextView) convertView.findViewById(R.id.measure_search_source);
            viewHolder.mTvRightAnswer =
                    (TextView) convertView.findViewById(R.id.measure_search_right_answer);
            viewHolder.mBtnSwitch =
                    (ImageButton) convertView.findViewById(R.id.measure_search_switch);
            viewHolder.mAnalysis =
                    (LinearLayout) convertView.findViewById(R.id.measure_search_analysis);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setContent(position, viewHolder);

        return convertView;
    }

    private void setContent(int position, final ViewHolder viewHolder) {
        if (mList == null || position >= mList.size()) return;
        MeasureSearchResp.SearchItemBean itemBean = mList.get(position);
        if (itemBean == null) return;

        // 题目
        showQuestion(itemBean, viewHolder.mQuestionContainer);

        // 解析
        showAnalysis(itemBean, viewHolder);

        // 开关
        viewHolder.mBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsAnalysisShow) {
                    viewHolder.mAnalysis.setVisibility(View.GONE);
                    mIsAnalysisShow = false;
                    viewHolder.mBtnSwitch.setImageResource(R.drawable.measure_search_expand);
                } else {
                    viewHolder.mAnalysis.setVisibility(View.VISIBLE);
                    mIsAnalysisShow = true;
                    viewHolder.mBtnSwitch.setImageResource(R.drawable.measure_search_packup);
                }
            }
        });
    }

    /**
     * 显示解析部分
     * @param itemBean 数据
     * @param viewHolder ViewHolder
     */
    private void showAnalysis(MeasureSearchResp.SearchItemBean itemBean, ViewHolder viewHolder) {
        viewHolder.mTvRightAnswer.setText(itemBean.getAnswer());

        viewHolder.mAnalysisContainer.removeAllViews();
        String text = "【解析】" + itemBean.getAnalysis();
        addRichTextToContainer(viewHolder.mAnalysisContainer, text, true);

        String note = "知识点：" + itemBean.getNote_name();
        viewHolder.mTvNote.setText(note);
        String source = "来源：" + itemBean.getSource();
        viewHolder.mTvSource.setText(source);
    }

    /**
     * 显示题目部分
     * @param itemBean data
     * @param questionContainer view
     */
    private void showQuestion(MeasureSearchResp.SearchItemBean itemBean,
                              LinearLayout questionContainer) {
        String question = itemBean.getQuestion();
        String a = itemBean.getOption_a();
        String b = itemBean.getOption_b();
        String c = itemBean.getOption_c();
        String d = itemBean.getOption_d();

        String text = question + "\n\n"
                + "A." + a + "\n"
                + "B." + b + "\n"
                + "C." + c + "\n"
                + "D." + d;

        questionContainer.removeAllViews();
        addRichTextToContainer(questionContainer, text, true);
    }

    /**
     * 获取关键词
     * @return String
     */
    private String getKeywords() {
        if (mContext instanceof MeasureSearchActivity) {
            return ((MeasureSearchActivity) mContext).mModel.getCurKeywords();
        }
        return null;
    }

    /**
     * 格式化
     * @param text 源数据
     * @param textView TextView
     */
    private void formatText(String text, TextView textView) {
        if (text == null) return;
        String keywords = getKeywords();
        if (keywords != null) {
            SpannableString ss = new SpannableString(text);
            int start = text.indexOf(keywords, 0);
            while (start != -1) {
                ss.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start,
                        start + keywords.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = start + keywords.length();
                start = text.indexOf(keywords, start);
            }

            // 预留字段特殊处理：【解析】
            if (text.contains("【解析】")) {
                start = text.indexOf("【解析】", 0);
                ss.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.themecolor)),
                    start,
                    4,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            textView.setText(ss);
        } else {
            textView.setText(text);
        }
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
                AbsListView.LayoutParams.WRAP_CONTENT,
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

                // 标记关键词
                formatText(segment.text.toString(), textView);

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

    private static class ViewHolder {
        LinearLayout mQuestionContainer;
        TextView mTvRightAnswer;
        LinearLayout mAnalysisContainer;
        TextView mTvNote;
        TextView mTvSource;
        ImageButton mBtnSwitch;
        LinearLayout mAnalysis;
    }

}
