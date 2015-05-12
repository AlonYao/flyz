package com.appublisher.quizbank.model;

import android.view.View;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.OpenCourseUnstartActivity;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseDetailResp;
import com.appublisher.quizbank.model.netdata.opencourse.OpenCourseM;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * OpenCourse Model
 */
public class OpenCourseModel {

    /**
     * 处理公开课详情回调
     * @param activity OpenCourseUnstartActivity
     * @param response 回调数据
     */
    public static void dealOpenCourseDetailResp(final OpenCourseUnstartActivity activity,
                                                JSONObject response) {
        if (response == null) return;

        Gson gson = GsonManager.initGson();
        OpenCourseDetailResp openCourseDetailResp = gson.fromJson(response.toString(),
                OpenCourseDetailResp.class);

        if (openCourseDetailResp == null || openCourseDetailResp.getResponse_code() != 1) return;

        OpenCourseM openCourse = openCourseDetailResp.getCourse();

        if (openCourse == null) return;

        // 公开课封面
        activity.mRequest.loadImage(openCourse.getCover_pic(), activity.mIvPic);

        // 公开课名字
        activity.mTvName.setText("名字：" + openCourse.getName());

        // 公开课时间
        String startTime = openCourse.getStart_time();
        String endTime = openCourse.getEnd_time();

        try {
            if (startTime != null) startTime = startTime.substring(0, 16);
            if (endTime != null) endTime = endTime.substring(11, 16);
        } catch (Exception e) {
            activity.mTvTime.setText("时间：" + openCourse.getStart_time()
                    + " - " + openCourse.getEnd_time());
        }

        activity.mTvTime.setText("时间：" + startTime + " - " + endTime);

        // 公开课讲师
        activity.mTvLector.setText("主讲：" + openCourse.getLector());

        // 预约状态
        boolean booked = openCourseDetailResp.isBooked();

        if (booked) {
            activity.mTvNotice.setText(R.string.opencourse_notice_true);
            activity.mTvNotice.setTextColor(
                    activity.getResources().getColor(R.color.setting_text));
            activity.mTvNotice.setBackgroundColor(
                    activity.getResources().getColor(R.color.transparency));

            activity.mTvNotice.setOnClickListener(null);

        } else {
            activity.mTvNotice.setText(R.string.opencourse_notice_false);
            activity.mTvNotice.setTextColor(
                    activity.getResources().getColor(R.color.white));
            activity.mTvNotice.setBackgroundColor(
                    activity.getResources().getColor(R.color.answer_sheet_btn));

            activity.mTvNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastManager.showToast(activity, "预约……施工中");
                }
            });
        }
    }
}
