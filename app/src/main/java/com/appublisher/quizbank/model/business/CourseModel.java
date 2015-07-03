package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.fragment.CourseFragment;
import com.appublisher.quizbank.model.netdata.course.FilterTagResp;

import org.json.JSONObject;

/**
 * 课程中心
 */
public class CourseModel {

    private static PopupWindow mPwTag;
    private static CourseFragment mCourseFragment;

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

        mCourseFragment = courseFragment;

        // 课程标签
        courseFragment.mRlTag.setOnClickListener(onClickListener);
    }

    /**
     * 点击事件
     */
    private static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.course_tag_rl:
                    // 课程标签Filter
                    if (mPwTag == null) initPwTag();
                    mPwTag.showAsDropDown(mCourseFragment.mRlTag);
                    break;
            }
        }
    };

    /**
     * 初始化课程标签Filter
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

        mPwTag.update();
    }
}
