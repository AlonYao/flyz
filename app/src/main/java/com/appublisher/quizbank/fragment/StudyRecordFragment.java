package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
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
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressBarManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.CommonFragmentActivity;
import com.appublisher.quizbank.activity.MainActivity;
import com.appublisher.quizbank.activity.RecordCollectActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewCommentListActivity;
import com.appublisher.quizbank.common.interview.netdata.InterviewCommentListResp;
import com.appublisher.quizbank.common.interview.netdata.InterviewRecordListItemBean;
import com.appublisher.quizbank.common.interview.network.InterviewRequest;
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

    public XListView mXListView;
    public ArrayList<HistoryPaperM> mWrittenList;
    public ArrayList<InterviewRecordListItemBean> mInterviewList;
    public int mOffset;
    public int mPage;
    public ImageView mIvNull;
    private View mRecordDataView;
    public boolean mIsRefresh;

    private int mCount;
    public QRequest mQRequest;
    public InterviewRequest mIRequest;
    private View mView;

    private StudyRecordModel mModel;            // 新的model
    private RadioButton mWriteButton;
    private RadioButton mInterviewButton;

    private RadioGroup radioGroup;
    private View mWriteHeadView;
    private View mInterviewHeadView;
    private RelativeLayout mWriteCollectRl;
    private RelativeLayout mWritewrongRl;
    private RelativeLayout mInterviewcollectRl;
    private RelativeLayout mInterviewCommentRl;
    public int mInterviewOffset;
    private int mAddpage;
    public MainActivity mActivity;

    private View mInterviewRedPoint;
    private View mCommentRedPoint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mIsRefresh = false;
        mOffset = 0;
        mAddpage = 1;
        mInterviewOffset = 0;
        mCount = 10;
        mQRequest = new QRequest(mActivity, this);
        mIRequest = new InterviewRequest(mActivity, this);
        mWrittenList = new ArrayList<>();
        mInterviewList = new ArrayList<>();
        mModel = new StudyRecordModel(mActivity, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_studyrecord, container, false);
        mXListView = (XListView) mView.findViewById(R.id.studyrecord_lv);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);
        mRecordDataView = mView.findViewById(R.id.record_view);

        View headView = mView.findViewById(R.id.headview_new);

        mWriteCollectRl = (RelativeLayout) headView.findViewById(R.id.write_collect_rl);    // 笔试:收藏
        mWritewrongRl = (RelativeLayout) headView.findViewById(R.id.write_wrong_rl);        // 笔试:错题
        mInterviewcollectRl = (RelativeLayout) headView.findViewById(R.id.interview_collect_rl); // 面试:收藏
        mInterviewCommentRl = (RelativeLayout) headView.findViewById(R.id.interview_comment_rl);
        mWriteHeadView = headView.findViewById(R.id.write_tab_view);
        mInterviewHeadView = headView.findViewById(R.id.interview_tab_view);

        mInterviewRedPoint = headView.findViewById(R.id.interview_red_point);
        mCommentRedPoint = headView.findViewById(R.id.comment_red_point);

        radioGroup = (RadioGroup) headView.findViewById(R.id.record_radiogroup);
        mWriteButton = (RadioButton) headView.findViewById(R.id.radiobutton_write_button);   // 笔试button
        mInterviewButton = (RadioButton) headView.findViewById(R.id.radiobutton_interview_button);   // 面试button

        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);        // 刷新

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
                final Intent intent = new Intent(getActivity(), RecordCollectActivity.class); // 进入面试中的收藏页面
                startActivity(intent);

            }
        });

        mInterviewCommentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewCommentListActivity.class);
                startActivity(intent);
            }
        });


        mWriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {     // 笔试radiobutton
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 笔试button处理
                if (isChecked) {
                    setRadioButtonLeftChecked(mWriteButton);
                    setRadioButtonRightUnChecked(mInterviewButton);

                    if (mWrittenList == null || mWrittenList.size() == 0) {
                        mQRequest.getHistoryPapers(0, mCount);     // 点击了笔试button,去获取数据:初始获取数据在LoadMore()方法中
                    } else {
                        showXListview();
                        mXListView.setAdapter(mModel.mWrittenAdapter);
                        mModel.mWrittenAdapter.notifyDataSetChanged();
                    }
                    mXListView.setOnItemClickListener(mModel.writtenListener);

                    mWriteHeadView.setVisibility(View.VISIBLE);
                    mInterviewHeadView.setVisibility(View.GONE);

                } else {
                    setRadioButtonLeftUnChecked(mWriteButton);

                    mWriteHeadView.setVisibility(View.GONE);
                    mInterviewHeadView.setVisibility(View.VISIBLE);
                }
            }
        });
        mInterviewButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {    // 面试的button
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  // 记录页面:面试按钮
                // 面试button处理
                if (isChecked) {
                    setRadioButtonRightChecked(mInterviewButton);
                    setRadioButtonLeftUnChecked(mWriteButton);

                    if (mInterviewList == null || mInterviewList.size() == 0) {
                        mPage = 1;
                        mQRequest.getStudyRecordInterviewHistoryPapersNew(mPage);     // 默认只加载第一页数据
                    } else {
                        showXListview();
                        mXListView.setAdapter(mModel.mInterviewAdapter);
                        mModel.mInterviewAdapter.notifyDataSetChanged();// 重新设置adapter
                    }
                    mXListView.setOnItemClickListener(mModel.interviewListener);
                } else {
                    setRadioButtonRightUnChecked(mInterviewButton);
                }
            }
        });

        setRadioGroupBg(radioGroup);
        mWriteButton.setChecked(true);
    }

    public void setmPage() {      // 如果加载失败,页数需要将加过的减去
        mPage = mPage - mAddpage;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (response == null || apiName == null) return;

        if ("history_papers".equals(apiName)) {         //  记录页面:笔试
            mModel.dealHistoryPapersResp(this, response);

        } else if ("user_interview_record".equals(apiName)) {   // 记录页面:面试
            mModel.dealInterviewHistoryPapersResp(this, response);  // 如果是面试页面:处理数据
        } else if ("comment_list".equals(apiName)) {
            InterviewCommentListResp commentListResp = GsonManager.getModel(response, InterviewCommentListResp.class);
            if (commentListResp.getResponse_code() == 1) {
                if (commentListResp.getList().size() > 0) {
                    mInterviewRedPoint.setVisibility(View.VISIBLE);
                    mCommentRedPoint.setVisibility(View.VISIBLE);

                    int height = ((MainActivity) getActivity()).recordRadioButton.getHeight();
                    int marginRight = (int) (Utils.getWindowWidth(getActivity()) / 5 * 1.22);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            20, 20);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    lp.bottomMargin = (int) (height * 0.7);
                    lp.rightMargin = marginRight;
                    ((MainActivity) getActivity()).recordTip.setLayoutParams(lp);
                    ((MainActivity) getActivity()).recordTip.setVisibility(View.VISIBLE);
                } else {
                    mInterviewRedPoint.setVisibility(View.INVISIBLE);
                    mCommentRedPoint.setVisibility(View.INVISIBLE);
                    ((MainActivity) getActivity()).recordTip.setVisibility(View.INVISIBLE);
                }
            } else {
                mInterviewRedPoint.setVisibility(View.INVISIBLE);
                mCommentRedPoint.setVisibility(View.INVISIBLE);
                ((MainActivity) getActivity()).recordTip.setVisibility(View.INVISIBLE);
            }
        }
        setLoadFinish();
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
        if (mWriteButton.isChecked()) {
            mOffset = 0;
            mWrittenList = new ArrayList<>();
            Logger.e("onRefresh().mWriteButton.isChecked()");
            mQRequest.getHistoryPapers(mOffset, mCount);
        } else {
            mPage = 1;
            mInterviewList = new ArrayList<>();
            Logger.e("onRefresh().mInterviewButton.isChecked()");
            mQRequest.getStudyRecordInterviewHistoryPapersNew(mPage);
        }
        mIsRefresh = true;

        mIRequest.getCommentList(0, -1, 1);

    }

    @Override
    public void onLoadMore() {

        if (mWriteButton.isChecked()) {
            mOffset = mOffset + mCount;
            Logger.e("onLoadMore().mWriteButton.isChecked()");
            mQRequest.getHistoryPapers(mOffset, mCount);
        } else {
            mPage = mPage + mAddpage;
            Logger.e("onLoadMore().mInterviewButton.isChecked()");
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

    public void showXListview() {
        mIvNull.setVisibility(View.GONE);
        mRecordDataView.setVisibility(View.VISIBLE);
    }

    public void showIvNull() {
        mIvNull.setVisibility(View.VISIBLE);
        mRecordDataView.setVisibility(View.GONE);
    }

}
