package com.appublisher.quizbank.common.opencourse.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.BaseActivity;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseModel;
import com.appublisher.quizbank.common.opencourse.model.OpenCourseRequest;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateListResp;
import com.appublisher.quizbank.common.opencourse.netdata.OpenCourseRateResp;
import com.appublisher.quizbank.common.opencourse.netdata.RateListOthersItem;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 公开课单个课程评价页面
 */
public class OpenCourseGradeActivity extends BaseActivity
        implements RequestCallback, BGARefreshLayout.BGARefreshLayoutDelegate{

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
    public RoundedImageView mIvMineAvatar;
    public RatingBar mRbMineRating;
    public TextView mTvMineName;
    public TextView mTvMineComment;
    public TextView mTvMineDate;
    public TextView mTvListEmpty;
    public ListView mLv;
    public BGARefreshLayout mBga;

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
        mIsOpen = getIntent().getStringExtra("is_open");

        // init view
        mLlMine = (LinearLayout) findViewById(R.id.opencourse_grade_mine_ll);
        mBtn = (Button) findViewById(R.id.opencourse_grade_btn);
        mIvMineAvatar = (RoundedImageView) findViewById(R.id.opencourse_grade_mine_avatar);
        mRbMineRating = (RatingBar) findViewById(R.id.opencourse_grade_mine_rb);
        mTvMineName = (TextView) findViewById(R.id.opencourse_grade_mine_username);
        mTvMineComment = (TextView) findViewById(R.id.opencourse_grade_mine_comment);
        mTvMineDate = (TextView) findViewById(R.id.opencourse_grade_mine_date);
        mTvListEmpty = (TextView) findViewById(R.id.opencourse_list_empty);
        mLv = (ListView) findViewById(R.id.opencourse_grade_lv);
        mBga = (BGARefreshLayout) findViewById(R.id.opencourse_grade_bga);

        // int BGARefreshLayout
        mBga.setDelegate(this);
        mBga.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProgressDialogManager.showProgressDialog(this);
        getData(1);
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

            case "rate_class":
                OpenCourseRateResp rateResp =
                        GsonManager.getModel(response, OpenCourseRateResp.class);
                if (rateResp != null && rateResp.getResponse_code() == 1) {
                    OpenCourseModel.closeRateDialog();
                    getData(1);
                    ToastManager.showToast(this, "评价成功");
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

    /**
     * 获取数据
     */
    private void getData(int page) {
        mRequest.getGradeList(0, mClassId, 0, page);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mCurPage = 1;
        getData(mCurPage);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        mCurPage++;
        getData(mCurPage);
        return true;
    }
}
