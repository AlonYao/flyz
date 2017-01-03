package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ProgressBarManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.model.business.StudyRecordModel_new;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.network.QRequest;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 学习记录
 */
public class StudyRecordFragment_new extends Fragment implements RequestCallback,
        XListView.IXListViewListener {

    public Activity mActivity;
    public XListView mXListView;
    public ArrayList<HistoryPaperM> mHistoryPapers;
    public int mOffset;
    public ImageView mIvNull;
    public boolean mIsRefresh;

    private int mCount;
    private QRequest mQRequest;
    private View mView;

    private StudyRecordModel_new mModel;            // 新的model
    private RadioButton mWriteButton;
    private RadioButton mInterviewButton;
    private boolean isWriteView = true;    // true是笔试页面,false是面试页面
    private RadioGroup radioGroup;
    private RelativeLayout mWriteCollectRl;
    private RelativeLayout mWritewrongRl;
    private RelativeLayout mInterviewcollectRl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mIsRefresh = false;
        mOffset = 0;
        mCount = 10;
        mQRequest = new QRequest(mActivity, this);
        mHistoryPapers = new ArrayList<>();
        mModel = new StudyRecordModel_new(mActivity,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_studyrecord, container, false);
        mXListView = (XListView) mView.findViewById(R.id.studyrecord_lv);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        View headView = inflater.inflate(R.layout.fragment_studyrecord_headview_new, null);

        mWriteCollectRl = (RelativeLayout) headView.findViewById(R.id.write_collect_rl);    // 笔试:收藏
        mWritewrongRl = (RelativeLayout) headView.findViewById(R.id.write_wrong_rl);        // 笔试:错题
        mInterviewcollectRl = (RelativeLayout) headView.findViewById(R.id.interview_collect_rl); // 面试:收藏

        radioGroup = (RadioGroup) headView.findViewById(R.id.record_radiogroup);
        mWriteButton = (RadioButton) headView.findViewById(R.id.radiobutton_write_button);   // 笔试button
        mInterviewButton = (RadioButton) headView.findViewById(R.id.radiobutton_interview_button);   // 面试button

        // 初始化XListView
        mXListView.addHeaderView(headView, null, false);

        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);
        setRadioButtonLeftChecked(mWriteButton);
        setRadioButtonRightUnChecked(mInterviewButton);

        // showLoading
        ProgressBarManager.showProgressBar(mView);

        setValue();

        return mView;
    }

    public void setValue() {

        mWriteCollectRl.setOnClickListener(new View.OnClickListener() {    // 笔试页面:收藏
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "collect");
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                UmengManager.onEvent(getContext(), "Record", map);
            }
        });

        mWritewrongRl.setOnClickListener(new View.OnClickListener() {     // 笔试页面:错题
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "wrong");
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Error");
                UmengManager.onEvent(getContext(), "Record", map);
            }
        });
        mInterviewcollectRl.setOnClickListener(new View.OnClickListener() {    // 面试页面:收藏
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
                intent.putExtra("from", "collect");
                startActivity(intent);

                // Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Action", "Collect");
                UmengManager.onEvent(getContext(), "Record", map);
            }
        });
        mWriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {     // 笔试radiobutton
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isWriteView = true;
                    setRadioButtonLeftChecked(mWriteButton);
                    setRadioButtonRightUnChecked(mInterviewButton);
                   // mQRequest.getHistoryPapers(mOffset, mCount);     // 点击了笔试button,去获取数据:初始获取数据在LoadMore()方法中
                } else {
                    setRadioButtonLeftUnChecked(mWriteButton);
                }
            }
        });
        mInterviewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {    // 面试的button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isWriteView = false;
                    setRadioButtonRightChecked(mInterviewButton);
                    setRadioButtonLeftUnChecked(mWriteButton);

              //      mQRequest.getHistoryPapers(mOffset, mCount);           // 需要修改参数
                } else {
                    setRadioButtonRightUnChecked(mInterviewButton);
                }
            }
        });
        setRadioGroupBg(radioGroup);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        if (!isHidden()) {
            refreshData();
        }

        // Umeng
        MobclickAgent.onPageStart("StudyRecordFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "StudyRecordFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // TalkingData
        TCAgent.onPageEnd(mActivity, "StudyRecordFragment");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshData();
        }
    }

    /*
    *   请求完数据后,通过adapter中的方法进行处理
    * */
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("history_papers".equals(apiName)) {
            mModel.dealHistoryPapersResp(this, response);
            dealInterview(true);
        }else{
            // 如果是面试页面
            mModel.dealInterviewHistoryPapersResp(this, response);
            dealInterview(false);
        }

        setLoadFinish();
    }
    /*
    *   显示不同的学习记录行
    * */
    public void dealInterview(boolean iswriteView){
        boolean isWriteView = iswriteView;
       if(isWriteView){
           mWriteCollectRl.setVisibility(View.VISIBLE);
           mWritewrongRl.setVisibility(View.VISIBLE);
           mInterviewcollectRl.setVisibility(View.GONE);
       }else{
           mWriteCollectRl.setVisibility(View.GONE);
           mWritewrongRl.setVisibility(View.GONE);
           mInterviewcollectRl.setVisibility(View.VISIBLE);
       }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        setLoadFinish();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        setLoadFinish();
    }

    @Override
    public void onRefresh() {
        mOffset = 0;
        mHistoryPapers = new ArrayList<>();
        mQRequest.getHistoryPapers(mOffset, mCount);
        mIsRefresh = true;
    }

    @Override
    public void onLoadMore() {
        mOffset = mOffset + mCount;
        mQRequest.getHistoryPapers(mOffset, mCount);
        mIsRefresh = false;
    }

    public void refreshData() {
        onRefresh();
    }

    /**
     * 加载结束
     */
    private void setLoadFinish() {
        onLoadFinish();
        ProgressBarManager.hideProgressBar();
    }

    /**
     * 刷新&加载结束时执行的操作
     */
    @SuppressLint("SimpleDateFormat")
    public void onLoadFinish() {
        mXListView.stopRefresh();
        mXListView.stopLoadMore();
        mXListView.setRefreshTime(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public void setRadioGroupBg(RadioGroup radioGroup) {
        // prepare
        int strokeWidth = 3; // 3px not dp
        int roundRadius = 20; // 8px not dp
        int strokeColor = mModel.getThemeColor();
        int fillColor = getResources().getColor(com.appublisher.lib_course.R.color.common_bg);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        radioGroup.setBackgroundDrawable(gd);
    }

    /**
     * 设置选中背景左
     *
     * @param radioButton
     */
    public void setRadioButtonLeftChecked(RadioButton radioButton) {
        radioButton.setTextColor(getResources().getColor(com.appublisher.lib_course.R.color.login_white));
        // prepare
        int fillColor = mModel.getThemeColor();

        float[] floats = new float[]{20, 20, 0, 0, 0, 0, 20, 20};

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadii(floats);
        radioButton.setBackgroundDrawable(gd);
    }

    /**
     * 设置选中背景右
     *
     * @param radioButton
     */
    public void setRadioButtonRightChecked(RadioButton radioButton) {
        radioButton.setTextColor(getResources().getColor(com.appublisher.lib_course.R.color.login_white));
        // prepare
        int fillColor = mModel.getThemeColor();

        float[] floats = new float[]{0, 0, 20, 20, 20, 20, 0, 0};

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadii(floats);
        radioButton.setBackgroundDrawable(gd);
    }

    /**
     * 设置未选中背景
     *
     * @param radioButton
     */
    public void setRadioButtonLeftUnChecked(RadioButton radioButton) {
        radioButton.setTextColor(mModel.getThemeColor());
        // prepare
        int strokeWidth = 3; // 3px not dp
        int strokeColor = mModel.getThemeColor();
        int fillColor = getResources().getColor(com.appublisher.lib_course.R.color.common_bg);

        float[] floats = new float[]{20, 20, 0, 0, 0, 0, 20, 20};

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadii(floats);
        gd.setStroke(strokeWidth, strokeColor);
        radioButton.setBackgroundDrawable(gd);
    }

    /**
     * 设置未选中背景
     *
     * @param radioButton
     */
    public void setRadioButtonRightUnChecked(RadioButton radioButton) {
        radioButton.setTextColor(mModel.getThemeColor());
        // prepare
        int strokeWidth = 3; // 3px not dp
        int strokeColor = mModel.getThemeColor();
        int fillColor = getResources().getColor(com.appublisher.lib_course.R.color.common_bg);

        float[] floats = new float[]{0, 0, 20, 20, 20, 20, 0, 0};

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadii(floats);
        gd.setStroke(strokeWidth, strokeColor);
        radioButton.setBackgroundDrawable(gd);
    }
}
