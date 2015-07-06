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
import com.appublisher.quizbank.adapter.FilterCourseTagAdapter;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.model.netdata.course.FilterAreaM;
import com.appublisher.quizbank.model.netdata.course.FilterAreaResp;
import com.appublisher.quizbank.model.netdata.course.FilterTagM;
import com.appublisher.quizbank.model.netdata.course.FilterTagResp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 课程中心
 */
public class CourseModel {

    private static PopupWindow mPwTag;
    private static PopupWindow mPwArea;
    private static CourseFragment mCourseFragment;
    private static ArrayList<FilterTagM> mFilterTags;
    private static ArrayList<FilterAreaM> mFilterAreas;
    private static TextView mTvLastTag;
    private static int mCurTagId;

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

        GridView gvTag = (GridView) view.findViewById(R.id.filter_coursetag_gv);
        gvTag.setNumColumns(2);

        FilterCourseTagAdapter filterCourseTagAdapter =
                new FilterCourseTagAdapter(mCourseFragment.mActivity, mFilterTags);
        gvTag.setAdapter(filterCourseTagAdapter);

        gvTag.setOnItemClickListener(onItemClickListener);

        mPwTag.update();
    }

    /**
     * 初始化Filter课程地区
     */
    private static void initPwArea() {
        // 初始化PopupWindow控件
        @SuppressLint("InflateParams") View view =
                LayoutInflater
                        .from(mCourseFragment.mActivity)
                        .inflate(R.layout.course_popup_tag, null);
        mPwArea = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwArea.setOutsideTouchable(true);
        mPwArea.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));
    }

    /**
     * 记录当前课程标签
     * @param position 位置
     */
    private static void recordCurTag(int position) {
        if (mFilterTags == null || position >= mFilterTags.size()) return;

        FilterTagM filterTag = mFilterTags.get(position);

        if (filterTag == null) return;

        mCurTagId = filterTag.getId();

        // 更新菜单栏文字
        mCourseFragment.mTvFilterTag.setText(filterTag.getCategory_name());
    }

    /**
     * 课程中心点击事件
     */
    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_tag_rl:
                    // Filter：课程标签
                    if (mPwTag == null) initPwTag();
                    mPwTag.showAsDropDown(mCourseFragment.mRlTag, 0, 2);
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
                        case R.id.filter_coursetag_gv:
                            // Popup：课程标签
                            TextView tvTag = (TextView) view.findViewById(
                                    R.id.course_filter_gv_item);
                            tvTag.setTextColor(Color.WHITE);
                            tvTag.setBackgroundResource(R.drawable.wholepage_item_all_selected);

                            // 前一个view改变
                            if (mTvLastTag != null && mTvLastTag != tvTag) {
                                mTvLastTag.setBackgroundResource(R.drawable.wholepage_item_all);
                                mTvLastTag.setTextColor(
                                        mCourseFragment.mActivity
                                                .getResources().getColor(R.color.setting_text));
                            }

                            mTvLastTag = tvTag;

                            // 记录当前的课程标签
                            recordCurTag(position);

                            break;
                    }
                }
            };
}
