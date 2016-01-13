package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.adapter.ListMyGradeAdapter;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassItem;
import com.appublisher.quizbank.customui.MultiListView;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的评价页面
 */
public class OpenCourseMyGradeActivity extends AppCompatActivity implements RequestCallback{

    public MultiListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_my_grade);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, "评分");

        // init data
        // noinspection unchecked
        ArrayList<OpenCourseUnrateClassItem> unRateClasses =
                (ArrayList<OpenCourseUnrateClassItem>)
                        getIntent().getSerializableExtra("unrate_classes");

        // init view
        mListView = (MultiListView) findViewById(R.id.mygrade_lv);

        if (unRateClasses == null || unRateClasses.size() == 0) {
            ToastManager.showToast(this, "暂无待评价课程");
        } else {
            ListMyGradeAdapter adapter = new ListMyGradeAdapter(this, unRateClasses);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {

    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
