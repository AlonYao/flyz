package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.FilterCourseTagAdapter;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.model.netdata.course.FilterTagM;
import com.appublisher.quizbank.model.netdata.course.FilterTagResp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 课程中心
 */
public class CourseModel {

    private static PopupWindow mPwTag;
    private static CourseFragment mCourseFragment;
    private static ArrayList<FilterTagM> mFilterTags;

    /**
     * 处理课程标签回调
     * @param response 回调数据
     */
    public static void dealCourseFilterTagResp(JSONObject response, CourseFragment courseFragment) {
        // 初始化数据
        if (response == null) return;
        FilterTagResp filterTagResp =
                CourseFragment.mGson.fromJson(response.toString(), FilterTagResp.class);
        if (filterTagResp == null || filterTagResp.getResponse_code() != 1) return;

        // 成员变量赋值
        mCourseFragment = courseFragment;
        mFilterTags = filterTagResp.getList();

        // Filter 课程标签
        courseFragment.mRlTag.setOnClickListener(onClickListener);
    }

    /**
     * 课程中心点击事件
     */
    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_tag_rl:
                    // Filter课程标签
                    if (mPwTag == null) initPwTag();
                    mPwTag.showAsDropDown(mCourseFragment.mRlTag, 0, 2);
                    break;
            }
        }
    };

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

        mPwTag.update();
    }
}
