package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.ProvinceGvAdapter;
import com.appublisher.quizbank.adapter.WholePageListAdapter;
import com.appublisher.quizbank.customui.XListView;
import com.appublisher.quizbank.model.netdata.wholepage.AreaM;
import com.appublisher.quizbank.model.netdata.wholepage.AreaYearResp;
import com.appublisher.quizbank.model.netdata.wholepage.EntirePaperM;
import com.appublisher.quizbank.model.netdata.wholepage.EntirePapersResp;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 真题演练
 */
public class WholePageFragment extends Fragment implements RequestCallback, XListView.IXListViewListener {

    private Activity mActivity;
    private PopupWindow mPwProvince;
    private Request mRequest;
    private Gson mGson;
    private ArrayList<AreaM> mAreas;
    private ArrayList<Integer> mYears;
    private TextView mTvLastProvince;
    private int mCurAreaId;
    private int mCurYear;
    private int mOffset;
    private int mCount;
    private View mMainView;
    private XListView mLvWholePage;
    private ArrayList<EntirePaperM> mEntirePapers;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 成员变量初始化
        mRequest = new Request(mActivity, this);
        mGson = new Gson();
        mCurAreaId = 0;
        mCurYear = 0;
        mOffset = 0;
        mCount = 5;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // view 初始化
        mMainView = inflater.inflate(R.layout.fragment_wholepage, container, false);
        final RelativeLayout rlProvince =
                (RelativeLayout) mMainView.findViewById(R.id.wholepage_province_rl);
        mLvWholePage = (XListView) mMainView.findViewById(R.id.wholepage_xlistview);

        // XListView
        mLvWholePage.setXListViewListener(this);
        mLvWholePage.setPullLoadEnable(true);
        mLvWholePage.setOnItemClickListener(xListViewOnClick);

        // 获取数据
        ProgressBarManager.showProgressBar(mMainView);
        mRequest.getAreaYear();

        // 省份
        rlProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPwProvince == null) {
                    initPwProvince();
                    mPwProvince.showAsDropDown(rlProvince, 0, 2);
                } else if (mPwProvince.isShowing()) {
                    mPwProvince.dismiss();
                } else {
                    mPwProvince.showAsDropDown(rlProvince, 0, 2);
                }
            }
        });

        return mMainView;
    }

    /**
     * 列表item点击事件
     */
    private AdapterView.OnItemClickListener xListViewOnClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    /**
     * 初始化省份菜单
     */
    private void initPwProvince() {
        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(mActivity).inflate(R.layout.wholepage_popup_province, null);

        mPwProvince = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        mPwProvince.setOutsideTouchable(true);
        mPwProvince.setBackgroundDrawable(
                mActivity.getResources().getDrawable(R.color.transparency));

        // 省份 GridView
        if (mAreas != null && mAreas.size() > 0) {
            // 添加全部
            AreaM area = new AreaM();
            area.setArea_id(0);
            area.setName("全部");
            mAreas.add(0, area);

            GridView gvProvince = (GridView) view.findViewById(R.id.wholepage_gv);
            final ProvinceGvAdapter provinceGvAdapter = new ProvinceGvAdapter(mActivity, mAreas);
            gvProvince.setAdapter(provinceGvAdapter);

            gvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tvProvinceItem =
                            (TextView) view.findViewById(R.id.wholepage_gridview_item_tv);

                    tvProvinceItem.setTextColor(Color.WHITE);
                    tvProvinceItem.setBackgroundResource(R.drawable.wholepage_item_all_selected);

                    // 前一个view改变
                    if (mTvLastProvince != null && mTvLastProvince != tvProvinceItem) {
                        mTvLastProvince.setBackgroundResource(R.drawable.wholepage_item_all);
                        mTvLastProvince.setTextColor(
                                getResources().getColor(R.color.setting_text));
                    }

                    mTvLastProvince = tvProvinceItem;

                    // 记录当前的地区id
                    if (mAreas != null && position < mAreas.size()) {
                        AreaM area = mAreas.get(position);

                        if (area != null) {
                            mCurAreaId = area.getArea_id();
                        }
                    }
                }
            });
        }

        TextView tvCancel = (TextView) view.findViewById(R.id.wholepage_province_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.wholepage_province_confirm);

        // 取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPwProvince.dismiss();
            }
        });

        // 确认
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBarManager.showProgressBar(mMainView);
                mRequest.getEntirePapers(mCurAreaId, mCurYear, mOffset, mCount, "false");
                mPwProvince.dismiss();
            }
        });

        mPwProvince.update();
    }

    /**
     * 处理地区和年份回调
     * @param response 地区和年份回调
     */
    private void dealAreaYearResp(JSONObject response) {
        if (response == null) return;

        AreaYearResp areaYearResp = mGson.fromJson(response.toString(), AreaYearResp.class);

        if (areaYearResp == null || areaYearResp.getResponse_code() != 1) return;

        mAreas = areaYearResp.getArea();
        mYears = areaYearResp.getYear();

        mRequest.getEntirePapers(0, 0, 0, 5, "true");
    }

    /**
     * 处理整卷回调
     * @param response 整卷回调
     */
    private void dealEntirePapersResp(JSONObject response) {
        if (response == null) {
            setLoadFinish();
            return;
        }

        EntirePapersResp entirePapersResp =
                mGson.fromJson(response.toString(), EntirePapersResp.class);

        if (entirePapersResp == null || entirePapersResp.getResponse_code() != 1) {
            setLoadFinish();
            return;
        }

        ArrayList<EntirePaperM> newEntirePapers = entirePapersResp.getList();

        if (newEntirePapers == null || newEntirePapers.size() == 0) {
            setLoadFinish();
            return;
        }

        if (mEntirePapers == null) {
            mEntirePapers = newEntirePapers;

        } else {
            int size = newEntirePapers.size();
            for (int i = 0; i < size; i++) {
                mEntirePapers.add(newEntirePapers.get(i));
            }
        }

        WholePageListAdapter wholePageListAdapter =
                new WholePageListAdapter(mActivity, mEntirePapers);
        mLvWholePage.setAdapter(wholePageListAdapter);

        setLoadFinish();
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
        mLvWholePage.stopRefresh();
        mLvWholePage.stopLoadMore();
        mLvWholePage.setRefreshTime(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("area_year".equals(apiName)) dealAreaYearResp(response);

        if ("entire_papers".equals(apiName)) dealEntirePapersResp(response);
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressBarManager.hideProgressBar();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mOffset = 0;
        mEntirePapers = null;
        mRequest.getEntirePapers(mCurAreaId, mCurYear, mOffset, mCount, "false");
    }

    /**
     * 上拉加载更多
     */
    @Override
    public void onLoadMore() {
        mOffset = mOffset + mCount;
        mRequest.getEntirePapers(mCurAreaId, mCurYear, mOffset, mCount, "false");
    }
}
