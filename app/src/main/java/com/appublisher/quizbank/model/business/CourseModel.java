package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.WebViewActivity;
import com.appublisher.quizbank.adapter.CourseListAdapter;
import com.appublisher.quizbank.adapter.FilterCourseAreaAdapter;
import com.appublisher.quizbank.adapter.FilterCoursePurchaseAdapter;
import com.appublisher.quizbank.adapter.FilterCourseTagAdapter;
import com.appublisher.quizbank.common.login.model.LoginModel;
import com.appublisher.quizbank.customui.ExpandableHeightGridView;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.model.netdata.course.CourseListResp;
import com.appublisher.quizbank.model.netdata.course.CourseM;
import com.appublisher.quizbank.model.netdata.course.FilterAreaM;
import com.appublisher.quizbank.model.netdata.course.FilterAreaResp;
import com.appublisher.quizbank.model.netdata.course.FilterTagM;
import com.appublisher.quizbank.model.netdata.course.FilterTagResp;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.appublisher.quizbank.utils.UmengManager;
import com.umeng.analytics.MobclickAgent;

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
    private static ArrayList<CourseM> mCourses;
    public static TextView mTvLastTag;
    public static TextView mTvLastArea;
    public static TextView mTvLastPurchase;
    public static CourseListAdapter mCourseListAdapter;

    /**
     * 处理课程标签回调（科目）
     *
     * @param response       回调数据
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
     *
     * @param response       回调数据
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
        getCourseList(courseFragment);
    }

    /**
     * 处理课程列表回调
     *
     * @param response       回调数据
     * @param courseFragment 课程中心
     */
    public static void dealCourseListResp(JSONObject response, CourseFragment courseFragment) {
        if (response == null) {
            showCourseNone(courseFragment);
            return;
        }
        //网络状态不好时显示过提示view，需要隐藏.
        courseFragment.netBadView.setVisibility(View.INVISIBLE);
        // 初始化数据
        mCourseFragment = courseFragment;
        CourseListResp courseListResp = courseFragment.mGson.fromJson(
                response.toString(), CourseListResp.class);

        if (courseListResp == null || courseListResp.getResponse_code() != 1
                || courseListResp.getCourses() == null || courseListResp.getCourses().size() == 0) {
            showCourseNone(courseFragment);
            return;
        }

        if (mCourseListAdapter == null) {
            mCourses = courseListResp.getCourses();
            mCourseListAdapter = new CourseListAdapter(courseFragment.mActivity, mCourses);
            courseFragment.mLvCourse.setAdapter(mCourseListAdapter);
        } else {
            mCourses.clear();
            mCourses.addAll(courseListResp.getCourses());
            mCourseListAdapter.notifyDataSetChanged();
        }

        courseFragment.mCourseNull.setVisibility(View.GONE);
        courseFragment.mLvCourse.setVisibility(View.VISIBLE);
        courseFragment.mLvCourse.setOnItemClickListener(onItemClickListener);

        ProgressBarManager.hideProgressBar();
    }

    /**
     * 显示没有课程
     *
     * @param courseFragment 课程中心页面
     */
    private static void showCourseNone(CourseFragment courseFragment) {
        ProgressBarManager.hideProgressBar();
        courseFragment.mCourseNull.setVisibility(View.VISIBLE);
        courseFragment.mLvCourse.setVisibility(View.GONE);
    }

    /**
     * 初始化Filter课程标签（科目）
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
        //noinspection deprecation
        mPwTag.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        if (mFilterTags == null || mFilterTags.size() == 0) return;

        ExpandableHeightGridView gvTag =
                (ExpandableHeightGridView) view.findViewById(R.id.filter_course_ehgv);
        gvTag.setNumColumns(3);
        TextView tvConfirm = (TextView) view.findViewById(R.id.course_filter_confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.course_filter_cancel);

        FilterCourseTagAdapter filterCourseTagAdapter =
                new FilterCourseTagAdapter(mCourseFragment.mActivity, mFilterTags);
        gvTag.setAdapter(filterCourseTagAdapter);
        gvTag.setOnItemClickListener(onItemClickListener);

        tvConfirm.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
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
        //noinspection deprecation
        mPwPurchase.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        // 构造数据结构
        mFilterPurchase = new ArrayList<>();
        mFilterPurchase.add("全部");
        mFilterPurchase.add("已购");
        mFilterPurchase.add("未购");

        ExpandableHeightGridView gvPurchase =
                (ExpandableHeightGridView) view.findViewById(R.id.course_filter_purchase_ehgv);
        TextView tvConfirm = (TextView) view.findViewById(R.id.course_filter_confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.course_filter_cancel);

        FilterCoursePurchaseAdapter filterCoursePurchaseAdapter =
                new FilterCoursePurchaseAdapter(mCourseFragment.mActivity, mFilterPurchase);
        gvPurchase.setAdapter(filterCoursePurchaseAdapter);
        gvPurchase.setOnItemClickListener(onItemClickListener);

        tvConfirm.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
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
        //noinspection deprecation
        mPwArea.setBackgroundDrawable(
                mCourseFragment.mActivity.getResources().getDrawable(R.color.transparency));

        if (mFilterAreas == null || mFilterAreas.size() == 0) return;

        ExpandableHeightGridView gvArea =
                (ExpandableHeightGridView) view.findViewById(R.id.course_filter_area_ehgv);
        TextView tvConfirm = (TextView) view.findViewById(R.id.course_filter_confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.course_filter_cancel);

        FilterCourseAreaAdapter filterCourseAreaAdapter =
                new FilterCourseAreaAdapter(mCourseFragment.mActivity, mFilterAreas);
        gvArea.setAdapter(filterCourseAreaAdapter);
        gvArea.setOnItemClickListener(onItemClickListener);

        tvConfirm.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
    }

    /**
     * 记录当前课程标签
     *
     * @param position 位置
     */
    private static void recordCurTag(int position) {
        if (mFilterTags == null || position >= mFilterTags.size()) return;

        FilterTagM filterTag = mFilterTags.get(position);

        if (filterTag == null) return;

        CourseFragment.mCurTagId = filterTag.getId();

        // 更新Filter文字
        changeFilterText(mCourseFragment.mTvFilterTag, filterTag.getCategory_name());
    }

    /**
     * 记录当前课程购买状态
     *
     * @param position 位置
     */
    private static void recordCurPurchase(int position) {
        if (mFilterPurchase == null || position >= mFilterPurchase.size()) return;

        String curPurchase = mFilterPurchase.get(position);
        CourseFragment.mCurPurchaseId = 2;

        if ("未购".equals(curPurchase)) {
            CourseFragment.mCurPurchaseId = 0;
        } else if ("已购".equals(curPurchase)) {
            CourseFragment.mCurPurchaseId = 1;
        }

        // 更新Filter文字
        changeFilterText(mCourseFragment.mTvFilterPurchase, curPurchase);
    }

    /**
     * 记录当前课程地区
     *
     * @param position 位置
     */
    private static void recordCurArea(int position) {
        if (mFilterAreas == null || position >= mFilterAreas.size()) return;

        FilterAreaM filterArea = mFilterAreas.get(position);

        if (filterArea == null) return;

        CourseFragment.mCurAreaId = filterArea.getCode();

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

                case R.id.course_filter_confirm:
                    // 确认按钮
                    getCourseList(mCourseFragment);
                    dismissAllPw();
                    break;

                case R.id.course_filter_cancel:
                    // 取消按钮
                    dismissAllPw();
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

                            // Umeng统计
                            UmengManager.sendCountEvent(
                                    mCourseFragment.mActivity, "Filter", "Type", "Course");

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

                            // Umeng统计
                            UmengManager.sendCountEvent(
                                    mCourseFragment.mActivity, "Filter", "Type", "Buy");

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

                            // Umeng统计
                            UmengManager.sendCountEvent(
                                    mCourseFragment.mActivity, "Filter", "Type", "Province");

                            break;

                        case R.id.course_listview:
                            // 课程列表
                            skipToCoursePage(position);
                            break;
                    }
                }
            };

    /**
     * 跳转至课程页面
     *
     * @param position item位置
     */
    private static void skipToCoursePage(int position) {
        if (mCourses == null || position >= mCourses.size()) return;

        CourseM course = mCourses.get(position);
        if (course == null) return;
        String type = course.getType();
        String title = course.getName();
        String courseUrl = "";
        int courseId = course.getId();
//        String detailPage = course.getDetail_page();
        String detailPage = course.getDetail_page().replace("m.yaoguo.cn", "dev.m.zhiboke.net");
        if ("live".equals(type)) {
            // 直播课&公开课
            courseUrl = detailPage
                    + "user_id=" + LoginModel.getUserId()
                    + "&user_token=" + LoginModel.getUserToken()
                    + "&course_id=" + String.valueOf(courseId)
                    + "&app_type=quizbank"
                    + "&app_version=" + Globals.appVersion;
            Logger.i("courseUrl=" + courseUrl);
        } else if ("vod".equals(type)) {
            // 录播课
            if (course.is_purchased()) {
                // 已购
                courseUrl = "http://daily.edu.appublisher.com/buy/detail.php?courseid="
                        + String.valueOf(courseId)
                        + "&uid=" + LoginModel.getUserId();
            } else {
                // 未购
                courseUrl = "http://daily.edu.appublisher.com/buy/info.php?courseid="
                        + String.valueOf(courseId)
                        + "&uid=" + LoginModel.getUserId();
            }
        }

        Intent intent = new Intent(mCourseFragment.mActivity, WebViewActivity.class);
        intent.putExtra("url", courseUrl);
        intent.putExtra("bar_title", title == null ? "" : title);
        intent.putExtra("from", "course");
        intent.putExtra("course_id", courseId);
        mCourseFragment.startActivityForResult(intent, ActivitySkipConstants.COURSE);

        // Umeng统计
        HashMap<String, String> map = new HashMap<>();
        map.put("CourseID", String.valueOf(courseId));
        map.put("Entry", "Cell");
        map.put("Status", course.is_purchased() ? "1" : "0");
        MobclickAgent.onEvent(mCourseFragment.mActivity, "EnterCourse", map);
    }

    /**
     * 设置控件的选中状态
     *
     * @param textView 控件
     */
    private static void setPopupTextViewSelect(TextView textView) {
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.wholepage_item_all_selected);
    }

    /**
     * 取消控件的选中状态
     *
     * @param textView 控件
     */
    private static void cancelPopupTextViewSelect(TextView textView) {
        textView.setBackgroundResource(R.drawable.wholepage_item_all);
        textView.setTextColor(
                mCourseFragment.mActivity
                        .getResources().getColor(R.color.common_text));
    }

    /**
     * 更新Filter文字
     *
     * @param textView 控件
     * @param name     名称
     */
    public static void changeFilterText(TextView textView, String name) {
        if (name != null && name.length() >= 5) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }

        textView.setText(name);
        textView.setTextColor(
                mCourseFragment.mActivity.getResources().getColor(R.color.course_filter_selected));
    }

    /**
     * 接口回调异常处理
     *
     * @param apiName        接口类型
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
            getCourseList(courseFragment);
        }
        if ("course_list".equals(apiName)) {
            mCourses.clear();
            mCourseListAdapter.notifyDataSetChanged();
            courseFragment.netBadView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 关闭所有的popupwindow
     */
    private static void dismissAllPw() {
        if (mPwTag != null && mPwTag.isShowing()) mPwTag.dismiss();
        if (mPwPurchase != null && mPwPurchase.isShowing()) mPwPurchase.dismiss();
        if (mPwArea != null && mPwArea.isShowing()) mPwArea.dismiss();
    }

    /**
     * 获取课程列表
     *
     * @param courseFragment CourseFragment
     */
    public static void getCourseList(CourseFragment courseFragment) {
        ProgressBarManager.showProgressBar(courseFragment.mMainView);
        courseFragment.mRequest.getCourseList(
                CourseFragment.mCurTagId,
                CourseFragment.mCurAreaId,
                CourseFragment.mCurPurchaseId);
    }
}
