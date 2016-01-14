package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.adapter.ListMyGradeAdapter;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseRateEntity;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseRequest;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassItem;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseUnrateClassResp;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的评价页面
 */
public class OpenCourseMyGradeActivity extends AppCompatActivity implements RequestCallback{

    public ListView mListView;
    public ListMyGradeAdapter mAdapter;
    public ArrayList<OpenCourseUnrateClassItem> mUnRateClasses;
    public String mIsOpen;
    public OpenCourseRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_my_grade);

        // Toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, "评分");

        // init data
        mRequest = new OpenCourseRequest(this, this);
        String entry = getIntent().getStringExtra("entry");
        mIsOpen = "false";
        if ("opencourse".equals(entry)) mIsOpen = "true";
        // noinspection unchecked
        mUnRateClasses = (ArrayList<OpenCourseUnrateClassItem>)
                        getIntent().getSerializableExtra("unrate_classes");

        // init view
        mListView = (ListView) findViewById(R.id.mygrade_lv);

        // show listview
        if (mUnRateClasses == null || mUnRateClasses.size() == 0) {
            ToastManager.showToast(this, "暂无待评价课程");
        } else {
            mAdapter = new ListMyGradeAdapter(this, mUnRateClasses);
            mListView.setAdapter(mAdapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUnRateClasses == null || position >= mUnRateClasses.size()) return;

                OpenCourseUnrateClassItem item = mUnRateClasses.get(position);
                if (item == null) return;

                OpenCourseRateEntity entity = new OpenCourseRateEntity();
                if ("true".equals(mIsOpen)) {
                    entity.class_id = item.getId();
                } else {
                    entity.course_id = item.getId();
                }
                entity.is_open = mIsOpen;

                OpenCourseModel.showGradeAlert(OpenCourseMyGradeActivity.this, entity);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        switch (apiName) {
            case "get_unrated_class":
                OpenCourseUnrateClassResp resp =
                        GsonManager.getModel(response, OpenCourseUnrateClassResp.class);
                OpenCourseModel.dealUnrateClassResp(this, resp);
                ProgressDialogManager.closeProgressDialog();
                break;

            case "rate_class":
                OpenCourseRateResp rateResp =
                        GsonManager.getModel(response, OpenCourseRateResp.class);
                if (rateResp != null && rateResp.getResponse_code() == 1) {
                    OpenCourseModel.closeRateDialog();
                    mRequest.getUnratedClass(mIsOpen, 1);
                } else {
                    ProgressDialogManager.closeProgressDialog();
                    ToastManager.showToast(this, "评价提交失败");
                }

                break;

            default:
                ProgressDialogManager.closeProgressDialog();
                break;
        }
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
