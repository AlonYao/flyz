package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 首页
 */
public class HomePageFragment extends Fragment implements RequestCallback{

    private Activity mActivity;
    private ScrollView mHomepage;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        TextView textView = (TextView) view.findViewById(R.id.homepage_quicktest);
        mHomepage = (ScrollView) view.findViewById(R.id.homepage);

        // 获取&呈现 数据
        if (Globals.homepageResp != null) {
            setContent(Globals.homepageResp);
        } else {
            ProgressBarManager.showProgressBar(view);
            new Request(mActivity, this).getEntryData();
        }

        // 快速练习
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MeasureActivity.class);
                intent.putExtra("flag", "auto_training");
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 设置内容
     * @param homepageResp 首页数据回调
     */
    private void setContent(JSONObject homepageResp) {
        showMainView();
    }

    /**
     * 显示主页面
     */
    private void showMainView() {
        mHomepage.setVisibility(View.VISIBLE);
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        showMainView();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        showMainView();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        showMainView();
    }
}
