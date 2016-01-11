package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseRequest;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressDialogManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 公开课单个课程评价页面
 */
public class OpenCourseGradeActivity extends AppCompatActivity implements RequestCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_grade);

        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, getIntent().getStringExtra("bar_title"));

        // 变量初始化
        int courseId = getIntent().getIntExtra("course_id", 0);
        OpenCourseRequest request = new OpenCourseRequest(this, this);

        ProgressDialogManager.showProgressDialog(this);
        request.getGradeList(courseId, 0, 0, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
