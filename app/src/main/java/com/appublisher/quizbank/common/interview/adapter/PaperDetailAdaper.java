package com.appublisher.quizbank.common.interview.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.netdata.InterviewPaperDetailResp;

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
        final TextView questionContent = (TextView) view.findViewById(R.id.question_content);
        final ImageView analysisIm = (ImageView) view.findViewById(R.id.analysis_im);
        final TextView analysisSwitchTv = (TextView) view.findViewById(R.id.analysis_switch_tv);
        final TextView analysisTv = (TextView) view.findViewById(R.id.analysis_tv);
        final TextView noteTv = (TextView) view.findViewById(R.id.note_tv);
        final TextView sourceTv = (TextView) view.findViewById(R.id.source_tv);
        final TextView keywordsTv = (TextView) view.findViewById(R.id.keywords_tv);

        if (list != null || position < (list.size() - 1)) {
            final InterviewPaperDetailResp.QuestionsBean questionsBean = list.get(position);
            questionContent.setText((position + 1) + "/" + list.size() + "  " + questionsBean.getQuestion());

            SpannableString analysis = new SpannableString("【解析】" + questionsBean.getAnalysis());
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.themecolor));
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(46);
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
            SpannableString source = new SpannableString("【来源】" + questionsBean.getNotes());
            source.setSpan(colorSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(sizeSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            source.setSpan(styleSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sourceTv.setText(source);

            //关键词
            SpannableString keywords = new SpannableString("【关键词】" + questionsBean.getNotes());
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
        super.destroyItem(container, position, object);
    }
}
