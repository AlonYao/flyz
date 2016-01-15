package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseRequest;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateListResp;
import com.appublisher.quizbank.common.opencourse.netdata.RateListOthersItem;
import com.appublisher.quizbank.customui.XListView;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 公开课单个课程评价页面
 */
public class OpenCourseGradeActivity extends AppCompatActivity
        implements RequestCallback, XListView.IXListViewListener{

    public XListView mXlv;
    public LinearLayout mLlMine;
    public Button mBtn;
    public int mCurPage;
    public ArrayList<RateListOthersItem> mOthers;
    public OpenCourseRequest mRequest;
    public int mCourseId;
    public int mClassId;
    public String mUrl;
    public String mCourseName;
    public String mIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_course_grade);

        // init toolbar
        CommonModel.setToolBar(this);
        CommonModel.setBarTitle(this, getIntent().getStringExtra("bar_title"));

        // init data
        mCourseId = getIntent().getIntExtra("course_id", 0);
        mClassId = getIntent().getIntExtra("class_id", 0);
        mUrl = getIntent().getStringExtra("url");
        mCourseName = getIntent().getStringExtra("bar_title");
        mRequest = new OpenCourseRequest(this, this);
        mCurPage = 1;
        mIsOpen = "false";
        if ("opencourse".equals(getIntent().getStringExtra("entry"))) {
            mIsOpen = "true";
        }

        // init view
        mXlv = (XListView) findViewById(R.id.opencourse_grade_xlv);
        mLlMine = (LinearLayout) findViewById(R.id.opencourse_grade_mine_ll);
        mBtn = (Button) findViewById(R.id.opencourse_grade_btn);

        mXlv.setXListViewListener(this);
        mXlv.setPullLoadEnable(true);
        mXlv.setPullRefreshEnable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProgressDialogManager.showProgressDialog(this);
        mRequest.getGradeList(mCourseId, 0, 0, 1);
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
            case "get_grade_list":
                OpenCourseRateListResp resp =
                        GsonManager.getModel(response, OpenCourseRateListResp.class);
                OpenCourseModel.dealOpenCourseRateListResp(this, resp);
                ProgressDialogManager.closeProgressDialog();
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

    @Override
    public void onRefresh() {
        mCurPage = 1;
        mRequest.getGradeList(mCourseId, 0, 0, mCurPage);
    }

    @Override
    public void onLoadMore() {
        mCurPage++;
        mRequest.getGradeList(mCourseId, 0, 0, mCurPage);
    }
}
