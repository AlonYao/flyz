package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressBarManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.activity.RecordCollectActivity;
import com.appublisher.quizbank.model.business.StudyRecordModel;
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
public class StudyRecordFragment extends Fragment implements RequestCallback,
        XListView.IXListViewListener {

    public Activity mActivity;
    public XListView mXListView;
    public ArrayList<HistoryPaperM> mHistoryPapers;
    public ArrayList<HistoryPaperM> mInterviewHistoryPapers;
    public int mOffset;
    public int mPage;
    public ImageView mIvNull;
    public boolean mIsRefresh;

    private int mCount;
    public QRequest mQRequest;
    private View mView;

    private StudyRecordModel mModel;            // 新的model
    private RadioButton mWriteButton;
    private RadioButton mInterviewButton;
    private boolean isWriteView ;    // true是笔试页面,false是面试页面
    private RadioGroup radioGroup;
    private RelativeLayout mWriteCollectRl;
    private RelativeLayout mWritewrongRl;
    private RelativeLayout mInterviewcollectRl;
    public int mInterviewOffset;
    private int mAddpage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mIsRefresh = false;
        mOffset = 0;
        mAddpage = 1;
        mInterviewOffset = 0;
        mCount = 10;
        isWriteView = true;
        mQRequest = new QRequest(mActivity, this);
        mHistoryPapers = new ArrayList<>();
        mInterviewHistoryPapers = new ArrayList<>();
        mModel = new StudyRecordModel(mActivity,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_studyrecord, container, false);
        mXListView = (XListView) mView.findViewById(R.id.studyrecord_lv);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        //View headView = inflater.inflate(R.layout.fragment_studyrecord_headview_new, null);
        View headView = mView.findViewById(R.id.headview_new);

        mWriteCollectRl = (RelativeLayout) headView.findViewById(R.id.write_collect_rl);    // 笔试:收藏
        mWritewrongRl = (RelativeLayout) headView.findViewById(R.id.write_wrong_rl);        // 笔试:错题
        mInterviewcollectRl = (RelativeLayout) headView.findViewById(R.id.interview_collect_rl); // 面试:收藏

        radioGroup = (RadioGroup) headView.findViewById(R.id.record_radiogroup);
        mWriteButton = (RadioButton) headView.findViewById(R.id.radiobutton_write_button);   // 笔试button
        mInterviewButton = (RadioButton) headView.findViewById(R.id.radiobutton_interview_button);   // 面试button

        // 初始化XListView
       // mXListView.addHeaderView(headView, null, false);

        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);        // 刷新

        ProgressBarManager.showProgressBar(mView);

        setValue();

        SharedPreferences sp = mActivity.getSharedPreferences("radiobutton", Context.MODE_PRIVATE);
        boolean iswriteView = sp.getBoolean("isWriteView", true);
        if (iswriteView) {   // 如果是笔试
            mWriteButton.setChecked(true);
            dealInterview(true);
        } else {            // 如果是面试
            mInterviewButton.setChecked(true);
            dealInterview(false);
        }
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
                final Intent intent = new Intent(getActivity(), RecordCollectActivity.class); // 进入面试中的收藏页面
                startActivity(intent);

            }
        });
        mWriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {     // 笔试radiobutton
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isWriteView = true;
                    Logger.e("点击了笔试button");
                    setRadioButtonLeftChecked(mWriteButton);
                    setRadioButtonRightUnChecked(mInterviewButton);

                    SharedPreferences sp = mActivity.getSharedPreferences("radiobutton", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isWriteView", isWriteView);
                    editor.commit();
                    if (mHistoryPapers == null || mHistoryPapers.size() == 0) {
                        mQRequest.getHistoryPapers(0, mCount);     // 点击了笔试button,去获取数据:初始获取数据在LoadMore()方法中
                    } else {
                        showXListview();
                        mXListView.setAdapter(mModel.mHistoryPapersListAdapter);
                    }
                    dealInterview(true);
                } else {
                    setRadioButtonLeftUnChecked(mWriteButton);
                    dealInterview(false);
                }
            }
        });
        mInterviewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {    // 面试的button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  // 记录页面:面试按钮
                if (isChecked) {
                    isWriteView = false;
                    Logger.e("点击了面试button");
                    setRadioButtonRightChecked(mInterviewButton);
                    setRadioButtonLeftUnChecked(mWriteButton);

                    SharedPreferences sp = mActivity.getSharedPreferences("radiobutton", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isWriteView", isWriteView);
                    editor.commit();

                    if (mInterviewHistoryPapers == null || mInterviewHistoryPapers.size() == 0) {
                        mPage = 1;
                        mQRequest.getStudyRecordInterviewHistoryPapersNew(mPage);     // 默认只加载第一页数据
                    } else {
                        showXListview();
                        mXListView.setAdapter(mModel.mInterviewHistoryPapersListAdapter);    // 重新设置adapter
                    }
                    dealInterview(false);
                } else {
                    setRadioButtonRightUnChecked(mInterviewButton);
                    dealInterview(true);
                }
            }
        });
        setRadioGroupBg(radioGroup);
    }

    public void setmPage(){      // 如果加载失败,页数需要将加过的减去
        mPage = mPage - mAddpage;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("onResume");
        // 获取数据
        if (!isHidden()) {
            refreshData();

            SharedPreferences sp = mActivity.getSharedPreferences("radiobutton", Context.MODE_PRIVATE);
            boolean iswriteView = sp.getBoolean("isWriteView", true);
            if (iswriteView) {   // 如果是笔试
                mWriteButton.setChecked(true);
            } else {            // 如果是面试
                mInterviewButton.setChecked(true);
            }
        }
        // Umeng
        MobclickAgent.onPageStart("StudyRecordFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "StudyRecordFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(mActivity, "StudyRecordFragment");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshData();

            SharedPreferences sp = mActivity.getSharedPreferences("radiobutton", Context.MODE_PRIVATE);
            boolean iswriteView = sp.getBoolean("isWriteView", true);
            if (iswriteView) {   // 如果是笔试  -->换新条件
                mWriteButton.setChecked(true);
            } else {            // 如果是面试
                mInterviewButton.setChecked(true);
            }
        }
    }

    /*
    *   请求完数据后,通过adapter中的方法进行处理
    * */
    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) return;

        if ("history_papers".equals(apiName)) {         //  记录页面:笔试
            String mFrom = "write";
            mModel.dealHistoryPapersResp(this, response, mFrom);

        }else if("user_interview_record".equals(apiName)){   // 记录页面:面试
            String mFrom = "interview";
            mModel.dealInterviewHistoryPapersResp(this, response , mFrom);  // 如果是面试页面:处理数据
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
        if(isWriteView){
            mOffset = 0;
            mHistoryPapers = new ArrayList<>();
            mQRequest.getHistoryPapers(mOffset, mCount);
        }else{
            mPage = 1;
            mInterviewHistoryPapers = new ArrayList<>();
            mQRequest.getStudyRecordInterviewHistoryPapersNew(mPage);
        }
        mIsRefresh = true;
    }

    @Override
    public void onLoadMore() {
        if(isWriteView){
            mOffset = mOffset + mCount;
            mQRequest.getHistoryPapers(mOffset, mCount);
        }else{
            mPage = mPage + mAddpage;
            mQRequest.getStudyRecordInterviewHistoryPapersNew(mPage);

        }
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

    public void showXListview(){
        mIvNull.setVisibility(View.INVISIBLE);
        mXListView.setVisibility(View.VISIBLE);
    }
    public void showIvNull(){
        mIvNull.setVisibility(View.VISIBLE);
        mXListView.setVisibility(View.INVISIBLE);
    }
}
