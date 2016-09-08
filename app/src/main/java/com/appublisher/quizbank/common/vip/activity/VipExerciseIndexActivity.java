package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseAdapter;
import com.appublisher.quizbank.common.vip.model.VipExerciseIndexModel;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VipExerciseIndexActivity extends BaseActivity implements RequestCallback {

    public View statusView;
    public View categoryView;
    public View typeView;
    public TextView statusText;
    public TextView categoryText;
    public TextView typeText;
    public ImageView statusArrow;
    public ImageView categoryArrow;
    public ImageView typeArrow;
    public int status_id = -1;
    public int category_id = -1;
    public int type_id = -1;
    private ListView listView;
    public View emptyView;
    private VipRequest mRequest;
    public List<VipExerciseResp.ExercisesBean> list;
    public VipExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_exercise_index);

        setToolBar(this);
        mRequest = new VipRequest(this, this);
        list = new ArrayList<>();
        adapter = new VipExerciseAdapter(this, list);
        initViews();
        setValues();
    }

    public void initViews() {
        statusView = findViewById(R.id.vip_status_rl);
        categoryView = findViewById(R.id.vip_category_rl);
        typeView = findViewById(R.id.vip_type_rl);
        statusText = (TextView) findViewById(R.id.vip_status_tv);
        categoryText = (TextView) findViewById(R.id.vip_category_tv);
        typeText = (TextView) findViewById(R.id.vip_type_tv);
        statusArrow = (ImageView) findViewById(R.id.vip_status_arrow);
        categoryArrow = (ImageView) findViewById(R.id.vip_category_arrow);
        typeArrow = (ImageView) findViewById(R.id.vip_type_arrow);
        listView = (ListView) findViewById(R.id.listView);
        emptyView = findViewById(R.id.empty_view);

        ProgressDialogManager.showProgressDialog(this);
        mRequest.getVipFilter();
        mRequest.getVipExercises(status_id, category_id, type_id);
    }

    public void setValues() {
        listView.setAdapter(adapter);
        statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipExerciseIndexModel.showStatusPop(VipExerciseIndexActivity.this);
            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipExerciseIndexModel.showCategoryPop(VipExerciseIndexActivity.this);
            }
        });

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VipExerciseIndexModel.showTypePop(VipExerciseIndexActivity.this);
            }
        });
    }

    public void refreshData() {
        mRequest.getVipExercises(status_id, category_id, type_id);
        ProgressDialogManager.showProgressDialog(this);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
        if (response == null) return;
        if ("vip_filter".equals(apiName)) {
            VipExerciseIndexModel.dealExerciseFilter(response, this);
        } else if ("vip_exercise".equals(apiName)) {
            VipExerciseIndexModel.dealExercises(response, this);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }


}
