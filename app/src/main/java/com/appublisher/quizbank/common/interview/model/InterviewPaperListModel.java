package com.appublisher.quizbank.common.interview.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperListActivity;
import com.appublisher.quizbank.common.interview.adapter.FilterAreaAdapter;
import com.appublisher.quizbank.common.interview.adapter.FilterYearAdapter;
import com.appublisher.quizbank.common.interview.netdata.InterviewFilterResp;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterCategoryAdapter;

import org.json.JSONObject;

/**
 * Created by jinbao on 2016/11/16.
 */

public class InterviewPaperListModel {
    private PopupWindow yearPop;
    private PopupWindow areaPop;
    private TextView curYearSelectedTv;
    private TextView curAreaSelectedTv;
    private InterviewFilterResp mInterviewFilterResp;

    public void dealFilterResp(JSONObject response) {
        InterviewFilterResp interviewFilterResp = GsonManager.getModel(response, InterviewFilterResp.class);
        if (interviewFilterResp.getResponse_code() == 1) {
            mInterviewFilterResp = interviewFilterResp;
        }
    }


    public void showYearPop(InterviewPaperListActivity activity) {

        if (yearPop == null) initYearPop(activity);

        if (yearPop.isShowing()) {
            yearPop.dismiss();
        } else {
            yearPop.showAsDropDown(activity.yearFilterView, 0, 1);
        }

    }

    public void initYearPop(final InterviewPaperListActivity activity) {
        if (mInterviewFilterResp == null) return;
        View categoryView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
        GridView categoryGridView = (GridView) categoryView.findViewById(R.id.gridview);
        FilterYearAdapter categoryAdapter = new FilterYearAdapter(activity, mInterviewFilterResp.getYear());
        categoryGridView.setAdapter(categoryAdapter);
        yearPop = new PopupWindow(categoryView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        yearPop.setOutsideTouchable(true);
        yearPop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));

        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                itemSelected(textView, activity);
                activity.year = mInterviewFilterResp.getYear().get(position);
                activity.yearFilterTv.setText(textView.getText());
                activity.yearFilterTv.setTextColor(activity.getResources().getColor(R.color.themecolor));
                if (curYearSelectedTv != null && curYearSelectedTv != textView) {
                    itemCancel(curYearSelectedTv, activity);
                }
                curYearSelectedTv = textView;
            }
        });
        TextView categoryCancle = (TextView) categoryView.findViewById(R.id.vip_filter_cancel);
        TextView categoryConfirm = (TextView) categoryView.findViewById(R.id.vip_filter_confirm);
        categoryCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearPop.isShowing())
                    yearPop.dismiss();
            }
        });
        categoryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearPop.isShowing()) {
                    yearPop.dismiss();
                    activity.getData();
                }

            }
        });

    }

    public void showAreaPop(InterviewPaperListActivity activity) {
        if (areaPop == null) initAreaPop(activity);

        if (areaPop.isShowing()) {
            areaPop.dismiss();
        } else {
            areaPop.showAsDropDown(activity.yearFilterView, 0, 1);
        }

    }

    public void initAreaPop(final InterviewPaperListActivity activity) {
        if (mInterviewFilterResp == null) return;
        View categoryView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
        GridView categoryGridView = (GridView) categoryView.findViewById(R.id.gridview);
        FilterAreaAdapter categoryAdapter = new FilterAreaAdapter(activity, mInterviewFilterResp.getArea());
        categoryGridView.setAdapter(categoryAdapter);
        areaPop = new PopupWindow(categoryView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        areaPop.setOutsideTouchable(true);
        areaPop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));

        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                itemSelected(textView, activity);
                activity.area_id = mInterviewFilterResp.getArea().get(position).getArea_id();
                activity.areaFilterTv.setText(textView.getText());
                activity.areaFilterTv.setTextColor(activity.getResources().getColor(R.color.themecolor));
                if (curAreaSelectedTv != null && curAreaSelectedTv != textView) {
                    itemCancel(curAreaSelectedTv, activity);
                }
                curAreaSelectedTv = textView;
            }
        });
        TextView categoryCancle = (TextView) categoryView.findViewById(R.id.vip_filter_cancel);
        TextView categoryConfirm = (TextView) categoryView.findViewById(R.id.vip_filter_confirm);
        categoryCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaPop.isShowing())
                    areaPop.dismiss();
            }
        });
        categoryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaPop.isShowing()) {
                    areaPop.dismiss();
                    activity.getData();
                }

            }
        });

    }

    /**
     * 取消选中item
     *
     * @param textView
     * @param context
     */
    public void itemCancel(TextView textView, Context context) {
        textView.setTextColor(context.getResources().getColor(R.color.common_text));
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(
                    context.getResources().getColor(R.color.vip_filter_item_unselect));
        }
    }


    /**
     * 选中item
     *
     * @param textView
     * @param context
     */
    public void itemSelected(TextView textView, Context context) {
        textView.setTextColor(Color.WHITE);
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(context.getResources().getColor(R.color.apptheme));
        }
    }
}
