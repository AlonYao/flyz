package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;

/**
 * 我的评价页面
 */
public class OpenCourseMyGradeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_my_grade);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, "评分");
    }

}
