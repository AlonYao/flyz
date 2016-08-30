package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.StudyRecordModel;
import com.appublisher.quizbank.model.netdata.history.HistoryPaperM;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 学习记录
 */
public class StudyRecordFragment extends Fragment implements RequestCallback,
        XListView.IXListViewListener{

    public Activity mActivity;
    public XListView mXListView;
    public ArrayList<HistoryPaperM> mHistoryPapers;
    public int mOffset;
    public ImageView mIvNull;

    private int mCount;
    private QRequest mQRequest;
    private View mView;

    /** Umeng */
    public String mUmengAction;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOffset = 0;
        mCount = 5;
        mQRequest = new QRequest(mActivity, this);
        mHistoryPapers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        mView = inflater.inflate(R.layout.fragment_studyrecord, container, false);
        mXListView = (XListView) mView.findViewById(R.id.studyrecord_lv);
        mIvNull = (ImageView) mView.findViewById(R.id.quizbank_null);

        // 初始化XListView
        mXListView.setXListViewListener(this);
        mXListView.setPullLoadEnable(true);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        ProgressBarManager.showProgressBar(mView);
        onRefresh();

        // Umeng
        mUmengAction = "0";
        MobclickAgent.onPageStart("StudyRecordFragment");

        // TalkingData
        TCAgent.onPageStart(mActivity, "StudyRecordFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng
        final Map<String,String> um_map = new HashMap<String,String>();
        um_map.put("Action", mUmengAction);
        UmengManager.onEvent(mActivity, "Record", um_map);
        MobclickAgent.onPageEnd("StudyRecordFragment");

        // TalkingData
        TCAgent.onPageEnd(mActivity, "StudyRecordFragment");
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("history_papers".equals(apiName))
            StudyRecordModel.dealHistoryPapersResp(this, response);

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
        mOffset = 0;
        mHistoryPapers = new ArrayList<>();
        mQRequest.getHistoryPapers(mOffset, mCount);
    }

    @Override
    public void onLoadMore() {
        mOffset = mOffset + mCount;
        mQRequest.getHistoryPapers(mOffset, mCount);
    }

    /**
     * 加载结束
     */
    private void setLoadFinish() {
        onLoadFinish();
        StudyRecordModel.showNullImg(this);
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
}
