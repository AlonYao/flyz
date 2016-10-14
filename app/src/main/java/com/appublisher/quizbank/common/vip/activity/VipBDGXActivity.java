package com.appublisher.quizbank.common.vip.activity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipBDGXAdapter;
import com.appublisher.quizbank.common.vip.netdata.VipBDGXResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import com.appublisher.quizbank.customui.CustomViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VipBDGXActivity extends BaseActivity implements RequestCallback {

    private CustomViewPager mViewPager;
    private int exerciseType;
    private int exerciseId;
    private VipRequest mRequest;
    private VipBDGXResp vipBDGXResp;
    private VipBDGXAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_bdgx);
        setToolBar(this);

        exerciseType = getIntent().getIntExtra("exerciseType", -1);
        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        mRequest = new VipRequest(this, this);

        if (exerciseType == 5) {
            setTitle("表达改写");
        } else if (exerciseType == 6) {
            setTitle("语义提炼");
        }

        initViews();

        if (exerciseId != -1) {
            mRequest.getExerciseDetail(exerciseId);
            ProgressDialogManager.showProgressDialog(this);
        }
    }

    public void initViews() {

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        adapter = new VipBDGXAdapter(this, vipBDGXResp);
        adapter.setExerciseType(exerciseType);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (vipBDGXResp != null && vipBDGXResp.getQuestion().get(position).getUser_answer() == null) {
                    mViewPager.setSLIDE_TO_RIGHT(false);
                } else {
                    mViewPager.setSLIDE_TO_RIGHT(true);
                }
                if (vipBDGXResp != null && position != 0 && vipBDGXResp.getQuestion().get(position - 1).getUser_answer() == null)
                    mViewPager.setCurrentItem(position - 1);
            }

            @Override
            public void onPageSelected(int position) {

            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        ProgressDialogManager.closeProgressDialog();

        if (response == null) return;

        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            VipBDGXResp vipBDGXResp = GsonManager.getModel(response, VipBDGXResp.class);
            if (vipBDGXResp.getResponse_code() == 1) {
                this.vipBDGXResp = vipBDGXResp;
                adapter.setVipBDGXResp(vipBDGXResp);
                adapter.notifyDataSetChanged();
            }
        } else if ("submit".equals(apiName)) {
            //提交成功
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                int responseCode = jsonObject.optInt("response_code");
                if (responseCode == 1) {
                    ToastManager.showToast(this, "提交成功");
                    adapter.notifyDataSetChanged();
                } else {
                    ToastManager.showToast(this, "提交失败，请重试");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}