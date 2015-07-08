package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.FilterCourseAreaAdapter;
import com.appublisher.quizbank.adapter.FilterCoursePurchaseAdapter;
import com.appublisher.quizbank.adapter.FilterCourseTagAdapter;
import com.appublisher.quizbank.customui.ExpandableHeightGridView;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.model.netdata.course.FilterAreaM;
import com.appublisher.quizbank.model.netdata.course.FilterAreaResp;
import com.appublisher.quizbank.model.netdata.course.FilterTagM;
import com.appublisher.quizbank.model.netdata.course.FilterTagResp;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 课程中心
 */
public class CourseModel {

    private static PopupWindow mPwTag;
    private static PopupWindow mPwArea;
    private static PopupWindow mPwPurchase;
    private static CourseFragment mCourseFragment;
    private static ArrayList<FilterTagM> mFilterTags;
    private static ArrayList<FilterAreaM> mFilterAreas;
    private static ArrayList<String> mFilterPurchase;
    private static TextView mTvLastTag;
    private static TextView mTvLastArea;
    private static TextView mTvLastPurchase;

    /**
     * 处理课程标签回调
     * @param response 回调数据
     * @param courseFragment 课程中心页面
     */
    public static void dealCourseFilterTagResp(JSONObject response, CourseFragment courseFragment) {
        if (response == null) return;

        // 初始化数据
        mCourseFragment = courseFragment;
        FilterTagResp filterTagResp =
                mCourseFragment.mGson.fromJson(response.toString(), FilterTagResp.class);
        if (filterTagResp == null || filterTagResp.getResponse_code() != 1) return;

        // 课程标签变量赋值
        mFilterTags = filterTagResp.getList();

        // 设置点击事件
        mCourseFragment.mRlTag.setOnClickListener(onClickListener);

        // 获取课程地区列表
        mCourseFragment.mRequest.getCourseFilterArea();
    }

    /**
     * 处理课程地区回调
     * @param response 回调数据
     * @param courseFragment 课程中心页面
     */
    public static void dealCourseFilterAreaResp(JSONObject response,
                                                CourseFragment courseFragment) {
        if (response == null) return;

        // 初始化数据
        mCourseFragment = courseFragment;
        FilterAreaResp filterAreaResp =
                mCourseFragment.mGson.fromJson(response.toString(), FilterAreaResp.class);
        if (filterAreaResp == null || filterAreaResp.getResponse_code() != 1) return;

        // 课程地区变量赋值
        mFilterAreas = filterAreaResp.getList();

        // 设置点击事件
        mCourseFragment.mRlArea.setOnClickListener(onClickListener);

        // 获取课程列表
        mCourseFragment.mRequest.getCourseList(
                mCourseFragment.mCurTagId,
                mCourseFragment.mCurAreaId,
                mCourseFragment.mCurPurchaseId);
    }

    /**
     * 处理课程列表回调
     * @param response 回调数据
     * @param courseFragment 课程中心
     */
    public static void dealCourseListResp(JSONObject response, CourseFragment courseFragment) {
        ProgressBarManager.hideProgressBar();
    }

    /**
     * 初始化Filter课程标签
     */
    private static void initPwTag() {
        // 初始化PopupWindow控件
        @SuppressLint("InflateParams") View view =
                LayoutInflater
                        .from(mCourseFragment.mActivity)
                        .inflate(R.layout.course_popup_tag, null);
        mPwTag = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwTag.setOutsideTouchable(true);
        mPwTag.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        if (mFilterTags == null || mFilterTags.size() == 0) return;

        ExpandableHeightGridView gvTag =
                (ExpandableHeightGridView) view.findViewById(R.id.filter_course_ehgv);
        gvTag.setNumColumns(2);

        FilterCourseTagAdapter filterCourseTagAdapter =
                new FilterCourseTagAdapter(mCourseFragment.mActivity, mFilterTags);
        gvTag.setAdapter(filterCourseTagAdapter);
        gvTag.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 初始化Filter课程购买状态
     */
    public static void initPwPurchase() {
        // 初始化PopupWindow控件
        @SuppressLint("InflateParams") View view =
                LayoutInflater
                        .from(mCourseFragment.mActivity)
                        .inflate(R.layout.course_popup_purchase, null);
        mPwPurchase = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwPurchase.setOutsideTouchable(true);
        mPwPurchase.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        // 构造数据结构
        mFilterPurchase = new ArrayList<>();
        mFilterPurchase.add("全部");
        mFilterPurchase.add("已购");
        mFilterPurchase.add("未购");

        ExpandableHeightGridView gvPurchase =
                (ExpandableHeightGridView) view.findViewById(R.id.course_filter_purchase_ehgv);

        FilterCoursePurchaseAdapter filterCoursePurchaseAdapter =
                new FilterCoursePurchaseAdapter(mCourseFragment.mActivity, mFilterPurchase);
        gvPurchase.setAdapter(filterCoursePurchaseAdapter);
        gvPurchase.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 初始化Filter课程地区
     */
    private static void initPwArea() {
        // 初始化PopupWindow控件
        @SuppressLint("InflateParams") View view =
                LayoutInflater
                        .from(mCourseFragment.mActivity)
                        .inflate(R.layout.course_popup_area, null);
        mPwArea = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwArea.setOutsideTouchable(true);
        mPwArea.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        if (mFilterAreas == null || mFilterAreas.size() == 0) return;

        ExpandableHeightGridView gvArea =
                (ExpandableHeightGridView) view.findViewById(R.id.course_filter_area_ehgv);

        FilterCourseAreaAdapter filterCourseAreaAdapter =
                new FilterCourseAreaAdapter(mCourseFragment.mActivity, mFilterAreas);
        gvArea.setAdapter(filterCourseAreaAdapter);
        gvArea.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 记录当前课程标签
     * @param position 位置
     */
    private static void recordCurTag(int position) {
        if (mFilterTags == null || position >= mFilterTags.size()) return;

        FilterTagM filterTag = mFilterTags.get(position);

        if (filterTag == null) return;

        mCourseFragment.mCurTagId = filterTag.getId();

        // 更新Filter文字
        changeFilterText(mCourseFragment.mTvFilterTag, filterTag.getCategory_name());
    }

    /**
     * 记录当前课程购买状态
     * @param position 位置
     */
    private static void recordCurPurchase(int position) {
        if (mFilterPurchase == null || position >= mFilterPurchase.size()) return;

        String curPurchase = mFilterPurchase.get(position);
        mCourseFragment.mCurPurchaseId = 2;

        if ("未购".equals(curPurchase)) {
            mCourseFragment.mCurPurchaseId = 0;
        } else if ("已购".equals(curPurchase)) {
            mCourseFragment.mCurPurchaseId = 1;
        }

        // 更新Filter文字
        changeFilterText(mCourseFragment.mTvFilterPurchase, curPurchase);
    }

    /**
     * 记录当前课程地区
     * @param position 位置
     */
    private static void recordCurArea(int position) {
        if (mFilterAreas == null || position >= mFilterAreas.size()) return;

        FilterAreaM filterArea = mFilterAreas.get(position);

        if (filterArea == null) return;

        mCourseFragment.mCurAreaId = filterArea.getCode();

        // 更新Filter文字
        changeFilterText(mCourseFragment.mTvFilterArea, filterArea.getName());
    }

    /**
     * 课程中心点击事件
     */
    public static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_tag_rl:
                    // Filter：课程标签
                    if (mPwTag == null) initPwTag();
                    mPwTag.showAsDropDown(mCourseFragment.mRlTag, 0, 2);
                    break;

                case R.id.course_purchase_rl:
                    // Filter：课程购买状态
                    if (mFilterPurchase == null) initPwPurchase();
                    mPwPurchase.showAsDropDown(mCourseFragment.mRlPurchase, 0, 2);
                    break;

                case R.id.course_area_rl:
                    // Filter：课程地区
                    if (mPwArea == null) initPwArea();
                    mPwArea.showAsDropDown(mCourseFragment.mRlArea, 0, 2);
                    break;
            }
        }
    };

    /**
     * 课程中心点击事件
     */
    private static AdapterView.OnItemClickListener onItemClickListener =
        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.filter_course_ehgv:
                        // Popup：课程标签
                        TextView tvTag = (TextView) view.findViewById(R.id.course_filter_gv_item);
                        setPopupTextViewSelect(tvTag);

                        // 前一个view改变
                        if (mTvLastTag != null && mTvLastTag != tvTag)
                            cancelPopupTextViewSelect(mTvLastTag);

                        mTvLastTag = tvTag;

                        // 记录当前的课程标签
                        recordCurTag(position);

                        break;

                    case R.id.course_filter_purchase_ehgv:
                        // Popup：课程购买状态
                        @SuppressLint("CutPasteId")
                        TextView tvPurchase = (TextView) view.findViewById(
                                R.id.course_filter_gv_item);
                        setPopupTextViewSelect(tvPurchase);

                        // 前一个view改变
                        if (mTvLastPurchase != null && mTvLastPurchase != tvPurchase)
                            cancelPopupTextViewSelect(mTvLastPurchase);

                        mTvLastPurchase = tvPurchase;

                        // 记录当前的课程购买状态
                        recordCurPurchase(position);

                        break;

                    case R.id.course_filter_area_ehgv:
                        // Popup：课程地区
                        @SuppressLint("CutPasteId")
                        TextView tvArea = (TextView) view.findViewById(
                                R.id.course_filter_gv_item);
                        setPopupTextViewSelect(tvArea);

                        // 前一个view改变
                        if (mTvLastArea != null && mTvLastArea != tvArea)
                            cancelPopupTextViewSelect(mTvLastArea);

                        mTvLastArea = tvArea;

                        // 记录当前的课程标签
                        recordCurArea(position);

                        break;
                }
            }
        };

    /**
     * 设置控件的选中状态
     * @param textView 控件
     */
    private static void setPopupTextViewSelect(TextView textView) {
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.wholepage_item_all_selected);
    }

    /**
     * 取消控件的选中状态
     * @param textView 控件
     */
    private static void cancelPopupTextViewSelect(TextView textView) {
        textView.setBackgroundResource(R.drawable.wholepage_item_all);
        textView.setTextColor(
                mCourseFragment.mActivity
                        .getResources().getColor(R.color.setting_text));
    }

    /**
     * 更新Filter文字
     * @param textView 控件
     * @param name 名称
     */
    private static void changeFilterText(TextView textView, String name) {
        textView.setText(name);
        textView.setTextColor(mCourseFragment.mActivity.getResources().getColor(R.color.blue));
    }

    /**
     * 接口回调异常处理
     * @param apiName 接口类型
     * @param courseFragment 课程中心
     */
    public static void dealRespError(String apiName, CourseFragment courseFragment) {
        ProgressBarManager.hideProgressBar();

        if ("course_filter_tag".equals(apiName)) {
            // 获取课程标签接口异常
            ProgressBarManager.showProgressBar(courseFragment.mMainView);
            courseFragment.mRequest.getCourseFilterArea();
        }

        if ("course_filter_area".equals(apiName)) {
            // 获取课程地区接口异常
            ProgressBarManager.showProgressBar(courseFragment.mMainView);
            courseFragment.mRequest.getCourseList(
                    courseFragment.mCurTagId,
                    courseFragment.mCurAreaId,
                    courseFragment.mCurPurchaseId);
        }
    }

}
