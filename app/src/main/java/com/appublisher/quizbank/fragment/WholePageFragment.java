package com.appublisher.quizbank.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.WholePageGvAdapter;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.ProgressBarManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 真题演练
 */
public class WholePageFragment extends Fragment implements RequestCallback{

    private Activity mActivity;
    private PopupWindow mPwProvince;
    private Request mRequest;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // view 初始化
        View view = inflater.inflate(R.layout.fragment_wholepage, container, false);
        final RelativeLayout rlProvince =
                (RelativeLayout) view.findViewById(R.id.wholepage_province_rl);

        // 获取数据
        ProgressBarManager.showProgressBar(view);
//        mRequest.getEntirePapers(0, 0, 0, 5);
        mRequest.getAreaYear();

        // 省份
        rlProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPwProvince != null && mPwProvince.isShowing()) {
                    mPwProvince.dismiss();
                } else {
                    initPwProvince();
                    mPwProvince.showAsDropDown(rlProvince);
                }
            }
        });

        return view;
    }

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
        GridView gvProvince = (GridView) view.findViewById(R.id.wholepage_gv);
        WholePageGvAdapter wholePageGvAdapter = new WholePageGvAdapter(mActivity);
        gvProvince.setAdapter(wholePageGvAdapter);

        mPwProvince.update();
    }

    /**
     * 处理地区和年份回调
     * @param response 地区和年份回调
     */
    private void dealAreaYearResp(JSONObject response) {
        if (response == null) return;


    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("area_year".equals(apiName)) dealAreaYearResp(response);

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
