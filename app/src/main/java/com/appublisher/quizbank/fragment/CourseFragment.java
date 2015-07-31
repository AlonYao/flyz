package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CourseModel;
import com.appublisher.quizbank.model.business.OpenCourseModel;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 课程中心
 */
public class CourseFragment extends Fragment implements RequestCallback{

    public View mMainView;
    public Activity mActivity;
    public Request mRequest;
    public Gson mGson;
    private TextView mCourseQQ;

    /** Filter **/
    public RelativeLayout mRlTag;
    public RelativeLayout mRlArea;
    public RelativeLayout mRlPurchase;
    public TextView mTvFilterTag;
    public TextView mTvFilterArea;
    public TextView mTvFilterPurchase;
    public String mCurAreaId;
    public int mCurTagId;
    public int mCurPurchaseId;

    /** 课程中心列表 **/
    public ListView mLvCourse;
    public LinearLayout mCourseNull;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequest = new Request(mActivity, this);
        mGson = GsonManager.initGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // view初始化
        mMainView = inflater.inflate(R.layout.fragment_course, container, false);
        mRlTag = (RelativeLayout) mMainView.findViewById(R.id.course_tag_rl);
        mTvFilterTag = (TextView) mMainView.findViewById(R.id.course_tag_tv);
        mRlArea = (RelativeLayout) mMainView.findViewById(R.id.course_area_rl);
        mTvFilterArea = (TextView) mMainView.findViewById(R.id.course_area_tv);
        mRlPurchase = (RelativeLayout) mMainView.findViewById(R.id.course_purchase_rl);
        mTvFilterPurchase = (TextView) mMainView.findViewById(R.id.course_purchase_tv);
        mLvCourse = (ListView) mMainView.findViewById(R.id.course_listview);
        mCourseNull = (LinearLayout) mMainView.findViewById(R.id.course_null);
        mCourseQQ = (TextView) mMainView.findViewById(R.id.course_qq);

        // 成员变量初始化
        mCurTagId = 0;
        mCurPurchaseId = 2;
        mCurAreaId = "ALL";
        CourseModel.mCourseListAdapter = null;

        // 已购/未购
        mRlPurchase.setOnClickListener(CourseModel.onClickListener);

        // 获取数据
        ProgressBarManager.showProgressBar(mMainView);
        mRequest.getCourseFilterTag();

        // 客服QQ
        mCourseQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCourseModel.setMarketQQ(mActivity);
            }
        });

        return mMainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivitySkipConstants.COURSE) {
            CourseModel.getCourseList(this);
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("course_filter_tag".equals(apiName))
            CourseModel.dealCourseFilterTagResp(response, this);

        if ("course_filter_area".equals(apiName))
            CourseModel.dealCourseFilterAreaResp(response, this);

        if ("course_list".equals(apiName))
            CourseModel.dealCourseListResp(response, this);
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        CourseModel.dealRespError(apiName, this);
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        CourseModel.dealRespError(apiName, this);
    }

}
