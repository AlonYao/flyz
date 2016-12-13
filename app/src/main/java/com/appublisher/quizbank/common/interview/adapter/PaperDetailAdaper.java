package com.appublisher.quizbank.common.interview.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.ScaleImageActivity;
import com.appublisher.lib_basic.volley.Request;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewMaterialDetailActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.richtext.IParser;
import com.appublisher.quizbank.model.richtext.ImageParser;
import com.appublisher.quizbank.model.richtext.MatchInfo;
import com.appublisher.quizbank.model.richtext.ParseManager;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jinbao on 2016/11/17.
 */

public class PaperDetailAdaper extends PagerAdapter {

    private Context context;
    private List<InterviewPaperDetailResp.QuestionsBean> list;

    public PaperDetailAdaper(Context context, List<InterviewPaperDetailResp.QuestionsBean> list) {
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
    public Object instantiateItem(ViewGroup container, int position) {

        final View view = LayoutInflater.from(context).inflate(R.layout.interview_question_item, null);
        final View merterialView = view.findViewById(R.id.meterial_rl);
        final View analysisView = view.findViewById(R.id.analysis_ll);
        final View analysisSwitchView = view.findViewById(R.id.analysis_switch_rl);
        final LinearLayout questionContent = (LinearLayout) view.findViewById(R.id.question_content);
        final ImageView analysisIm = (ImageView) view.findViewById(R.id.analysis_im);
        final TextView analysisSwitchTv = (TextView) view.findViewById(R.id.analysis_switch_tv);
        final TextView analysisTv = (TextView) view.findViewById(R.id.analysis_tv);
        final TextView noteTv = (TextView) view.findViewById(R.id.note_tv);
        final TextView sourceTv = (TextView) view.findViewById(R.id.source_tv);
        final TextView keywordsTv = (TextView) view.findViewById(R.id.keywords_tv);

        if (list != null && position < list.size()) {
            final InterviewPaperDetailResp.QuestionsBean questionsBean = list.get(position);

            //材料
            if (questionsBean.getMaterial() != null && !"".equals(questionsBean.getMaterial())) {
                merterialView.setVisibility(View.VISIBLE);
                merterialView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(context, InterviewMaterialDetailActivity.class);
                        intent.putExtra("material", questionsBean.getMaterial());
                        context.startActivity(intent);

                        // Umeng
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Action", "Material");
                        UmengManager.onEvent(context, "InterviewProblem", map);
                    }
                });
            } else {
                merterialView.setVisibility(View.GONE);
            }

            analysisView.setVisibility(View.GONE);
            if ("notice".equals(questionsBean.getStatus())) {
                analysisSwitchTv.setText("展开提示");
            } else {
                analysisSwitchTv.setText("展开解析");
            }

            analysisSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (analysisView.getVisibility() == View.VISIBLE) {
                        analysisView.setVisibility(View.GONE);
                        analysisIm.setImageResource(R.drawable.interview_arrow_down);
                        if ("notice".equals(questionsBean.getStatus())) {
                            analysisSwitchTv.setText("展开提示");
                            analysisTv.setVisibility(View.GONE);
                        } else {
                            analysisSwitchTv.setText("展开解析");
                            analysisTv.setVisibility(View.VISIBLE);
                        }
                    } else {
                        analysisView.setVisibility(View.VISIBLE);
                        analysisIm.setImageResource(R.drawable.interview_arrow_up);
                        if ("notice".equals(questionsBean.getStatus())) {
                            analysisSwitchTv.setText("收起提示");
                            analysisTv.setVisibility(View.GONE);
                        } else {
                            analysisSwitchTv.setText("收起解析");
                            analysisTv.setVisibility(View.VISIBLE);
                        }
                    }

                    // Umeng
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Action", "Answer");
                    UmengManager.onEvent(context, "InterviewProblem", map);
                }
            });

            String rich = (position + 1) + "/" + list.size() + "  " + questionsBean.getQuestion();
            addRichTextToContainer((Activity) context, questionContent, rich, true);

            SpannableString analysis = new SpannableString("【解析】" + questionsBean.getAnalysis());
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.themecolor));
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Utils.sp2px(context, 15));
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

            //解析
            analysis.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysis.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            analysisTv.setText(analysis);

            //知识点
            SpannableString note = new SpannableString("【知识点】" + questionsBean.getNotes());
            note.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            note.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noteTv.setText(note);

            //来源
            SpannableString source = new SpannableString("【来源】" + questionsBean.getFrom());
            source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sourceTv.setText(source);

            //关键词
            SpannableString keywords = new SpannableString("【关键词】" + questionsBean.getKeywords());
            keywords.setSpan(colorSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(sizeSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywords.setSpan(styleSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            keywordsTv.setText(keywords);

        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
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
