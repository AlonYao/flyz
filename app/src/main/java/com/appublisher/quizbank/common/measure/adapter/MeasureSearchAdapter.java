package com.appublisher.quizbank.common.measure.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.activity.MeasureSearchActivity;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;

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
            viewHolder.mTvContent =
                    (TextView) convertView.findViewById(R.id.measure_search_item_content);
            viewHolder.mTvSource =
                    (TextView) convertView.findViewById(R.id.measure_search_item_source);
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

        // 显示内容
        if (isContainsKeywords(itemBean.getQuestion())) {
            formatText(itemBean.getQuestion(), viewHolder.mTvContent);
        } else if (isContainsKeywords(itemBean.getOption_a())) {
            formatText(itemBean.getOption_a(), viewHolder.mTvContent);
        } else if (isContainsKeywords(itemBean.getOption_b())) {
            formatText(itemBean.getOption_b(), viewHolder.mTvContent);
        } else if (isContainsKeywords(itemBean.getOption_c())) {
            formatText(itemBean.getOption_c(), viewHolder.mTvContent);
        } else if (isContainsKeywords(itemBean.getOption_d())) {
            formatText(itemBean.getOption_d(), viewHolder.mTvContent);
        } else if (isContainsKeywords(itemBean.getMaterial())) {
            formatText(itemBean.getMaterial(), viewHolder.mTvContent);
        }

        // 显示来源
        String source = "来源：" + itemBean.getSource();
        viewHolder.mTvSource.setText(source);
    }

    private boolean isContainsKeywords(String text) {
        String keywords = getKeywords();
        return !(keywords == null || text == null) && text.contains(keywords);
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

        // 去掉图片
        int start = text.indexOf("<img=");
        int end;
        String imgItem;
        while (start != -1) {
            end = text.indexOf("</img>");
            imgItem = text.substring(start, end + 6);
            text = text.replace(imgItem, "");

            start = text.indexOf("<img=");
        }

        String keywords = getKeywords();
        if (keywords != null) {
            SpannableString ss = new SpannableString(text);
            start = text.indexOf(keywords, 0);
            while (start != -1) {
                ss.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start,
                        start + keywords.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = start + keywords.length();
                start = text.indexOf(keywords, start);
            }

            // 预留字段特殊处理：材料：
            if (text.contains("材料：")) {
                ss.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.themecolor)),
                    0,
                    2,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            textView.setText(ss);
        } else {
            textView.setText(text);
        }
    }

    private static class ViewHolder {
        TextView mTvContent;
        TextView mTvSource;
    }

}
