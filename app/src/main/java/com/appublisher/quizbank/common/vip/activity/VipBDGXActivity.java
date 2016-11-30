package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
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

import java.util.HashMap;

/**
 * 表达改写&语义提炼
 */
public class VipBDGXActivity extends BaseActivity implements RequestCallback {

    private CustomViewPager mViewPager;
    private int exerciseType;
    private int exerciseId;
    private VipRequest mRequest;
    private VipBDGXResp vipBDGXResp;
    private VipBDGXAdapter adapter;
    public String mUMEventId;
    private long mUMTimeStamp;
    private boolean mUMIsPostType = false;

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
            // Umeng
            mUMEventId = "Biaoda";
        } else if (exerciseType == 6) {
            setTitle("语义提炼");
            // Umeng
            mUMEventId = "Yuyi";
        }

        initViews();

        if (exerciseId != -1) {
            mRequest.getExerciseDetail(exerciseId);
            showLoading();
        }

        // Umeng
        mUMTimeStamp = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        int dur = (int) ((System.currentTimeMillis() - mUMTimeStamp) / 1000);
        HashMap<String, String> map = new HashMap<>();
        UmengManager.onEventValue(this, mUMEventId, map, dur);
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
        if (response == null) return;

        if (VipRequest.EXERCISE_DETAIL.equals(apiName)) {
            VipBDGXResp vipBDGXResp = GsonManager.getModel(response, VipBDGXResp.class);
            if (vipBDGXResp != null && vipBDGXResp.getResponse_code() == 1) {
                this.vipBDGXResp = vipBDGXResp;
                adapter.setVipBDGXResp(vipBDGXResp);
                adapter.notifyDataSetChanged();

                // Umeng
                if (!mUMIsPostType) {
                    String umStatus;
                    int status = vipBDGXResp.getStatus();
                    if (status == 1 || status == 3 || status == 5) {
                        umStatus = "1";
                    } else {
                        umStatus = "0";
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Type", umStatus);
                    UmengManager.onEvent(this, mUMEventId, map);
                    mUMIsPostType = true;
                }
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
        hideLoading();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

}
