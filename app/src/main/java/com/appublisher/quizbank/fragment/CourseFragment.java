package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CourseModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
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

    private Request mRequest;
    private String mUserId;
    private View mMainView;

    /** Filter **/
    public RelativeLayout mRlTag;

    public Activity mActivity;
    public static Gson mGson;

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

        // 获取数据
        ProgressBarManager.showProgressBar(mMainView);
        mRequest.getCourseFilterTag();

        return mMainView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !LoginModel.getUserId().equals(mUserId)) {
            // 如果登录用户发生变化，则刷新课程列表

            // 更新用户id
            mUserId = LoginModel.getUserId();
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("course_filter_tag".equals(apiName))
            CourseModel.dealCourseFilterTagResp(response, this);
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressBarManager.hideProgressBar();
    }
}
